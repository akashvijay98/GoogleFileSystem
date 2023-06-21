package com.cloud.gfs.service;

import com.cloud.gfs.DAO.ChunkDAO;
import com.cloud.gfs.DAO.FileDAO;
import com.cloud.gfs.DAO.FileMetaDataDAO;
import com.cloud.gfs.Repository.ChunkRepository;
import com.cloud.gfs.Repository.FileMetaDataRepository;
import com.cloud.gfs.Repository.FileRepository;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
public class MetaService {

    FileMetaDataRepository fileMetaDataRepository;

    public MetaService(ChunkRepository _chunkRepository, FileRepository _fileRepository, FileMetaDataRepository _fileMetaDataRepository){

        fileMetaDataRepository = _fileMetaDataRepository;
    }

    public  void addFileMetaData(FileMetaDataDAO fileMetaData){
        fileMetaDataRepository.save(fileMetaData);
    }



}
