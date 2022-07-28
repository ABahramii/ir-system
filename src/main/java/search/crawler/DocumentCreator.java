package search.crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DocumentCreator {

    private static String DOCS_DIRECTORY = "./z_docs/";

    public static void create(String docName, List<String> list) {
        try {
            Files.write(Paths.get(DOCS_DIRECTORY + docName), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
