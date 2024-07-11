package com.cloud.gfs.DAO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ChunkMessage implements Serializable {
    private String command;
    private String fileName;
    private String fileExtension;
    private int chunkCount;
    private int fileSize;
    private byte[] data;
}
