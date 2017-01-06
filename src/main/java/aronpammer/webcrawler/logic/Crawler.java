package aronpammer.webcrawler.logic;

import aronpammer.webcrawler.exception.WrongInitialWebPageException;
import aronpammer.webcrawler.misc.CrawlerConfig;
import aronpammer.webcrawler.misc.SiteInformation;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class Crawler {
    private CrawlerConfig crawlerConfig;

    public Crawler(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }

    public String retrieveSiteAddresses() throws WrongInitialWebPageException {
        crawlerConfig.getAddressStorer().storeQueue(new SiteInformation(crawlerConfig.getRawBaseUrl(), null, 0));
        crawlSite();
        return crawlerConfig.getAddressStorer().getPages();
    }

    private void crawlSite() throws WrongInitialWebPageException {
        SiteInformation siteInformation;
        while ((siteInformation = crawlerConfig.getAddressStorer().getQueue()) != null) {

            try {
                Connection connection = Jsoup.connect(siteInformation.getSite()).userAgent(crawlerConfig.getUserAgent());

                //first get just the headers to check the content type
                Connection.Response response = connection.method(Connection.Method.HEAD).ignoreContentType(true).execute();
                URL responseURL = response.url();

                //were there any redirects while getting the response?
                if (didRedirect(siteInformation.getSite(), responseURL.toString())) {
                    crawlerConfig.getAddressStorer().storeRedirection(siteInformation.getSite(), responseURL.toString());
                    siteInformation.setSite(responseURL.toString());
                    //check if we have already visited the redirected site
                    if (this.handleUrlExists(siteInformation.getSite(), siteInformation.getParentSite()))
                        continue;
                }

                //in case it is not an html file then classify it as an asset
                if (response.contentType() != null && !response.contentType().contains("text/html")) {
                    if (siteInformation.getParentSite() == null) throw new WrongInitialWebPageException();

                    //using (maxdepth + 1) here to be able to fetch the assets of a certain page that's depth is maxdepth
                    if (siteInformation.getCurrentDepth() > crawlerConfig.getMaxDepth() + 1)
                        continue;

                    crawlerConfig.getAddressStorer().storeAsset(siteInformation.getParentSite(), siteInformation.getSite());
                } else {
                    //making sure that the urls are in the same domain
                    //NOTE: here using getAuthority instead of getHost could differentiate between sites with different port numbers.
                    //      for more results getHost is used here.
                    if (siteInformation.getCurrentDepth() > crawlerConfig.getMaxDepth()
                            || !responseURL.getHost().equals(crawlerConfig.getBaseUrl().getHost())) {
                        continue;
                    }

                    parseWebsite(connection, siteInformation);
                }
            } catch (Exception e) {
                crawlerConfig.getAddressStorer().storeError(siteInformation.getSite());
            }
        }
    }

    private void parseWebsite(Connection connection, SiteInformation siteInformation) throws WrongInitialWebPageException, IOException {
        //getting the actual html
        Connection.Response response = connection.method(Connection.Method.GET).ignoreContentType(false).execute();

        crawlerConfig.getAddressStorer().storeWebPage(siteInformation.getSite());

        String[] urls = crawlerConfig.getParser().getUrls(response.parse());
        //System.out.println(Arrays.toString(urls));
        for (String url : urls) {
            //check if we have already visited this url
            if (this.handleUrlExists(url, siteInformation.getSite()))
                continue;

            crawlerConfig.getAddressStorer().storeQueue(new SiteInformation(url, siteInformation.getSite(), siteInformation.getCurrentDepth() + 1));
        }
    }

    private boolean didRedirect(String urlBegin, String urlEnd) {
        return !urlBegin.equals(urlEnd);
    }

    private boolean handleUrlExists(String currentSite, String parentSite) throws WrongInitialWebPageException {
        // get if there was a redirection from this url to another url before
        currentSite = crawlerConfig.getAddressStorer().getRedirection(currentSite);

        switch (crawlerConfig.getAddressStorer().doesUrlExists(currentSite)) {
            case Error:
            case WebPage:
                // no need to fetch again
                return true;
            case Asset:
                // in case of an asset, save it
                if (parentSite == null) throw new WrongInitialWebPageException();
                crawlerConfig.getAddressStorer().storeAsset(parentSite, currentSite);
                return true;
        }
        return false;
    }
}
