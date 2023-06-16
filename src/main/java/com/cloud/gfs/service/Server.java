package com.cloud.gfs.service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    void run() throws IOException{
        String serverIp = "192.168.1.15";
        int port = 4589;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("server started");
        Socket socket = serverSocket.accept();
        System.out.println("server accepted");

        PrintWriter writeToClient =
                new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader readFromClient =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));

        while (true) {
            // read a message from the client
            System.out.println("entered while loop");
            String message = readFromClient.readLine();
            if (message == null) break; // client closed its side of the connection
            if (message.equals("quit")) break; // client sent a quit message

            // prepare a reply, in this case just echoing the message
            String reply = "echo: " + message;

            // write the reply
            writeToClient.println(reply);
            writeToClient.flush(); // important! otherwise the reply may just sit in a buffer, unsent
        }

    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }
}
