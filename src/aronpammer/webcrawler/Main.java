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
       
        try {
            CrawlerConfig crawlerConfig = new CrawlerConfig("http://gocardless.com/", new HashAddressStorer(), new QuickParser(), "", 1);
            Crawler crawler = new Crawler(crawlerConfig);
            crawler.retreiveSiteAdresses();
        } catch (WrongInitialWebPageException e) {
            System.out.println("The initial url was not pointing to an html");
        } catch (MalformedURLException e) {
            System.out.println("Malformed initial url");
        }

    }
}
