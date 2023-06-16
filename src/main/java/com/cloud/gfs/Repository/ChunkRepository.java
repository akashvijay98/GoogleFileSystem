package com.cloud.gfs.Repository;

import com.cloud.gfs.DAO.ChunkDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChunkRepository extends JpaRepository<ChunkDAO, UUID> {
}
