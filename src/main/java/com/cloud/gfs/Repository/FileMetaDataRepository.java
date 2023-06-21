package com.cloud.gfs.Repository;

import com.cloud.gfs.DAO.FileMetaDataDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetaDataDAO, UUID> {

    @Query(value = "SELECT * FROM gfs1.public.file_meta_data  WHERE file_id  = ?1", nativeQuery = true)
    List<FileMetaDataDAO> findByFileId(UUID fileId);
}
