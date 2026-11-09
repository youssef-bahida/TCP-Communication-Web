# 🌐 TCP Communication Web Application

### 🚀 A Java-based multi-client TCP communication system with a modern web interface.

<p align="center">

  **Java · Spring Boot · TCP/IP · WebSocket · HTML · CSS · JavaScript**

</p>

---

## 📖 About the Project

**TCP Communication Web** is a web-based application designed to demonstrate TCP client-server communication through an interactive interface.

The application allows users to start a TCP server and create multiple independent clients directly from their browser. Each client can connect, disconnect, send numerical messages, and receive responses from the server in real time.

The project combines Java networking with modern web technologies to provide an accessible and intuitive demonstration of multi-client communication.

## ✨ Features

- 🖥️ **Web Interface:** Modern and responsive user interface.
- 🌐 **TCP Server:** Start and stop the TCP server.
- 👥 **Multiple Clients:** Dynamically create independent client instances.
- 🔌 **Client Connection:** Connect and disconnect each client independently.
- 📤 **Message Sending:** Send numerical messages to the server.
- 📥 **Real-Time Responses:** Receive server responses instantly.
- 🔄 **WebSocket Communication:** Bridge the browser and Java backend.
- 📱 **Responsive Design:** Access the interface from desktop and mobile devices.
- ☁️ **Online Deployment:** Host the frontend and backend using cloud services.

## 🏗️ System Architecture

The application uses a three-layer communication architecture:

```text
              🌍 USER BROWSER
                     |
                     |
             HTML / CSS / JS
                     |
                     | WebSocket
                     |
             ☕ JAVA BACKEND
                Spring Boot
                     |
                     |
                TCP SERVER
                 Port 5001
                     |
          +----------+----------+
          |          |          |
       Client 1   Client 2   Client 3
          |          |          |
          +----------+----------+
                     |
              Server Response
```

### 🔹 Communication Process

1. The user opens the web application.
2. The browser establishes a WebSocket connection with the Java backend.
3. The backend manages the TCP server.
4. Users create independent client instances.
5. Each client establishes its own TCP socket connection.
6. Clients send messages to the TCP server.
7. The server processes the messages and returns responses.
8. The backend forwards responses to the corresponding browser client.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| ☕ Java 17 | Backend programming |
| 🍃 Spring Boot | Backend application |
| 🔌 TCP/IP | Client-server communication |
| 🔄 WebSocket | Real-time browser communication |
| 🌐 HTML5 | Web structure |
| 🎨 CSS3 | Interface styling |
| ⚡ JavaScript | Frontend interactions |
| 📦 Maven | Dependency management |
| 🐳 Docker | Backend containerization |
| ☁️ Render | Backend hosting |
| 🐙 GitHub Pages | Frontend hosting |

## 📂 Project Structure

```text
TCP-Communication-Web/
│
├── index.html
├── README.md
├── .gitignore
│
└── backend/
    ├── Dockerfile
    ├── pom.xml
    │
    └── src/
        └── main/
            ├── java/
            │   └── com/youssef/tcpweb/
            │       ├── TcpWebApplication.java
            │       ├── WebSocketConfig.java
            │       ├── TcpServerManager.java
            │       ├── TcpWebSocketHandler.java
            │       └── HealthController.java
            │
            └── resources/
                └── application.properties
```

## ⚙️ Local Installation

### 1. Requirements

Install the following tools:

- Java JDK 17 or later compatible with the project.
- Apache Maven.
- Python 3.
- Git.

### 2. Clone the repository

```bash
git clone https://github.com/youssef-bahida/TCP-Communication-Web.git

cd TCP-Communication-Web
```

### 3. Start the Java backend

Open a terminal:

```bash
cd backend
mvn spring-boot:run
```

The backend will run locally on:

`http://localhost:8080`

### 4. Start the web interface

Open a second terminal:

```bash
cd ~/TCP-Communication-Web

python3 -m http.server 5500
```

Open your browser:

`http://localhost:5500`

## 🧪 Example Usage

The TCP server processes numerical messages and returns their doubled values.

| Client | Sent Message | Server Response |
|---|---:|---:|
| Client 1 | 10 | 20 |
| Client 2 | 25 | 50 |
| Client 3 | 100 | 200 |

Each client maintains its own TCP connection and receives its own responses.

## ☁️ Online Deployment

The project can be deployed using two hosting services.

### 🌍 Frontend — GitHub Pages

Repository:

https://github.com/youssef-bahida/TCP-Communication-Web

Enable GitHub Pages through:

**Settings → Pages → Deploy from a branch → main → / (root)**

### ☕ Backend — Render

Deploy the Java backend as a Docker Web Service.

Recommended configuration:

| Setting | Value |
|---|---|
| Runtime | Docker |
| Branch | main |
| Root Directory | backend |
| Instance | Free |

After deployment, configure the frontend to use your Render backend URL instead of `localhost`.

> ⚠️ The free hosting plan may sleep when inactive, so the first connection can take some time.

## 🎯 Learning Objectives

This project demonstrates practical concepts in:

- TCP/IP socket programming.
- Client-server architecture.
- Multi-client communication.
- Java networking and concurrency.
- WebSocket communication.
- Backend and frontend integration.
- Cloud deployment and hosting.

## 👨‍💻 Author

**Youssef Bahida**

🎓 Computer Science Student  
💻 Software Development & Networking

- 🐙 GitHub: https://github.com/youssef-bahida
- 📧 Email: youssefbahida18@gmail.com

---

<p align="center">

  ⭐ **If you find this project useful, consider giving it a star!**

  Made with ❤️ using Java and Web Technologies.

</p>
