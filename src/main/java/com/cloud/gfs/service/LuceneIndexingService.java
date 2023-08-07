package com.cloud.gfs.service;

import com.cloud.gfs.DAO.FileDAO;
import com.cloud.gfs.Repository.FileRepository;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class LuceneIndexingService {
    private final FileRepository fileRepository;
    private final Directory indexDirectory;


    public LuceneIndexingService(FileRepository fileRepository) throws IOException {
        this.fileRepository = fileRepository;
        this.indexDirectory = FSDirectory.open(Paths.get("index"));
    }

    public void indexData() {

        List<FileDAO> files = fileRepository.findAll();

        try (IndexWriter indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(new StandardAnalyzer()))) {
            for (FileDAO file : files) {
                Document luceneDocument = new Document();
                luceneDocument.add(new TextField("name", file.getName(), Field.Store.YES));
                indexWriter.addDocument(luceneDocument);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
