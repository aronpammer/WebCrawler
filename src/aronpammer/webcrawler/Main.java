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
        if(args.length < 3)
        {
            System.out.println("Missing parameters");
            return;
        }
        try {
            String website = args[0];
            String userAgent = args[1];
            int maxDepth = Integer.parseInt(args[2]);
            CrawlerConfig crawlerConfig = new CrawlerConfig(website, new HashAddressStorer(), new QuickParser(), userAgent, maxDepth);
            Crawler crawler = new Crawler(crawlerConfig);
            String result = crawler.retreiveSiteAdresses();
            System.out.println(result);
        } catch (WrongInitialWebPageException e) {
            System.out.println("The initial url was not pointing to an html");
        } catch (MalformedURLException e) {
            System.out.println("Malformed initial url");
        } catch (NumberFormatException e) {
            System.out.println("Wrong maximum depth parameter");
        }

    }
}
