package search.engine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Indexer {
    private static final Logger logger = Logger.getLogger("indexer");

    private final Path indexDirectory;
    private final Path docsDirectory;
    private Properties props;

    public Indexer(String indexDirectory, String docsDirectory) {
        this.indexDirectory = Paths.get(indexDirectory);
        this.docsDirectory = Paths.get(docsDirectory);
    }

    public void createTfIdfIndexing() {
        try {
            loadDocProperties();

            Directory dir = FSDirectory.open(indexDirectory);
            Analyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            // add tf-idf support
            iwc.setSimilarity(new ClassicSimilarity());

            try (IndexWriter writer = new IndexWriter(dir, iwc)) {
                indexDocs(writer, docsDirectory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void indexDocs(final IndexWriter writer, Path path) throws IOException {
        Files.walkFileTree(
                path,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                        try {
                            if (path.getFileName().toString().startsWith("D")) {
                                indexDoc(writer, path);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return FileVisitResult.CONTINUE;
                    }
                }
        );
    }

    private void indexDoc(IndexWriter writer, Path filePath) throws IOException {
        try (InputStream stream = Files.newInputStream(filePath)) {
            Document doc = new Document();

            doc.add(new StringField("filePath", filePath.toString(), Field.Store.YES));

            String fileName = filePath.getFileName().toString();
            doc.add(new StringField("title", props.getProperty(fileName + ".title"), Field.Store.YES));
            doc.add(new StringField("uri", props.getProperty(fileName + ".uri"), Field.Store.YES));

            doc.add(
                    new TextField(
                            "contents",
                            new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    )
            );

            logger.log(Level.INFO, "adding {0}", filePath);
            writer.addDocument(doc);
        }
    }

    private void loadDocProperties() throws IOException {
        props = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(docsDirectory.toString() + "/doc.properties")) {
            props.load(fileInputStream);
        }
    }
}
