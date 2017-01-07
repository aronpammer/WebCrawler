package aronpammer.webcrawler.store.hash;

import aronpammer.webcrawler.misc.SiteInformation;
import aronpammer.webcrawler.store.AddressStorerInterface;
import com.google.gson.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashAddressStorer implements AddressStorerInterface
{
    private static final Logger logger = Logger.getLogger("HashAddressStorer");
    private HashMap<String, WebPageContainer> webPageHashMap;
    private HashSet<String> assetHashSet; // for faster duplicate detection
    private HashSet<String> errorHashSet;
    private HashMap<String, String> redirections;
    private Queue<SiteInformation> urlQueue;
    private Queue<SiteInformation> webPageQueue;

    public HashAddressStorer()
    {
        webPageHashMap = new HashMap<>();
        assetHashSet = new HashSet<>();
        errorHashSet = new HashSet<>();
        redirections = new HashMap<>();
        urlQueue = new LinkedList<>();
        webPageQueue = new LinkedList<>();
    }

    @Override
    public void storeWebPage(SiteInformation siteInformation) {
        String webPageUrl = siteInformation.getSite();
        logger.log(Level.INFO, "Storing webpage: " + webPageUrl);
        webPageHashMap.put(webPageUrl, new WebPageContainer());
        webPageQueue.add(siteInformation);
    }

    @Override
    public void storeAsset(SiteInformation siteInformation) {
        String webPageUrl = siteInformation.getParentSite();
        String assetUrl = siteInformation.getSite();

        logger.log(Level.INFO, "Storing asset, webpageurl: " + webPageUrl + " asseturl: " + assetUrl);
        webPageHashMap.get(webPageUrl).addAsset(assetUrl);
        if(!assetHashSet.contains(assetUrl)) {
            assetHashSet.add(assetUrl);
        }
    }

    @Override
    public void storeError(SiteInformation siteInformation) {
        String currentUrl = siteInformation.getSite();

        logger.log(Level.INFO, "Storing error: " + currentUrl);
        errorHashSet.add(currentUrl);
    }

    @Override
    public void storeRedirection(String from, String to) {
        logger.log(Level.INFO, "Storing redirection, from: " + from + " to: " + to);
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
        urlQueue.add(siteInformation);
    }

    @Override
    public int getUrlQueueCount() {
        return urlQueue.size();
    }

    @Override
    public int getUrlCount() {
        return assetHashSet.size() + errorHashSet.size() + webPageHashMap.size();
    }

    @Override
    public int getWebPageQueueCount() {
        return webPageQueue.size();
    }

    @Override
    public SiteInformation getNextInUrlQueue() {
        return urlQueue.poll();
    }

    @Override
    public SiteInformation getNextInWebPageQueue() {
        return webPageQueue.poll();
    }


    @Override
    public String getUrlJson(boolean includeEmptyAsset) {
        JsonArray rootJsonArray = new JsonArray();
        for (Map.Entry<String, WebPageContainer> entry : webPageHashMap.entrySet()) {
            WebPageContainer webPageContainer = entry.getValue();
            if(!includeEmptyAsset && webPageContainer.getAssets().length == 0)
                continue;
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
