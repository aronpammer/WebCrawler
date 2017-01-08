package aronpammer.webcrawler.store;

import aronpammer.webcrawler.misc.SiteInformation;

public interface AddressStorerInterface {

    enum UrlType { Unknown, WebPage, Error, Asset }

    /**
     * Stores the WebPage and adds it to the WebPage Queue
     * @param siteInformation
     */
    void storeWebPage(SiteInformation siteInformation);

    /**
     * Stores the asset
     * @param siteInformation
     */
    void storeAsset(SiteInformation siteInformation);

    /**
     * Stores an url that couldn't be parsed/loaded
     * @param siteInformation
     */
    void storeError(SiteInformation siteInformation);

    /**
     * Stores a redirection
     * @param from The initial url
     * @param to The final url
     */
    void storeRedirection(String from, String to);

    /**
     * Adds an url to the Url Queue
     * @param siteInformation
     */
    void storeUrlQueue(SiteInformation siteInformation);

    int getUrlQueueCount();

    int getUrlCount();

    int getWebPageQueueCount();

    SiteInformation getNextInUrlQueue();

    SiteInformation getNextInWebPageQueue();

    String getRedirection(String from);

    /**
     * Collects every WebPage url and the assets they contain
     * @return String in Json format
     */
    String getUrlJson();

    UrlType doesUrlExists(String url);



}
