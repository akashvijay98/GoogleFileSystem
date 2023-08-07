package com.cloud.gfs.service;

import com.cloud.gfs.DAO.ChunkDAO;
import com.cloud.gfs.DAO.FileDAO;
import com.cloud.gfs.DAO.FileMetaDataDAO;
import com.cloud.gfs.DAO.Message;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    @Value("${app.gfs.chunk-size}")
    private int maxChunkSize; // the maximum chunk size is 4Kb in size


    public String uploadFile(File largeFile, String fileName, String fileExtension, Integer fileSize) throws IOException {

        Socket socket0;

        List<File> list = new ArrayList<>(); // check

        try (InputStream in = Files.newInputStream(largeFile.toPath())) {

            int serverNumber= 0;
            int chunkCount = 1;
            byte[] buffer = new byte[maxChunkSize];
            int dataRead;

            //map
            FileDAO file = new FileDAO(fileName, fileSize); // check

            FileDAO fileResponse =  fileService.addFile(file);

            while ((dataRead=in.read(buffer)) != -1 ) {

                try {
                    socket0 = new Socket(servers[serverNumber], ports[serverNumber]);
                    socket0.setKeepAlive(true);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

                //
                OutputStream outToServer;
                DataOutputStream writeToServer;

                //
                InputStream inFromServer;
                DataInputStream readFromServer;

                outToServer = socket0.getOutputStream();
                inFromServer = socket0.getInputStream();


                writeToServer = new DataOutputStream(outToServer);

                // ADD LOG
                log.info("connected to server",writeToServer);

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

                //
                byte[] upbytes = new byte[dataRead];

                // log
                System.arraycopy(buffer, 0, upbytes, 0, dataRead);
                log.info("data read size"+dataRead);

                //
                String chunkName = fileName + Integer.toString(chunkCount);
                int chunkSize = buffer.length;

                try {

                   writeToServer.writeUTF(message);
                    Thread.sleep(200);

                    writeToServer.write(buffer, 0, dataRead);
                    writeToServer.flush();

                    buffer = new byte[maxChunkSize];

                    String response = readFromServer.readUTF();
                    log.info("response from server after uploading chunk"+response);


                    if(!response.equals("success")){

                        log.error("the server is not responding");
                        break;
                    }

                    socket0.close();

                    Thread.sleep(200);
                }
                catch(UTFDataFormatException e){
                    log.error("Data format is incorrect", e.getStackTrace());
                }

                log.info("Chunk count ", chunkCount);

                ChunkDAO chunk = new ChunkDAO(chunkName, chunkSize, chunkCount);
                ChunkDAO chunkResponse =  chunkService.addChunk(chunk);

                FileMetaDataDAO fileMetaData = new FileMetaDataDAO(fileResponse.getId(), chunkResponse.getId(), chunkCount, servers[serverNumber], ports[serverNumber]);
                metaService.addFileMetaData(fileMetaData);

                chunkCount++;
                serverNumber = chunkCount % 2;

            }
        }
        catch (Exception e){
            log.error("there is an exception", e.getStackTrace());
            e.printStackTrace();
        }
        return "successfully uploaded";

    }


        public void getFile(UUID fileId, String fileName, String fileExtension) throws IOException
        {
            Socket socket0;

            List<FileMetaDataDAO> fileMetaDataList = metaService.fileMetaDataRepository.findByFileId(fileId);

            int[] chunkIndexes = fileMetaDataList.stream().mapToInt(FileMetaDataDAO :: getChunkIndex).sorted().toArray();

            OutputStream outToServer;
            InputStream inFromServer;

            DataOutputStream writeToServer;
            DataInputStream readFromServer;

            String path = "./media";

            File outputFile = File.createTempFile(fileName,fileExtension, new File(path));
            FileOutputStream fos = new FileOutputStream(outputFile);

            for(int chunkIndex : chunkIndexes){

                try {
                    String serverIp = fileMetaDataList.stream().filter(data -> data.getChunkIndex() == chunkIndex).findFirst().map(FileMetaDataDAO :: getServerId).orElse("Default Property");
                    Integer port = fileMetaDataList.stream().filter(data -> data.getChunkIndex() == chunkIndex).findFirst().map(FileMetaDataDAO :: getPort).orElse(0);
                    socket0 = new Socket(serverIp, port);
                }

                catch (IOException e) {
                    throw new RuntimeException(e);
                }

                outToServer = socket0.getOutputStream();
                inFromServer = socket0.getInputStream();

                writeToServer =  new DataOutputStream(outToServer);
                readFromServer = new DataInputStream(inFromServer);

                String chunkName = fileName + Integer.toString(chunkIndex) + fileExtension;

                String message = "";
                message+= "read";
                message+=",";
                message+= chunkName;

                int bytes;

                byte[] buffer = new byte[8192];
                int totalBytesRead = 0;

                writeToServer.writeUTF(message);

                while( totalBytesRead<maxChunkSize &&(bytes = readFromServer.read(buffer,totalBytesRead,maxChunkSize-totalBytesRead))!=-1) {

                    log.info("bytes=", bytes);
                    fos.write(buffer, totalBytesRead, bytes);
                    totalBytesRead += bytes;

                }
            }
            log.info("successfully uploaded");
        }
    }

