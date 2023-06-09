package com.cloud.gfs.service;

import com.cloud.gfs.DAO.ChunkDAO;
import com.cloud.gfs.DAO.FileDAO;
import com.cloud.gfs.DAO.FileMetaDataDAO;
import com.cloud.gfs.DAO.Message;
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
import java.util.Arrays;
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


    @Value("${app.gfs.servers}")
    String[] servers;

    @Value("${app.gfs.ports}")
    Integer[] ports;

    String fileName = "FileReadJava";
    String fileExtension = ".txt";

    int fileSize = 8192;

    @Value("${app.gfs.chunk-size}")
    private int maxChunkSize; // the maximum chunk size is 4Kb in size



    public String uploadFile(File largeFile, String fileName, String fileExtension) throws IOException {

        Socket socket0;





        List<File> list = new ArrayList<>();

        try (InputStream in = Files.newInputStream(largeFile.toPath())) {

            int serverNumber= 0;
            int chunkCount = 1;
            final byte[] buffer = new byte[maxChunkSize];
            int dataRead;


            FileDAO file = new FileDAO(fileName, fileSize);
            FileDAO fileResponse =  fileService.addFile(file);



            while ((dataRead=in.read(buffer,0, maxChunkSize)) != -1 ) {


                try {

                    socket0 = new Socket(servers[0], ports[0]);
                    //socket0.setSoTimeout(100000);
                    socket0.setKeepAlive(true);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }


                OutputStream outToServer;
                DataOutputStream writeToServer;

                InputStream inFromServer;
                DataInputStream readFromServer;

                outToServer = socket0.getOutputStream();
                inFromServer = socket0.getInputStream();


                writeToServer = new DataOutputStream(outToServer);
                System.out.println("connected to server"+writeToServer);

                readFromServer = new DataInputStream(inFromServer);


                String message = "";
                message+= "create";
                message+=",";
                message+= fileName;
                message+=",";
                message+=fileExtension;
                message+=",";
                message+=Integer.toString(chunkCount);
                message+=",";
                message+= Integer.toString(fileSize);



               // String[] messageData = message.split(",");


                //  System.out.println("wrote to serer byte array"+ Arrays.toString(buffer));

                byte[] upbytes = new byte[dataRead];
                System.arraycopy(buffer, 0, upbytes, 0, dataRead);
                System.out.println("data read size =="+dataRead);


                String chunkName = fileName + Integer.toString(chunkCount);
                int chunkSize = buffer.length;


                try {


                   writeToServer.writeUTF(message);
                    Thread.sleep(200);

                    writeToServer.write(upbytes, 0, dataRead);
                    writeToServer.flush();

                    String response = readFromServer.readUTF();
                    System.out.println("response from server after uploading chunk"+response);
                    if(!response.equals("success")){
                        System.out.println("there seems to be a problem");
                        break;
                    }

                    Thread.sleep(200);

                }
                catch(UTFDataFormatException e){
                    e.printStackTrace();

                }
              System.out.println("chunk no "+chunkCount);



                ChunkDAO chunk = new ChunkDAO(chunkName, chunkSize, chunkCount);
                ChunkDAO chunkResponse =  chunkService.addChunk(chunk);

                FileMetaDataDAO fileMetaData = new FileMetaDataDAO(fileResponse.getId(), chunkResponse.getId(), chunkCount, servers[serverNumber]);
                metaService.addFileMetaData(fileMetaData);

                chunkCount++;

                serverNumber = chunkCount % 3;

//                list.add(fileChunk);

//            }

            }

        }
        catch (Exception e){
            System.out.println("there is an exception"+e);
            e.printStackTrace();
        }
        return "successfully uploaded";

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


//            Socket socket1;
//
//            {
//                try {
//                    socket1 = new Socket(servers[1], ports[1]);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            Socket socket2;
//
//            {
//                try {
//                    socket2 = new Socket(servers[2], ports[2]);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//


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
            try {
                socket0 = new Socket(servers[0], ports[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String serverIp = fileMetaDataList.stream().filter(data -> data.getChunkIndex() == chunkIndex).findFirst().map(FileMetaDataDAO :: getServerId).orElse("Default Property");;

           // if(serverIp.equals("192.168.1.12")) {
                outToServer = socket0.getOutputStream();
                inFromServer = socket0.getInputStream();
           // }
//
//            else if (serverIp.equals("192.168.1.15")) {
//                outToServer = socket1.getOutputStream();
//                inFromServer = socket1.getInputStream();
//            }
//
//            else {
//
//                outToServer = socket2.getOutputStream();
//                inFromServer = socket2.getInputStream();
//            }
            writeToServer =  new DataOutputStream(outToServer);
            readFromServer = new DataInputStream(inFromServer);

            writeToServer.writeUTF("read");


            int bytes=0;

            byte[] buffer = new byte[8192];

            String chunkName = fileName + Integer.toString(chunkIndex) + fileExtension;
            writeToServer.writeUTF(chunkName);

            bytes = readFromServer.read(buffer);

                    System.out.println("bytes==="+bytes);
                    fos.write(buffer, 0 , bytes);


        }

        System.out.println("sucess===============");

    }



    }

