package aronpammer.webcrawler.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuickParser implements ParserInterface {
    @Override
    public List<URL> getUrls(Document document) {
        Elements links = document.select("a[href]");
        Elements media = document.select("[src]");
        Elements imports = document.select("link[href]");
        String[] urls = new String[links.size() + media.size() + imports.size()];
        int counter = 0;
        for (Element link : links) {
            urls[counter++] = link.attr("abs:href");
        }
        for (Element src : media) {
            urls[counter++] = src.attr("abs:src");
        }
        for (Element link : imports) {
            urls[counter++] = link.attr("abs:href");
        }

        return getUrls(urls);
    }

    public ArrayList<URL> getUrls(String[] urls)
    {
        ArrayList<URL> urlList = new ArrayList<>();
        for (String url : urls)
        {
            try
            {
                URL realUrl = new URL(url);
                realUrl.toURI(); // url format check

                urlList.add(realUrl);
            } catch (MalformedURLException | URISyntaxException e) {
                //ignore
            }
        }
        return  urlList;
    }
}
