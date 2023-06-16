package com.cloud.gfs.Controller;

import com.cloud.gfs.service.FileService;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class FileController {

    private FileService fileService;


    @PostMapping
    public List<File> uploadFile(@RequestBody File file){
        try {
            return fileService.uploadFile(file);
        }
        catch(Exception e){
            throw new RuntimeException(e);


        }

    }

}
