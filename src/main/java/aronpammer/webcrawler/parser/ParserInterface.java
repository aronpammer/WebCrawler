package aronpammer.webcrawler.parser;

import org.jsoup.nodes.Document;

public interface ParserInterface {
    /**
     * Extracts the urls from the document and returns them
     * @param document The document to parse
     * @return String[] that contains the urls
     */
    String[] getUrls(Document document);
}
