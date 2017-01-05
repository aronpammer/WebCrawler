package aronpammer.webcrawler.store;

public interface AddressStorerInterface {

    enum UrlType { Unknown, WebPage, Error, Asset }

    void storeWebPage(String webPageUrl);

    void storeAsset(String webPageUrl, String assetUrl);

    void storeError(String currentPath);

    String getPages();

    UrlType doesUrlExists(String url);

}
