package aronpammer.webcrawler.store;

import aronpammer.webcrawler.misc.SiteInformation;

public interface AddressStorerInterface {

    enum UrlType { Unknown, WebPage, Error, Asset }

    void storeWebPage(SiteInformation siteInformation);

    void storeAsset(SiteInformation siteInformation);

    void storeError(SiteInformation siteInformation);

    void storeRedirection(String from, String to);

    void storeQueue(SiteInformation siteInformation);

    int getUrlQueueCount();

    int getUrlCount();

    int getWebPageQueueCount();

    SiteInformation getNextInUrlQueue();

    SiteInformation getNextInWebPageQueue();

    String getRedirection(String from);

    String getUrlJson(boolean includeEmptyAsset);

    UrlType doesUrlExists(String url);



}
