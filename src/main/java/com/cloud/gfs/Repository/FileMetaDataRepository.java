package com.cloud.gfs.Repository;

import com.cloud.gfs.DAO.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileMetaDataRepository extends JpaRepository<FileMetaData, UUID> {
}
