package com.cloud.gfs.DAO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "FileMetaData")

@Getter
@Setter
public class FileMetaData {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "fileId", referencedColumnName = "id")
    private FileDAO fileDAO;

    @ManyToOne
    @JoinColumn(name = "chunkId", referencedColumnName = "id")
    private ChunkDAO chunkDAO;

    private Integer serverId;

    FileMetaData(){}

    FileMetaData(UUID id, FileDAO fileDAO, ChunkDAO chunkDAO, Integer serverId ){
        this.id = id;
        this.fileDAO = fileDAO;
        this.chunkDAO = chunkDAO;
        this.serverId = serverId;
    }



}
