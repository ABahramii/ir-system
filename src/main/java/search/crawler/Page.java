package search.crawler;

import java.util.List;
import java.util.Set;

public class Page {
    private final String title;
    private final Set<String> extractedLinks;
    private final List<String> terms;

    public Page(String title, Set<String> extractedLinks, List<String> terms) {
        this.title = title;
        this.extractedLinks = extractedLinks;
        this.terms = terms;
    }

    public String getTitle() {
        return title;
    }

    public Set<String> getExtractedLinks() {
        return extractedLinks;
    }

    public List<String> getTerms() {
        return terms;
    }
}
