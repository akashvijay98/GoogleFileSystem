package com.cloud.gfs.Controller;

import com.cloud.gfs.service.LuceneIndexingService;
import com.cloud.gfs.service.LuceneSearchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LuceneIndexingController {

    @Autowired
    LuceneIndexingService luceneIndexingService;



    @PostMapping("/gfs/luceneIndexing")
    public void runIndexer(){
        try {
            luceneIndexingService.indexData();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}

