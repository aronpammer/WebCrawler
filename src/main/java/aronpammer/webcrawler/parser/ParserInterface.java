package aronpammer.webcrawler.parser;

import org.jsoup.nodes.Document;

public interface ParserInterface {
    String[] getUrls(Document document);
}
