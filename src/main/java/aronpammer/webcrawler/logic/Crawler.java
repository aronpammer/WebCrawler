package aronpammer.webcrawler.logic;

import aronpammer.webcrawler.exception.WrongInitialWebPageException;
import aronpammer.webcrawler.misc.CrawlerConfig;
import aronpammer.webcrawler.misc.SiteInformation;
import aronpammer.webcrawler.store.AddressStorerInterface;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Crawler {
    private static final Logger logger = Logger.getLogger("Crawler");

    private CrawlerConfig crawlerConfig;

    public Crawler(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }

    public String retrieveSiteAddresses() throws WrongInitialWebPageException {
        crawlerConfig.getAddressStorer().storeWebPage(new SiteInformation(crawlerConfig.getRawBaseUrl(), null, 0));
        SiteInformation siteInformation;
        while ((siteInformation = crawlerConfig.getAddressStorer().getNextInWebPageQueue()) != null) {
            try {
                parseWebsite(siteInformation);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while getting url: " + siteInformation.getSite() + " " + e.getMessage());
                crawlerConfig.getAddressStorer().storeError(siteInformation);
            }
            parseUrls();
        }
        return crawlerConfig.getAddressStorer().getUrlJson(true);
    }

    private void parseUrls() throws WrongInitialWebPageException {
        SiteInformation siteInformation;
        while ((siteInformation = crawlerConfig.getAddressStorer().getNextInUrlQueue()) != null) {
            logger.log(Level.INFO,
                    String.format("Getting url: %s | Current depth: %d | URLs saved: %d | URLs left: %d | WebPages left: %d",
                            siteInformation.getSite(),
                            siteInformation.getCurrentDepth(),
                            crawlerConfig.getAddressStorer().getUrlCount(),
                            crawlerConfig.getAddressStorer().getUrlQueueCount(),
                            crawlerConfig.getAddressStorer().getWebPageQueueCount()));

            if (this.handleUrlExists(siteInformation.getSite(), siteInformation.getParentSite()))
                continue;

            try {
                logger.log(Level.INFO, "Sending HEAD request");
                Connection connection = getConnection(siteInformation.getSite());
                //first get just the headers to check the content type
                Connection.Response response = connection.method(Connection.Method.HEAD).ignoreContentType(true).timeout(crawlerConfig.getTimeout()).execute();
                URL responseURL = response.url();

                //were there any redirects while getting the response?
                if (didRedirect(siteInformation.getSite(), responseURL.toString())) {
                    crawlerConfig.getAddressStorer().storeRedirection(siteInformation.getSite(), responseURL.toString());
                    siteInformation.setSite(responseURL.toString());
                    //check if we have already visited the redirected site
                    if (this.handleUrlExists(siteInformation.getSite(), siteInformation.getParentSite()))
                        continue;
                }
                logger.log(Level.INFO, "Content-Type: " + response.contentType());
                //in case it is not an html file then classify it as an asset
                if (response.contentType() != null && !response.contentType().contains("text/html")) {
                    if (siteInformation.getParentSite() == null) throw new WrongInitialWebPageException();

                    //using (maxdepth + 1) here to be able to fetch the assets of a certain page with depth = maxdepth
                    if (siteInformation.getCurrentDepth() > crawlerConfig.getMaxDepth() + 1)
                        continue;

                    crawlerConfig.getAddressStorer().storeAsset(siteInformation);
                } else {
                    //making sure that the urls are in the same domain
                    //NOTE: here using getAuthority instead of getHost could differentiate between sites with different port numbers.
                    //      for more results getHost is used here.
                    if (siteInformation.getCurrentDepth() > crawlerConfig.getMaxDepth()
                            || !responseURL.getHost().equals(crawlerConfig.getBaseUrl().getHost())) {
                        continue;
                    }
                    crawlerConfig.getAddressStorer().storeWebPage(siteInformation);
                }
            }
            catch (WrongInitialWebPageException e)
            {
                throw e;
            }
            catch (Exception e) {
                logger.log(Level.SEVERE, "Error while getting url: " + siteInformation.getSite() + " " + e.getMessage());
                crawlerConfig.getAddressStorer().storeError(siteInformation);
            }
        }
    }

    private void parseWebsite(SiteInformation siteInformation) throws WrongInitialWebPageException, IOException {
        logger.log(Level.INFO, "Parsing website: " + siteInformation.getSite());
        logger.log(Level.INFO, "Sending GET request");
        Connection connection = getConnection(siteInformation.getSite());
        //getting the actual html
        Connection.Response response = connection.method(Connection.Method.GET).ignoreContentType(false).timeout(crawlerConfig.getTimeout()).execute();

        String[] urls = crawlerConfig.getParser().getUrls(response.parse());

        for (String url : urls) {
            URL realURL = getURL(url);
            if(realURL == null)
                continue;

            if(crawlerConfig.isOptimistUrlChecking() && !crawlerConfig.getBaseUrl().getHost().equals(realURL.getHost()))
                continue;

            if(!crawlerConfig.keepHashtags())
                url = url.replace("#" + realURL.getRef(), "");

            if (this.handleUrlExists(url, siteInformation.getSite()))
                continue;

            crawlerConfig.getAddressStorer().storeQueue(new SiteInformation(url, siteInformation.getSite(), siteInformation.getCurrentDepth() + 1));
        }
    }

    private boolean didRedirect(String urlBegin, String urlEnd) {
        return !urlBegin.equals(urlEnd);
    }

    private boolean handleUrlExists(String currentSite, String parentSite) throws WrongInitialWebPageException {
        // get if there was a redirection from this url to another url before (if there was a redirection we have already visited that site)
        currentSite = crawlerConfig.getAddressStorer().getRedirection(currentSite);

        AddressStorerInterface.UrlType urlType = crawlerConfig.getAddressStorer().doesUrlExists(currentSite);
        if(urlType != AddressStorerInterface.UrlType.Unknown)
            logger.log(Level.INFO, "URL already visited, type: " + urlType.toString());

        switch (urlType) {
            case Error:
            case WebPage:
                // no need to fetch again
                return true;
            case Asset:
                // in case of an asset, save it
                if (parentSite == null) throw new WrongInitialWebPageException();
                crawlerConfig.getAddressStorer().storeAsset(new SiteInformation(currentSite, parentSite));
                return true;
        }
        return false;
    }

    private Connection getConnection(String url) {
        return Jsoup.connect(url).method(Connection.Method.HEAD).userAgent(crawlerConfig.getUserAgent()).timeout(crawlerConfig.getTimeout());
    }

    private URL getURL(String url)
    {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
