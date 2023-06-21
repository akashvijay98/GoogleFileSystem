package com.cloud.gfs.Repository;

import com.cloud.gfs.DAO.ChunkDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChunkRepository extends JpaRepository<ChunkDAO, UUID> {
}
