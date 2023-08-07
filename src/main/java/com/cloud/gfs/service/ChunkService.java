package com.cloud.gfs.service;

import com.cloud.gfs.DAO.ChunkDAO;
import com.cloud.gfs.Repository.ChunkRepository;
import org.springframework.stereotype.Service;

@Service
public class ChunkService {
    ChunkRepository chunkRepository;

    public  ChunkService(ChunkRepository _chunkRepository){
        chunkRepository = _chunkRepository;

    }

    public ChunkDAO addChunk(ChunkDAO chunk ){
        ChunkDAO chunkResponse = chunkRepository.save(chunk);
        return chunkResponse;
    }
}
