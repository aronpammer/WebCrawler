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

    HashMap<String, String> redirections;
    Queue<SiteInformation> siteQueue;

    private CrawlerConfig crawlerConfig;

    public Crawler(CrawlerConfig crawlerConfig)
    {
        this.crawlerConfig = crawlerConfig;
        this.redirections = new HashMap<>();
        this.siteQueue = new LinkedList<>();
    }

    public String retreiveSiteAdresses() throws WrongInitialWebPageException {
        siteQueue.add(new SiteInformation(crawlerConfig.getRawBaseUrl(), null, 0));
        crawlSite();
        return crawlerConfig.getAddressStorer().getPages();
    }

    private void crawlSite() throws WrongInitialWebPageException {

        while(siteQueue.size() != 0) {

            SiteInformation siteInformation = siteQueue.poll();
            String currentPath = siteInformation.getSite();
            String parentPath = siteInformation.getParentSite();
            System.out.println(siteInformation.getDepth());
            if(siteInformation.getDepth() > crawlerConfig.getMaxDepth())
                continue;
            //check if there was a redirection from this url to another url before
            if (redirections.containsKey(currentPath)) {
                //if yes, then change the currentPath to the redirected site's url
                currentPath = redirections.get(currentPath);
            }

            //check if we already visited this url
            if (this.handleUrlExists(currentPath, parentPath))
                continue;

            try {
                Connection connection = Jsoup.connect(currentPath).userAgent(crawlerConfig.getUserAgent());

                //first we get just the headers to check the content type
                Connection.Response response = connection.method(Connection.Method.HEAD).ignoreContentType(true).execute();
                URL responseURL = response.url();
                String responseURLString = responseURL.toString();

                if (didRedirect(currentPath, responseURLString)) {
                    System.out.println("redirection happened");
                    System.out.println(currentPath);
                    System.out.println(responseURLString);

                    redirections.put(currentPath, responseURLString);

                    //check if we already visited the redirected path
                    if (this.handleUrlExists(responseURLString, parentPath))
                        continue;
                }

                //in case it is not a html file then we classify it as an asset
                if (response.contentType() != null && !response.contentType().contains("text/html")) {
                    if (parentPath == null) throw new WrongInitialWebPageException();

                    System.out.println(currentPath);
                    System.out.println(parentPath);
                    crawlerConfig.getAddressStorer().storeAsset(parentPath, currentPath);
                } else {
                    //here using getAuthority instead of getHost could differentiate between sites with different port numbers.
                    //for more results getHost is used here.
                    if (!responseURL.getHost().equals(crawlerConfig.getBaseUrl().getHost())) {
                        continue;
                    }
                    System.out.println(responseURLString);
                    //getting the actual html
                    response = connection.method(Connection.Method.GET).ignoreContentType(false).execute();
                    Document htmlDocument = response.parse();
                    System.out.println("storing: " + responseURLString);
                    crawlerConfig.getAddressStorer().storeWebPage(responseURLString);

                    List<URL> urls = crawlerConfig.getParser().getUrls(htmlDocument);
                    System.out.println(urls);
                    for (URL url : urls) {
                        String urlString = url.toString();

                        siteQueue.add(new SiteInformation(urlString, responseURLString, siteInformation.getDepth() + 1));
                    }
                }

            } catch (IOException e) {

                System.out.println("error:" + currentPath);
                e.printStackTrace();
                crawlerConfig.getAddressStorer().storeError(currentPath);
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
