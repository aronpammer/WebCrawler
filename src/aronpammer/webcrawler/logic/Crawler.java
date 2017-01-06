package aronpammer.webcrawler.logic;

import aronpammer.webcrawler.exception.WrongInitialWebPageException;
import aronpammer.webcrawler.misc.CrawlerConfig;
import aronpammer.webcrawler.misc.SiteInformation;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Crawler {
    private CrawlerConfig crawlerConfig;

    public Crawler(CrawlerConfig crawlerConfig)
    {
        this.crawlerConfig = crawlerConfig;
    }

    public String retrieveSiteAddresses() throws WrongInitialWebPageException {
        crawlerConfig.getAddressStorer().storeQueue(new SiteInformation(crawlerConfig.getRawBaseUrl(), null, 0));
        crawlSite();
        return crawlerConfig.getAddressStorer().getPages();
    }

    private String getContentType(Connection.Response response)
    {
        return null;
    }

    private void crawlSite() throws WrongInitialWebPageException {
        SiteInformation siteInformation;
        while((siteInformation = crawlerConfig.getAddressStorer().getQueue()) != null) {
            String currentSite = siteInformation.getSite();
            String parentSite = siteInformation.getParentSite();

            // get if there was a redirection from this url to another url before
            currentSite = crawlerConfig.getAddressStorer().getRedirection(currentSite);

            //check if we have already visited this url
            if (this.handleUrlExists(currentSite, parentSite))
                continue;

            try {
                Connection connection = Jsoup.connect(currentSite).userAgent(crawlerConfig.getUserAgent());

                //first we get just the headers to check the content type
                Connection.Response response = connection.method(Connection.Method.HEAD).ignoreContentType(true).execute();
                URL responseURL = response.url();
                String responseURLString = responseURL.toString();

                //were there any redirects while getting the response?
                if (didRedirect(currentSite, responseURLString)) {
                    crawlerConfig.getAddressStorer().storeRedirection(currentSite, responseURLString);
                    //check if we have already visited the redirected path
                    if (this.handleUrlExists(responseURLString, parentSite))
                        continue;
                }

                //in case it is not an html file then we classify it as an asset
                if (response.contentType() != null && !response.contentType().contains("text/html")) {
                    if (parentSite == null) throw new WrongInitialWebPageException();

                    //using (maxdepth + 1) here to be able to fetch the assets of a certain page that's depth is maxdepth
                    if(siteInformation.getCurrentDepth() > crawlerConfig.getMaxDepth() + 1)
                        continue;
                    System.out.println(currentSite);
                    crawlerConfig.getAddressStorer().storeAsset(parentSite, currentSite);
                } else {
                    //making sure that we are still in the same domain
                    //NOTE: here using getAuthority instead of getHost could differentiate between sites with different port numbers.
                    //      for more results getHost is used here.
                    if (siteInformation.getCurrentDepth() > crawlerConfig.getMaxDepth()
                            || !responseURL.getHost().equals(crawlerConfig.getBaseUrl().getHost())) {
                        continue;
                    }
                    //getting the actual html
                    response = connection.method(Connection.Method.GET).ignoreContentType(false).execute();
                    Document htmlDocument = response.parse();

                    crawlerConfig.getAddressStorer().storeWebPage(responseURLString);

                    List<URL> urls = crawlerConfig.getParser().getUrls(htmlDocument);
                    System.out.println(urls);
                    for (URL url : urls) {
                        String urlString = url.toString();

                        crawlerConfig.getAddressStorer().storeQueue(new SiteInformation(urlString, responseURLString, siteInformation.getCurrentDepth() + 1));
                    }
                }

            } catch (IOException e) {
                crawlerConfig.getAddressStorer().storeError(currentSite);
            }
        }


    }

    private boolean didRedirect(String urlBegin, String urlEnd)
    {
        return !urlBegin.equals(urlEnd);
    }

    private boolean handleUrlExists(String currentPath, String parentPath) throws WrongInitialWebPageException {
        switch (crawlerConfig.getAddressStorer().doesUrlExists(currentPath))
        {
            case Error:
            case WebPage:
                // no need to fetch again
                return true;
            case Asset:
                // in case of an asset, save it
                if(parentPath == null) throw new WrongInitialWebPageException();
                crawlerConfig.getAddressStorer().storeAsset(parentPath, currentPath);
                return true;
        }
        return false;
    }
}
