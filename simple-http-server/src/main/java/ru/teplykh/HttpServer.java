package ru.teplykh;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started at http://localhost:8080");

        while (true) {
            StringBuilder requestBuilder = new StringBuilder();
            Socket socket = serverSocket.accept();
            System.out.println("Client connected");

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            BufferedWriter writer = new BufferedWriter(new
                    OutputStreamWriter(socket.getOutputStream()));

            while (!reader.ready());

            while (reader.ready()) {
                System.out.println(reader.readLine());
            }

            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write("Content-Type: text/html; charset=UTF-8\r\n");
            writer.write("\r\n");
            writer.write("<h1>Hello from server!</h1>");
            writer.write("<p>It works!</p>");
            writer.flush();

            socket.close();
        }
    }
}