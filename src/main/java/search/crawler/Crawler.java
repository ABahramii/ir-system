package search.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Crawler {
    private static final Logger logger = Logger.getLogger("crawler");

    private static final int TOTAL_LINKS_LIMIT = 20;
    private static final Set<String> HEADER_TAGS = new HashSet<>(Arrays.asList("h1", "h2", "h3", "h4", "h5", "h6"));
    private static final Set<String> PLAIN_TEXT_TAGS = new HashSet<>(Arrays.asList("p", "span", "pre", "td", "li"));
    private static final Pattern USEFUL_LINK_PATTERN = Pattern.compile("https://medium.com/@.+/");

    private final String seedPageUri;
    private final Properties prop = new Properties();

    private int addedLinksNum = 1;
    private int currentLinkNum = 0;

    public Crawler(String seedPageUri) {
        this.seedPageUri = seedPageUri;
    }

    public void start() throws IOException {
        boolean canAddLinkToQueue = true;
        Queue<String> frontier = new LinkedList<>();
        frontier.add(seedPageUri);

        while (!frontier.isEmpty()) {
            String uri = frontier.poll();
            currentLinkNum++;

            Document document = getDocumentFromUri(uri);
            if (document == null) {
                continue;
            }

            Page page = createPage(document.getAllElements());

            String docName = "D" + currentLinkNum;
            createDocumentFile(docName, page.getTerms());
            setProperties(uri, docName, page.getTitle());

            Set<String> extractedLinks = page.getExtractedLinks();
            if (canAddLinkToQueue) {
                int total = addedLinksNum + extractedLinks.size();
                if (total <= TOTAL_LINKS_LIMIT) {
                    frontier.addAll(extractedLinks);
                    addedLinksNum += extractedLinks.size();
                } else {
                    canAddLinkToQueue = false;
                    int remain = TOTAL_LINKS_LIMIT - addedLinksNum;
                    frontier.addAll(extractedLinks.stream().limit(remain).toList());
                }
            }
            logger.log(Level.INFO, "{0} parsed.\n", docName);
        }

        try (FileOutputStream out = new FileOutputStream("./z_docs/doc.properties")) {
            prop.store(out, "");
        }
    }

    private void setProperties(String uri, String docName, String title) {
        prop.setProperty(docName + ".title", title);
        prop.setProperty("D" + currentLinkNum + ".uri", uri);
    }

    private void createDocumentFile(String docName, List<String> terms) {
        new Thread(() -> DocumentCreator.create(docName, terms)).start();
    }

    private Document getDocumentFromUri(String uri) {
        Document document;
        try {
            Connection connect = Jsoup.connect(uri);
            connect.timeout(10 * 1000);
            document = connect.get();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "exception in connection to uri");
            addedLinksNum--;
            currentLinkNum--;
            return null;
        }
        return document;
    }

    private Page createPage(Elements docElements) {
        String title = "";
        Set<String> extractedLinks = new HashSet<>();
        List<String> terms = new ArrayList<>(1000);

        for (Element element : docElements) {
            if (PLAIN_TEXT_TAGS.contains(element.tagName().toLowerCase()) || HEADER_TAGS.contains(element.tagName().toLowerCase())) {
                String text = element.text();
                terms.add(text);
            } else if (extractedLinks.size() < 5 && element.tagName().equalsIgnoreCase("a")) {
                String link = element.attr("abs:href");
                if (USEFUL_LINK_PATTERN.matcher(link).find()) {
                    extractedLinks.add(link);
                }
            } else if (element.tagName().equalsIgnoreCase("title")) {
                title = element.text();
            }
        }
        return new Page(title, extractedLinks, terms);
    }
}
