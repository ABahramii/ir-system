package search.crawler;

import java.io.IOException;

public class App {

    private static String DOCS_DIRECTORY = "./z_docs/";
//    private static String SEED_PAGE_URI = "https://blog.jetbrains.com/idea/2022/05/java-annotated-monthly-may-2022/";
    private static String SEED_PAGE_URI = "https://medium.com/analytics-vidhya/introduction-to-information-retrieval-rank-retrieval-tf-idf-using-a-search-engine-in-nlp-9c988d9f0051#:~:text=What%20do%20you%20mean%20by,(usually%20stored%20on%20computers).";

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler(SEED_PAGE_URI);
        crawler.start();

//        testDocsValidation();
    }

    /*private static void testDocsValidation() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(DOCS_DIRECTORY + "doc.properties"));

        for (int i = 1; i <= 20; i++) {
            String uri = (String) props.get("D" + i + ".uri");
            String title = (String) props.get("D" + i + ".title");

            Document document;

            try {
                Connection connect = Jsoup.connect(uri);
                connect.timeout(10 * 1000);
                document = connect.get();
            } catch (Exception e) {
                System.out.println("exception in connection to uri");
                continue;
            }

            Elements element = document.getElementsByTag("title");
            if (element.text().equals(title)) {
                System.out.println("D" + i + ": OK");
            }

        }
    }*/
}
