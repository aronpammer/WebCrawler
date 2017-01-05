package aronpammer.webcrawler.store.hash;

import aronpammer.webcrawler.store.AddressStorerInterface;
import com.google.gson.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class HashAddressStorer implements AddressStorerInterface
{
    HashMap<String, WebPageContainer> webPageHashMap;
    HashSet<String> assetHashSet; // for faster duplicate detection
    HashSet<String> errorHashSet;

    public HashAddressStorer()
    {
        webPageHashMap = new HashMap<>();
        assetHashSet = new HashSet<>();
        errorHashSet = new HashSet<>();
    }

    @Override
    public void storeWebPage(String webPageUrl) {
        webPageHashMap.put(webPageUrl, new WebPageContainer());
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
        JsonArray rootJsonArray = new JsonArray();
        for (Map.Entry<String, WebPageContainer> entry : webPageHashMap.entrySet()) {
            WebPageContainer webPageContainer = entry.getValue();
            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("url", entry.getKey());
            JsonArray assets = new JsonArray();
            for (String asset : webPageContainer.getAssets())
            {
                assets.add(new JsonPrimitive(asset));
            }
            mainObject.add("assets", assets);
            rootJsonArray.add(mainObject);
        }

        return rootJsonArray.toString();
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
