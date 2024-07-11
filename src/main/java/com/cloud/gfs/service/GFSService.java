package com.cloud.gfs.service;

import Util.KafkaConfig;
import com.cloud.gfs.DAO.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GFSService {

    private static final String CREATE_TOPIC = "create-chunk";
    private static final String READ_TOPIC = "read-chunk";
    private static final String RESPONSE_TOPIC = "response-topic";

    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    @Autowired
    public GFSService(KafkaConfig kafkaConfig) {
        this.consumer = kafkaConfig.createConsumer(RESPONSE_TOPIC);
        this.producer = kafkaConfig.createProducer();
    }


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


            FileDAO file = new FileDAO(fileName, fileSize); // check

            // save file info in file table
            FileDAO fileResponse = saveFileDetails(file);

            while ((dataRead=in.read(buffer)) != -1 ) {
                ChunkMessage chunkMessage = new ChunkMessage();
                chunkMessage.setCommand("create");
                chunkMessage.setFileName(fileName);
                chunkMessage.setFileExtension(fileExtension);
                chunkMessage.setChunkCount(chunkCount);
                chunkMessage.setFileSize(fileSize);
                chunkMessage.setData(Arrays.copyOf(buffer, dataRead));

                // Serialize object to JSON using Jackson ObjectMapper
                ObjectMapper mapper = new ObjectMapper();
                String jsonMessage = mapper.writeValueAsString(chunkMessage);

                // Calculate partition based on custom logic (example: chunkCount % 2)
                serverNumber = chunkCount % 2;

                // Create Kafka producer record with JSON string and partition number
                ProducerRecord<String, String> record = new ProducerRecord<>(CREATE_TOPIC, serverNumber, null, jsonMessage);
                producer.send(record);
                System.out.println("Chunk count "+chunkCount);
                String chunkName = fileName + Integer.toString(chunkCount);
                int chunkSize = buffer.length;

                // Creating chunk dto object
//                ChunkDAO chunk = new ChunkDAO(chunkName, chunkSize, chunkCount);
//                ChunkDAO chunkResponse =  chunkService.addChunk(chunk);
//
//                FileMetaDataDAO fileMetaData = new FileMetaDataDAO(fileResponse.getId(), chunkResponse.getId(), chunkCount, servers[serverNumber], ports[serverNumber]);
//                metaService.addFileMetaData(fileMetaData);
//                // Method updates file metadata
//                saveFileMetaDataDetails(fileMetaData);

                //increment the chunkCount after the chunk gets successfuly stored in the server
                chunkCount++;




                //new FileMetaDataDAO(fileResponse.getId(), chunkResponse.getId(), chunkCount, servers[serverNumber], ports[serverNumber]);



                // Wait for response
                ConsumerRecords<String, String> records = consumer.poll(100);


                for (ConsumerRecord<String, String> responseRecord : records) {
                    if (responseRecord.value().equals("success")) {
                        System.out.println("success");
                    } else {
                        System.out.println("failure");
                    }
                }
            }


        }
        catch (Exception e){
            log.error("there is an exception", e.getStackTrace());
            e.printStackTrace();
        }
        return "successfully uploaded";

    }







    public ChunkDAO saveChunkInfo(ChunkDAO chunk){
    // save chunk info on chunk table
    ChunkDAO chunkResponse =  chunkService.addChunk(chunk);

    return chunkResponse;

}
public FileDAO saveFileDetails(FileDAO file){
        // save file info
        FileDAO fileResponse =  fileService.addFile(file);
         return fileResponse;

}

public void saveFileMetaDataDetails(FileMetaDataDAO fileMetaData) {
    // save fileMetaData info
    // updating the fileMetaData table
    metaService.addFileMetaData(fileMetaData);
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

