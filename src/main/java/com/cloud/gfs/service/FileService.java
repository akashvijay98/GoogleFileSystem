package com.cloud.gfs.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    private String TEMP_DIRECTORY = "C:/Users/ajayv/Documents/";

    String fileName = "FileReadJava";
    String fileExtension = ".txt";
    private int maxChunkSize = 4096; // the maximum chunk size is 4Kb in size

    public List<File> uploadFile(File largeFile) throws IOException {

        String hostname = "192.168.1.15";
        int port = 4589;
        Socket socket = new Socket(hostname, port);

        InputStream inFromServer = socket.getInputStream();
        OutputStream outToServer = socket.getOutputStream();

        DataOutputStream writeToServer = new DataOutputStream(outToServer);


        writeToServer.writeUTF("create");
        writeToServer.writeUTF(fileName);
        writeToServer.writeUTF(fileExtension);



//        PrintWriter writeToServer =
//                new PrintWriter(new OutputStreamWriter(outToServer));
        BufferedReader readFromServer =
                new BufferedReader(new InputStreamReader(inFromServer));

        List<File> list = new ArrayList<>();
        try (InputStream in = Files.newInputStream(largeFile.toPath())) {
            final byte[] buffer = new byte[maxChunkSize];
            int dataRead = in.read(buffer);
            while (dataRead > -1) {
                //File fileChunk = stageFile(buffer, dataRead);
                //send to server socket

                writeToServer.write(buffer, 0, dataRead);

//                list.add(fileChunk);
                dataRead = in.read(buffer);
//            }
                // writeToServer.println("hey hello /n hey ehe");
                writeToServer.flush();
            }
            return list;
        }


    }

    public void testChunk() throws IOException {
        String sourcepath = "/home/akvj/chunkfile1";


        String path = "C:/Users/ajayv/";
        String hostname = "192.168.1.15";
        int portNo = 4589;

        Socket socket = new Socket(hostname, portNo);

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        DataOutputStream outToServer = new DataOutputStream(outputStream);
        DataInputStream inFromServer = new DataInputStream(inputStream);

        outToServer.writeUTF("read");
        int bytes;

        byte[] buffer = new byte[4096];


        while(true){

            File outputFile = File.createTempFile("javaInputfile",".txt", new File(path));
            FileOutputStream fos = new FileOutputStream(outputFile);
            while( (bytes = inFromServer.read(buffer)) != 1 ){
                System.out.println("bytes==="+bytes);
                fos.write(buffer, 0 , bytes);
                bytes = inFromServer.read(buffer);

            }
            socket.close();

        }

        }

//    public File stageFile(byte[] buffer, int length) throws IOException {
//        File outPutFile = File.createTempFile("mankathafile",null, new File(TEMP_DIRECTORY));

    // check if the file is created
//        if (outPutFile.exists()) {
//
//            // the file is created
//            // as the function returned true
//            System.out.println("Temp File created: "
//                    + outPutFile.getAbsolutePath());
//        }
//
//        else {

    // display the file cannot be created
    // as the function returned false

//            System.out.println("Temp File cannot be created: "
//                    + outPutFile.getAbsolutePath());
    // }

//        try(FileOutputStream fos = new FileOutputStream(outPutFile)) {
//            fos.write(buffer, 0, length);
//        }
//        return outPutFile;
//    }



    }

