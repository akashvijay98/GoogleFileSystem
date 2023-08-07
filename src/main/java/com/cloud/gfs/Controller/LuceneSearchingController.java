package com.cloud.gfs.Controller;

import com.cloud.gfs.DAO.FileDAO;
import com.cloud.gfs.service.LuceneSearchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
public class LuceneSearchingController {

    @Autowired
    LuceneSearchingService luceneSearchingService;

    @GetMapping("/luceneSearching")
    public ResponseEntity<List<FileDAO>> search(@RequestParam String query){

        List<FileDAO> searchResult = luceneSearchingService.search(query);
        return ResponseEntity.ok(searchResult);

    }
}
