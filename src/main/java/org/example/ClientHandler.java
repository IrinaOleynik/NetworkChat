package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    protected Socket clientSocket;
    protected BufferedReader reader;
    protected PrintWriter writer;
    protected String nickName;
    Logger logger = Logger.getInstance();
    private final String EXIT = "/exit";
    private final String GET_SETTINGS = "/settings";
    protected boolean isRunning;

    public ClientHandler(Socket client) throws IOException {
        this.clientSocket = client;
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        isRunning = true;
        setNickName();
        try {
            while (isRunning) {
                String entry = null;
                if (reader.ready()) {
                    entry = reader.readLine();
                    logger.log("Сервер: Получено сообщение от пользователя " + nickName + ": " + entry);
                    switch (entry) {
                        case EXIT:
                            stopHandler();
                            break;
                        case GET_SETTINGS:
                            writer.println("Ваш клиентский порт: " + clientSocket.getPort());
                            writer.println("IP адрес сервера: " + clientSocket.getLocalAddress());
                            writer.println("Порт сервера: " + clientSocket.getLocalPort());
                            writer.println("Ваш никнейм: " + nickName);
                        default:
                            send(nickName + ": " + entry);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.interrupt();
        }
    }
    public void send(String msg) {
        for (ClientHandler clientHandler : Server.connectionList) {
            if (!clientHandler.equals(this)) {
                clientHandler.writer.println(msg);
            }
        }
    }
    private void setNickName() {
        writer.println("Введите имя");
        try {
            nickName = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writer.println(nickName + ", добро пожаловать в чат");
        logger.log("Сервер: Пользователь " + nickName + " вошел в чат");
        send(nickName + " вошел в чат");
    }

    public void stopHandler() {
        isRunning = false;
        try {
            send(nickName + " покинул чат");
            Server.connectionList.remove(this);
            reader.close();
            writer.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
