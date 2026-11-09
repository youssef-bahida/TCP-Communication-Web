package com.youssef.tcpweb;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TcpServerManager {

    private static final int PORT = 5001;

    private volatile ServerSocket serverSocket;
    private volatile boolean running = false;

    private final Set<Socket> clients = ConcurrentHashMap.newKeySet();

    public synchronized void start() throws IOException {
        if (running) {
            return;
        }

        ServerSocket socket = new ServerSocket();
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(
                InetAddress.getLoopbackAddress(), PORT));

        serverSocket = socket;
        running = true;

        Thread acceptThread = new Thread(this::acceptClients);
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    private void acceptClients() {
        while (running) {
            try {
                Socket client = serverSocket.accept();
                clients.add(client);

                Thread clientThread = new Thread(() -> handleClient(client));
                clientThread.setDaemon(true);
                clientThread.start();

            } catch (IOException e) {
                if (running) {
                    System.err.println("Accept error: " + e.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket client) {
        try (
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            PrintWriter output = new PrintWriter(
                    client.getOutputStream(), true)
        ) {
            String line;

            while ((line = input.readLine()) != null) {
                try {
                    double number = Double.parseDouble(line.trim());
                    output.println("RESULT:" + (number * 2));
                } catch (NumberFormatException e) {
                    output.println("ERROR:Please send a valid number.");
                }
            }

        } catch (IOException e) {
            System.out.println("TCP client disconnected.");
        } finally {
            clients.remove(client);
            try {
                client.close();
            } catch (IOException ignored) {
            }
        }
    }

    public synchronized void stop() {
        running = false;

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }

        for (Socket client : clients) {
            try {
                client.close();
            } catch (IOException ignored) {
            }
        }

        clients.clear();
        serverSocket = null;
    }

    public boolean isRunning() {
        return running;
    }
}
