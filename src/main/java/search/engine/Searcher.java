package search.engine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Searcher {
    private static final String SEARCH_FIELD = "contents";
    private static final int MAX_RESULT_NUMBER = 20;
    private final Path indexDirectory;

    public Searcher(String indexDirectory) {
        this.indexDirectory = Paths.get(indexDirectory);
    }

    public void search(String searchText) {
        try {
            DirectoryReader reader = DirectoryReader.open(FSDirectory.open(indexDirectory));
            IndexSearcher searcher = new IndexSearcher(reader);
            // set similarity to tf-idf
            searcher.setSimilarity(new ClassicSimilarity());

            Analyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser(SEARCH_FIELD, analyzer);
            Query query = parser.parse(searchText);

            TopDocs results = searcher.search(query, MAX_RESULT_NUMBER);
            ScoreDoc[] hits = results.scoreDocs;

            printResult(searcher, hits);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printResult(IndexSearcher searcher, ScoreDoc[] hits) throws IOException {
        for (int i = 0; i < hits.length; i++) {
            Document doc = searcher.doc(hits[i].doc);
            String path = doc.get("filePath");

            if (path != null) {
                System.out.println((i + 1) + ". " + path);
                String title = doc.get("title");
                String uri = doc.get("uri");
//                if (title != null) {
                    System.out.println("   Title: " + title);
                    System.out.println("   URI: " + uri);
//                }
            }
            System.out.println("\n");
        }
    }
}
