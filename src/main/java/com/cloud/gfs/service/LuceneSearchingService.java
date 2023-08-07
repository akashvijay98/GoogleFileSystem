package com.cloud.gfs.service;

import com.cloud.gfs.DAO.FileDAO;
import org.apache.el.parser.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class LuceneSearchingService {
    private final Directory indexDirectory;

    @Autowired
    public LuceneSearchingService() throws IOException {
        this.indexDirectory = FSDirectory.open(Paths.get("index"));
    }

    public List<FileDAO> search(String query) {
        List<FileDAO> results = new ArrayList<>();

        try (DirectoryReader indexReader = DirectoryReader.open(indexDirectory)) {
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            QueryParser queryParser = new QueryParser("name", new StandardAnalyzer());

            Query luceneQuery = queryParser.parse(query);
            TopDocs topDocs = indexSearcher.search(luceneQuery, 10);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document luceneDocument = indexSearcher.doc(scoreDoc.doc);

                FileDAO file = new FileDAO();
                file.setName(luceneDocument.get("name"));

                results.add(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            throw new RuntimeException(e);
        }

        return results;
    }
}
