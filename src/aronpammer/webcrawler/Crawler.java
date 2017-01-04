package aronpammer.webcrawler;

import org.jsoup.Jsoup;

public class Crawler {
    private static Crawler singleton = new Crawler();

    private Crawler(){}

    public static Crawler getInstance()
    {
        return singleton;
    }

    public void crawlSite(String url)
    {

    }
}
