package com.cloud.gfs.DAO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "FileMetaData")

@Getter
@Setter
public class FileMetaDataDAO {

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

    private String serverId;

    private Integer chunkIndex;

    FileMetaDataDAO(){}

    FileMetaDataDAO(UUID id, FileDAO fileDAO, ChunkDAO chunkDAO, Integer chunkIndex, String serverId ){
        this.id = id;
        this.fileDAO = fileDAO;
        this.chunkDAO = chunkDAO;
        this.chunkIndex = chunkIndex;
        this.serverId = serverId;
    }


    public FileMetaDataDAO(UUID fileId, UUID chunkId, Integer chunkIndex, String serverId) {
        this.fileDAO = new FileDAO();
        this.fileDAO.setId(fileId);

        this.chunkDAO = new ChunkDAO();
        this.chunkDAO.setId(chunkId);

        this.chunkIndex = chunkIndex;
        this.serverId = serverId;

    }

}
