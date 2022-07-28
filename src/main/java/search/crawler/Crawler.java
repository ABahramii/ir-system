package search.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Crawler {
    private static final int totalLinksLimit = 20;
    private static final Set<String> headerTags = new HashSet<>(Arrays.asList("h1", "h2", "h3", "h4", "h5", "h6"));
    private static final Set<String> plainTextTags = new HashSet<>(Arrays.asList("p", "span", "pre", "td", "li"));
    private static final Pattern usefulLinkPattern = Pattern.compile("https://medium.com/@.+/");
    private final String seedPageUri;
    private final Properties prop = new Properties();

    public Crawler(String seedPageUri) {
        this.seedPageUri = seedPageUri;
    }

    public void start() throws IOException {
        long f = System.currentTimeMillis();

        Queue<String> frontier = new LinkedList<>();
        frontier.add(seedPageUri);

        int addedLinksNum = 1;
        int currentLinkNum = 0;
        boolean canAddLinkToQueue = true;

        while (!frontier.isEmpty()) {
            String uri = frontier.poll();
            currentLinkNum++;

            Document document;

            try {
                Connection connect = Jsoup.connect(uri);
                connect.timeout(10 * 1000);
                document = connect.get();
            } catch (Exception e) {
                System.out.println("exception in connection to uri");
                addedLinksNum--;
                currentLinkNum--;
                continue;
            }

            String title = "";
            Set<String> extractedLinks = new HashSet<>();
            List<String> terms = new ArrayList<>(1000);

            for (Element element : document.getAllElements()) {
                if (plainTextTags.contains(element.tagName().toLowerCase())) {
                    String text = element.text();
                    terms.add(text);
                } else if (headerTags.contains(element.tagName().toLowerCase())) {
                    String text = element.text();
                    terms.add(text);
                } else if (extractedLinks.size() < 5 && element.tagName().equalsIgnoreCase("a")) {
                    String link = element.attr("abs:href");
                    if (usefulLinkPattern.matcher(link).find()) {
                        extractedLinks.add(link);
                    }
                } else if (element.tagName().equalsIgnoreCase("title")) {
                    title = element.text();
                }
            }

            String docName = "D" + currentLinkNum;
            new Thread(() -> DocumentCreator.create(docName, terms)).start();

            prop.setProperty(docName + ".title", title);
            prop.setProperty("D" + currentLinkNum + ".uri", uri);

            if (canAddLinkToQueue) {
                int total = addedLinksNum + extractedLinks.size();
                if (total <= totalLinksLimit) {
                    frontier.addAll(extractedLinks);
                    addedLinksNum += extractedLinks.size();
                } else {
                    canAddLinkToQueue = false;
                    int remain = totalLinksLimit - addedLinksNum;
                    frontier.addAll(extractedLinks.stream().limit(remain).toList());
                }
            }
            System.out.println(docName + " parsed.\n");
        }

//        System.out.println(extractedLinks.size());

        prop.store(new FileOutputStream("./z_docs/doc.properties"), "");
        long total = System.currentTimeMillis() - f;
        System.out.println("Time: " + total);
    }
}
