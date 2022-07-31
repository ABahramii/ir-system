package search.crawler;

import java.io.IOException;

public class App {

    private static final String SEED_PAGE_URI =
            "https://medium.com/analytics-vidhya/introduction-to-information-retrieval-rank-retrieval-tf-idf-using-a-search-engine-in-nlp-9c988d9f0051#:~:text=What%20do%20you%20mean%20by,(usually%20stored%20on%20computers).";

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler(SEED_PAGE_URI);
        crawler.start();
    }
}
