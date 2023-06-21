package com.cloud.gfs.Controller;

import com.cloud.gfs.DAO.GFSRequestDAO;
import com.cloud.gfs.service.GFSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
public class GFSController {

    @Autowired
    private GFSService gfsService;

    GFSController(GFSService gfsService){
        this.gfsService = gfsService;
    }

    @GetMapping("/gfs")
    public  void readFile(@RequestParam("fileId") UUID fileId,
                          @RequestParam("fileName") String fileName,
                          @RequestParam("fileExtension") String fileExtension) throws IOException {
        gfsService.getFile(fileId, fileName, fileExtension);
    }

    @PostMapping("/gfs")
    public List<File> uploadFile(@RequestBody GFSRequestDAO gfsRequest){
        try {
            return gfsService.uploadFile(gfsRequest.getFile(),gfsRequest.getFileName(), gfsRequest.getFileExtension());
        }
        catch(Exception e){
            throw new RuntimeException(e);


        }

    }

}
