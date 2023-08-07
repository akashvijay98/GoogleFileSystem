package com.cloud.gfs.DAO;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class GFSRequestDAO {
    private File file;
    private String fileName;
    private String fileExtension;
    private Integer fileSize;
}
