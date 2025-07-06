package ru.teplykh;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {
    private final static String SERVER_STARTED = "Server started at http://localhost:8080";
    private final static String CLIENT_CONNECTED = "Client connected";
    private final static String FOLDER_WITH_FILES = "simple-http-server\\static";
    private final static String FILE_NOT_FOUND_RESPONSE = "FileNotFound.html";
    private final static String METHODS_NOT_SUPPORTED_RESPONSE = "RequestTypeIsNotSupported.html";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println(SERVER_STARTED);

        while (true) {
            StringBuilder requestBuilder = new StringBuilder();
            Socket socket = serverSocket.accept();
            System.out.println(CLIENT_CONNECTED);

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new
                    OutputStreamWriter(outputStream));

            while (!reader.ready());

            while (reader.ready()) {
                String line = reader.readLine();
                requestBuilder.append(line).append("\r\n");
                System.out.println(line);
            }

            String[] splitRequestBuilder = requestBuilder.toString().split("\n");

            String header = splitRequestBuilder[0];
            String[] splitHeader = header.split(" ");

            String requestType = splitHeader[0];

            String link = splitHeader[1];

            Path path;
            String responseHeader;

            if (!requestType.equals("GET")) {
                path = Paths.get(FOLDER_WITH_FILES + "\\" + METHODS_NOT_SUPPORTED_RESPONSE);
                responseHeader = "HTTP/1.1 405 Method Not Allowed\r\n";
            } else {
                Path neededPath = Paths.get(FOLDER_WITH_FILES, link);
                if (Files.exists(neededPath)) {
                    path = neededPath;
                    responseHeader = "HTTP/1.1 200 OK\r\n";
                } else {
                    path = Paths.get(FOLDER_WITH_FILES + "\\" + FILE_NOT_FOUND_RESPONSE);
                    responseHeader = "HTTP/1.1 404 Not Found\r\n";
                }
            }

            writer.write(responseHeader);
            writer.write("Content-Type: text/html; charset=UTF-8\r\n");
            writer.write("Content-Length: " + Files.size(path) + "\r\n");
            writer.write("\r\n");
            writer.flush();

            try (InputStream fileInputStream = Files.newInputStream(path)) {
                fileInputStream.transferTo(outputStream);
            }

            socket.close();
        }
    }
}

