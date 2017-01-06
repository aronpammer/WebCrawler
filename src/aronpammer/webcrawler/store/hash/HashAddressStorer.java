package aronpammer.webcrawler.store.hash;

import aronpammer.webcrawler.misc.SiteInformation;
import aronpammer.webcrawler.store.AddressStorerInterface;
import com.google.gson.*;

import java.util.*;

public class HashAddressStorer implements AddressStorerInterface
{
    private HashMap<String, WebPageContainer> webPageHashMap;
    private HashSet<String> assetHashSet; // for faster duplicate detection
    private HashSet<String> errorHashSet;
    private HashMap<String, String> redirections;
    private Queue<SiteInformation> siteQueue;

    public HashAddressStorer()
    {
        webPageHashMap = new HashMap<>();
        assetHashSet = new HashSet<>();
        errorHashSet = new HashSet<>();
        redirections = new HashMap<>();
        siteQueue = new LinkedList<>();
    }

    @Override
    public void storeWebPage(String webPageUrl) {
        webPageHashMap.put(webPageUrl, new WebPageContainer());
        System.out.println("webpage: " + webPageHashMap.size());
    }

    @Override
    public void storeAsset(String webPageUrl, String assetUrl) {
        webPageHashMap.get(webPageUrl).addAsset(assetUrl);
        assetHashSet.add(assetUrl);
        System.out.println("asset: " + assetHashSet.size());
    }

    @Override
    public void storeError(String currentPath) {
        errorHashSet.add(currentPath);
    }

    @Override
    public void storeRedirection(String from, String to) {
        redirections.put(from, to);
    }

    @Override
    public String getRedirection(String from) {
        //check if there was a redirection from this url to another url before
        if (redirections.containsKey(from)) {
            //return the final location of the redirection
            return redirections.get(from);
        }
        //otherwise return the original url
        return from;
    }

    @Override
    public void storeQueue(SiteInformation siteInformation) {
        siteQueue.add(siteInformation);
    }

    @Override
    public SiteInformation getQueue() {
        return siteQueue.poll();
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
