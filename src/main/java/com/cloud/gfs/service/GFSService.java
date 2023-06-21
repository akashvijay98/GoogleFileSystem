package com.cloud.gfs.service;

import com.cloud.gfs.DAO.ChunkDAO;
import com.cloud.gfs.DAO.FileDAO;
import com.cloud.gfs.DAO.FileMetaDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GFSService {

    @Value("${app.gfs.temp.directory}")
    private String TEMP_DIRECTORY;

    @Autowired
    private MetaService metaService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ChunkService chunkService;


   // AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    @Value("${app.gfs.servers}")
    String[] servers;

    @Value("${app.gfs.ports}")
    Integer[] ports;

   // @Qualifier("socket1")




    //@Qualifier("socket2")


    //@Qualifier("socket0")
    String fileName = "FileReadJava";
    String fileExtension = ".txt";

    int fileSize = 1000;

    @Value("${app.gfs.chunk-size}")
    private int maxChunkSize; // the maximum chunk size is 4Kb in size




    public List<File> uploadFile(File largeFile, String fileName, String fileExtension) throws IOException {

        Socket socket0;

        {
            try {
                socket0 = new Socket(servers[0], ports[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Socket socket1;

        {
            try {
                socket1 = new Socket(servers[1], ports[1]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Socket socket2;

        {
            try {
                socket2 = new Socket(servers[2], ports[2]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



        List<File> list = new ArrayList<>();
        try (InputStream in = Files.newInputStream(largeFile.toPath())) {

            int serverNumber= 0;
            int chunkCount = 1;
            final byte[] buffer = new byte[maxChunkSize];
            int dataRead = in.read(buffer);


            FileDAO file = new FileDAO(fileName, fileSize);

            FileDAO fileResponse =  fileService.addFile(file);
            while (dataRead > -1) {

                String hostname = servers[serverNumber];
                int port = ports[serverNumber];


               // InputStream inFromServer = socket.getInputStream();

                OutputStream outToServer;
                DataOutputStream writeToServer;

                InputStream inFromServer;
                DataInputStream readFromServer;

                if(serverNumber == 0){
                    outToServer = socket0.getOutputStream();
                    inFromServer = socket0.getInputStream();


                }

                else if(serverNumber == 1) {

                    outToServer = socket1.getOutputStream();
                    inFromServer = socket1.getInputStream();

                }
                else {
                    outToServer = socket2.getOutputStream();
                    inFromServer = socket2.getInputStream();


                }

                writeToServer = new DataOutputStream(outToServer);
                readFromServer = new DataInputStream(inFromServer);

                String chunkName = fileName + Integer.toString(chunkCount);
                int chunkSize = buffer.length;

                writeToServer.writeUTF("create");
                writeToServer.writeUTF(fileName);
                writeToServer.writeUTF(fileExtension);



                writeToServer.writeInt(chunkCount);
                writeToServer.write(buffer, 0, dataRead);

               //String respons = readFromServer.readUTF();
            //    System.out.println("response from server"+respons);



                ChunkDAO chunk = new ChunkDAO(chunkName, chunkSize, chunkCount);
                ChunkDAO chunkResponse =  chunkService.addChunk(chunk);

                FileMetaDataDAO fileMetaData = new FileMetaDataDAO(fileResponse.getId(), chunkResponse.getId(), chunkCount, servers[serverNumber]);
                metaService.addFileMetaData(fileMetaData);


                chunkCount++;

                serverNumber = chunkCount % 3;

//                list.add(fileChunk);
                dataRead = in.read(buffer);

//            }

            }
            return list;
        }


    }

    public void ReadFile(String fileName, String fileExtension, UUID fileId){






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

        public void getFile(UUID fileId, String fileName, String fileExtension) throws IOException {

            Socket socket0;

            {
                try {
                    socket0 = new Socket(servers[0], ports[0]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Socket socket1;

            {
                try {
                    socket1 = new Socket(servers[1], ports[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Socket socket2;

            {
                try {
                    socket2 = new Socket(servers[2], ports[2]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }



            List<FileMetaDataDAO> fileMetaDataList = metaService.fileMetaDataRepository.findByFileId(fileId);

        int[] chunkIndexes = fileMetaDataList.stream().mapToInt(FileMetaDataDAO :: getChunkIndex).sorted().toArray();

        OutputStream outToServer;
        InputStream inFromServer;

        DataOutputStream writeToServer;
        DataInputStream readFromServer;

        String path = "C:/Users/ajayv/Documents";

        File outputFile = File.createTempFile(fileName,fileExtension, new File(path));
        FileOutputStream fos = new FileOutputStream(outputFile);

        for(int chunkIndex : chunkIndexes){

            String serverIp = fileMetaDataList.stream().filter(data -> data.getChunkIndex() == chunkIndex).findFirst().map(FileMetaDataDAO :: getServerId).orElse("Default Property");;

            if(serverIp.equals("192.168.1.14")) {
                outToServer = socket0.getOutputStream();
                inFromServer = socket0.getInputStream();
            }

            else if (serverIp.equals("192.168.1.15")) {
                outToServer = socket1.getOutputStream();
                inFromServer = socket1.getInputStream();
            }

            else {

                outToServer = socket2.getOutputStream();
                inFromServer = socket2.getInputStream();
            }
            writeToServer =  new DataOutputStream(outToServer);
            readFromServer = new DataInputStream(inFromServer);

            writeToServer.writeUTF("read");


            int bytes;

            byte[] buffer = new byte[4096];

            String chunkName = fileName + Integer.toString(chunkIndex) + fileExtension;
            writeToServer.writeUTF(chunkName);

            bytes = readFromServer.read(buffer);

                    System.out.println("bytes==="+bytes);
                    fos.write(buffer, 0 , bytes);







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

