package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {
    protected static ArrayList<ClientHandler> connectionList = new ArrayList<>();
    private static final String SETTINGS = "Settings.txt";
    private static Logger logger = Logger.getInstance();
    private static int port;
    protected static ServerSocket serverSocket;
    protected boolean isRunning;


    public Server() {
        try (BufferedReader in = new BufferedReader(new FileReader(SETTINGS))) {
            port = Integer.parseInt(in.readLine());
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startServer() {
        logger.log("Сервер запущен");
        isRunning = true;
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                addNewConnection(clientHandler);
                clientHandler.start();
                logger.log("Сервер: подключен новый пользователь");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            stopServer();
        }
    }

    public void addNewConnection(ClientHandler clientHandler) {
        connectionList.add(clientHandler);
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (ClientHandler handler : connectionList) {
                handler.stopHandler();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
