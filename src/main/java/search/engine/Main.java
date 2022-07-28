package search.engine;

import java.util.Scanner;

public class Main {
    private static final String INDEX_DIRECTORY = "./z_index";
    private static final String DOCS_DIRECTORY = "./z_docs";

    public static void main(String[] args) {
        long f = System.currentTimeMillis();

        System.out.println("start indexing...");
        Indexer indexer = new Indexer(INDEX_DIRECTORY, DOCS_DIRECTORY);
        indexer.createTfIdfIndexing();
        System.out.println("Docs indexed.");


        System.out.println("====================================");

        Scanner sc = new Scanner(System.in);
        System.out.print("query: ");
        String searchText = sc.nextLine();

        System.out.println("Start searching: " + searchText);

        System.out.println("\nResult: ");
        Searcher searcher = new Searcher(INDEX_DIRECTORY);
        searcher.search(searchText);

        System.out.println("Time: " + (System.currentTimeMillis() - f));
    }
}
