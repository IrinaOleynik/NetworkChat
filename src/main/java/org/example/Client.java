package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SETTINGS = "Settings.txt";
    private static String host;
    private static int port;
    protected static Socket client;
    protected Scanner scanner;
    protected BufferedReader reader;
    protected PrintWriter writer;
    private String nickName;
    Logger logger = Logger.getInstance();
    private final String EXIT = "/exit";


    public Client() {
        try (BufferedReader br = new BufferedReader(new FileReader(SETTINGS))) {
            port = Integer.parseInt(br.readLine());
            host = br.readLine();
            client = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);
            scanner = new Scanner(System.in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    // Конструктор для юнит тестов
    public Client(Socket socket) throws IOException {
            this.client = socket;
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);
            scanner = new Scanner(System.in);
    }
    public void startChat() {
        this.setNickNameName();
        
        Thread sender = new Thread(() -> {
            while (true) {
                String msg = scanner.nextLine();
                writer.println(msg);
                logger.log("Пользователь " + nickName + " отправил сообщение: " + msg);
                if (msg.equals(EXIT)) {
                    break;
                }
            }
            this.exit();
        });
        sender.start();

        Thread listener = new Thread(() -> {
            while (!client.isClosed()) {
                try {
                    if (reader.ready()) {
                        String msg = reader.readLine();
                        System.out.println(msg);
                        logger.log("Пользователь " + nickName + " получил сообщение от " + msg);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        listener.start();
    }

    public void exit(){
        try {
            writer.close();
            reader.close();
            client.close();
            scanner.close();
            logger.log(nickName + " покинул чат");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void setNickNameName() {
        try {
            System.out.println(reader.readLine());
            nickName = scanner.nextLine();
            writer.println(nickName);
            System.out.println(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



