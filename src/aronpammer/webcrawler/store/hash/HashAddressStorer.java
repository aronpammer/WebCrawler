package aronpammer.webcrawler.store.hash;

import aronpammer.webcrawler.store.AddressStorerInterface;

import java.util.HashMap;
import java.util.HashSet;

public class HashAddressStorer implements AddressStorerInterface
{
    HashMap<String, WebPageContainer> webPageHashMap;
    HashSet<String> assetHashSet;
    HashSet<String> errorHashSet;

    public HashAddressStorer()
    {
        webPageHashMap = new HashMap<>();
        assetHashSet = new HashSet<>();
        errorHashSet = new HashSet<>();
    }

    @Override
    public void storeWebPage(String webPageUrl) {
        webPageHashMap.put(webPageUrl, new WebPageContainer(webPageUrl));
    }

    @Override
    public void storeAsset(String webPageUrl, String assetUrl) {
        webPageHashMap.get(webPageUrl).addAsset(assetUrl);
        assetHashSet.add(assetUrl);
    }

    @Override
    public void storeError(String currentPath) {
        errorHashSet.add(currentPath);
    }

    @Override
    public String getPages() {
        return null;
    }

    @Override
    public UrlType doesUrlExists(String url) {
        if(webPageHashMap.containsKey(url))
            return UrlType.WebPage;
        if(assetHashSet.contains(url))
            return UrlType.Asset;
        if(errorHashSet.contains(url))
            return UrlType.Error;
        return UrlType.Unknown;
    }
}
