package aronpammer.webcrawler.store;

import aronpammer.webcrawler.misc.SiteInformation;

public interface AddressStorerInterface {

    enum UrlType { Unknown, WebPage, Error, Asset }

    void storeWebPage(String webPageUrl);

    void storeAsset(String webPageUrl, String assetUrl);

    void storeError(String currentPath);

    void storeRedirection(String from, String to);

    void storeQueue(SiteInformation siteInformation);

    SiteInformation getQueue();

    String getRedirection(String from);

    String getPages();

    UrlType doesUrlExists(String url);

}
