package aronpammer.webcrawler.parser;

import org.jsoup.nodes.Document;

import java.net.URL;

public interface ParserInterface {
    java.util.List<URL> getUrls(Document document);
}
