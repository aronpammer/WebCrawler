package aronpammer.webcrawler.logic;

import aronpammer.webcrawler.address.AddressStorer;
import aronpammer.webcrawler.misc.CrawlerConfig;
import aronpammer.webcrawler.address.Address;

public class Crawler {
    private AddressStorer addressStorer;
    private CrawlerConfig crawlerConfig;

    public Crawler(CrawlerConfig crawlerConfig, AddressStorer addressStorer)
    {
        this.crawlerConfig = crawlerConfig;
        this.addressStorer = addressStorer;
    }

    public Address[] retreiveSiteAdresses()
    {
        crawlSite(crawlerConfig.getBaseUrl());
    }

    private void crawlSite(String url)
    {

    }
}
