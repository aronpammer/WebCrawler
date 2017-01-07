package aronpammer.webcrawler.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class QuickParser implements ParserInterface {
    @Override
    public String[] getUrls(Document document) {
        Elements links = document.select("a[href]");
        Elements media = document.select("[src]");
        Elements imports = document.select("link[href]");
        String[] urls = new String[links.size() + media.size() + imports.size()];
        int counter = 0;
        for (Element link : links) {
            urls[counter++] = link.absUrl("href");
        }
        for (Element src : media) {
            urls[counter++] = src.absUrl("src");
        }
        for (Element link : imports) {
            urls[counter++] = link.absUrl("href");
        }

        return urls;
    }
}
