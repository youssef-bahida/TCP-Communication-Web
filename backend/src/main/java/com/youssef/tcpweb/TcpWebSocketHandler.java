package com.youssef.tcpweb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TcpWebSocketHandler extends TextWebSocketHandler {

    private final TcpServerManager serverManager;
    private final ObjectMapper mapper = new ObjectMapper();

    // One browser session can own multiple independent TCP clients.
    private final Map<String, Map<String, Socket>> clients =
            new ConcurrentHashMap<>();

    @Value("${app.access-code:}")
    private String accessCode;

    public TcpWebSocketHandler(TcpServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        clients.put(session.getId(), new ConcurrentHashMap<>());
        send(session, "CONNECTED", "Connected", null);
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session, TextMessage message) throws Exception {

        JsonNode data = mapper.readTree(message.getPayload());
        String type = data.path("type").asText("");
        String clientId = data.path("clientId").asText("");

        if (!type.equals("PING") && !isAuthorized(data)) {
            send(session, "ERROR", "Invalid access code", clientId);
            return;
        }

        switch (type) {
            case "PING" ->
                    send(session, "PONG", "Connection alive", null);

            case "START_SERVER" -> {
                try {
                    serverManager.start();
                    send(session, "SERVER_STARTED",
                            "TCP server started", null);
                } catch (Exception e) {
                    send(session, "ERROR", e.getMessage(), null);
                }
            }

            case "STOP_SERVER" -> {
                serverManager.stop();
                send(session, "SERVER_STOPPED",
                        "TCP server stopped", null);
            }

            case "CONNECT" -> connectClient(session, clientId);

            case "DISCONNECT" -> disconnectClient(session, clientId);

            case "SEND" -> sendToClient(
                    session, clientId, data.path("value").asText(""));

            default ->
                    send(session, "ERROR", "Unknown command", clientId);
        }
    }

    private boolean isAuthorized(JsonNode data) {
        return accessCode == null || accessCode.isBlank()
                || accessCode.equals(data.path("accessCode").asText(""));
    }

    private void connectClient(WebSocketSession session, String clientId) {
        Map<String, Socket> sessionClients = clients.get(session.getId());

        if (sessionClients == null || clientId.isBlank()) {
            send(session, "ERROR", "Invalid client", clientId);
            return;
        }

        if (sessionClients.containsKey(clientId)) {
            send(session, "CLIENT_CONNECTED", "Already connected", clientId);
            return;
        }

        if (!serverManager.isRunning()) {
            send(session, "ERROR", "Start the server first", clientId);
            return;
        }

        try {
            Socket socket = new Socket("127.0.0.1", 5001);
            sessionClients.put(clientId, socket);

            send(session, "CLIENT_CONNECTED",
                    "TCP client connected", clientId);

            Thread reader = new Thread(
                    () -> readResponses(session, clientId, socket));
            reader.setDaemon(true);
            reader.start();

        } catch (IOException e) {
            send(session, "ERROR", "Connection failed: " + e.getMessage(),
                    clientId);
        }
    }

    private void sendToClient(
            WebSocketSession session, String clientId, String value) {

        Map<String, Socket> sessionClients = clients.get(session.getId());
        Socket socket = sessionClients == null
                ? null : sessionClients.get(clientId);

        if (socket == null || socket.isClosed()) {
            send(session, "ERROR", "Connect this client first", clientId);
            return;
        }

        try {
            PrintWriter output =
                    new PrintWriter(socket.getOutputStream(), true);
            output.println(value);

            send(session, "MESSAGE_SENT", "Sent: " + value, clientId);

        } catch (IOException e) {
            send(session, "ERROR", "Could not send message", clientId);
        }
    }

    private void readResponses(
            WebSocketSession session, String clientId, Socket socket) {

        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String response;
            while ((response = input.readLine()) != null) {
                send(session, "TCP_MESSAGE", response, clientId);
            }

        } catch (IOException ignored) {
        } finally {
            Map<String, Socket> sessionClients = clients.get(session.getId());

            boolean removed = sessionClients != null
                    && sessionClients.remove(clientId, socket);

            try {
                socket.close();
            } catch (IOException ignored) {
            }

            if (removed) {
                send(session, "CLIENT_DISCONNECTED",
                        "TCP client disconnected", clientId);
            }
        }
    }

    private void disconnectClient(WebSocketSession session, String clientId) {
        Map<String, Socket> sessionClients = clients.get(session.getId());
        Socket socket = sessionClients == null
                ? null : sessionClients.remove(clientId);

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }

        send(session, "CLIENT_DISCONNECTED",
                "TCP client disconnected", clientId);
    }

    private void send(
            WebSocketSession session, String type,
            String message, String clientId) {

        if (session == null || !session.isOpen()) return;

        try {
            Map<String, Object> response = new ConcurrentHashMap<>();
            response.put("type", type);
            response.put("message", message);
            if (clientId != null) response.put("clientId", clientId);

            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(
                            mapper.writeValueAsString(response)));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void disconnectAll(WebSocketSession session) {
        Map<String, Socket> sessionClients = clients.remove(session.getId());

        if (sessionClients != null) {
            for (Map.Entry<String, Socket> entry : sessionClients.entrySet()) {
                try {
                    entry.getValue().close();
                } catch (IOException ignored) {
                }

                send(session, "CLIENT_DISCONNECTED",
                        "TCP client disconnected", entry.getKey());
            }
        }
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session, CloseStatus status) {
        disconnectAll(session);
    }

    @Override
    public void handleTransportError(
            WebSocketSession session, Throwable exception) {
        disconnectAll(session);
    }
}
