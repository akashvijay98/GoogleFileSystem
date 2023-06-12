package com.cloud.gfs.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private String TEMP_DIRECTORY = "C:/Users/ajayv/Documents/";

    public  List<File> fileWriter(File largeFile, int maxChunkSize) throws IOException {
        List<File> list = new ArrayList<>();
        try (InputStream in = Files.newInputStream(largeFile.toPath())) {
            final byte[] buffer = new byte[maxChunkSize];
            int dataRead = in.read(buffer);
            while (dataRead > -1) {
                File fileChunk = stageFile(buffer, dataRead);
                list.add(fileChunk);
                dataRead = in.read(buffer);
            }
        }
        return list;
    }

    public File stageFile(byte[] buffer, int length) throws IOException {
        File outPutFile = File.createTempFile("mankathafile", ".mp4", new File(TEMP_DIRECTORY));

        // check if the file is created
        if (outPutFile.exists()) {

            // the file is created
            // as the function returned true
            System.out.println("Temp File created: "
                    + outPutFile.getAbsolutePath());
        }

        else {

            // display the file cannot be created
            // as the function returned false
            System.out.println("Temp File cannot be created: "
                    + outPutFile.getAbsolutePath());
        }
        try(FileOutputStream fos = new FileOutputStream(outPutFile)) {
            fos.write(buffer, 0, length);
        }
        return outPutFile;
    }

    public File join(List<File> list) throws IOException {
        File outPutFile = File.createTempFile("joinedfile", ".mp4", new File(TEMP_DIRECTORY));
        FileOutputStream fos = new FileOutputStream(outPutFile);
        for (File file : list) {
            Files.copy(file.toPath(), fos);
        }
        fos.close();
        return outPutFile;
    }

}
