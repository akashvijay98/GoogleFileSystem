package com.cloud.gfs.Repository;

import com.cloud.gfs.DAO.FileDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<FileDAO, UUID> {


}
