package aronpammer.webcrawler;

import aronpammer.webcrawler.exception.WrongInitialWebPageException;
import aronpammer.webcrawler.logic.Crawler;
import aronpammer.webcrawler.misc.CrawlerConfig;
import aronpammer.webcrawler.parser.QuickParser;
import aronpammer.webcrawler.store.hash.HashAddressStorer;

import java.net.MalformedURLException;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        /*HashMap<String, String> asd = new HashMap<>();
        String aaa = "asd";
        String bbb = "asd";
        asd.put(aaa, "asd");
        System.out.println(asd.get(bbb));
        if(true)
            return;*/
        try {
            CrawlerConfig crawlerConfig = new CrawlerConfig("http://example.org/", new HashAddressStorer(), new QuickParser(), "");
            Crawler crawler = new Crawler(crawlerConfig);
            crawler.retreiveSiteAdresses();
        } catch (WrongInitialWebPageException e) {
            System.out.println("The initial url was not pointing to an html");
        } catch (MalformedURLException e) {
            System.out.println("Malformed initial url");
        }

    }
}
