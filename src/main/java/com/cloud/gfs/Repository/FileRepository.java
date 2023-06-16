package com.cloud.gfs.Repository;

import com.cloud.gfs.DAO.FileDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<FileDAO, UUID> {


}
