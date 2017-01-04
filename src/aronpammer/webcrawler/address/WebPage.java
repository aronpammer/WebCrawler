package aronpammer.webcrawler.address;

import java.net.MalformedURLException;
import java.util.HashSet;

/**
 * Created by aron on 05/01/17.
 */
public class WebPage extends Address {

    HashSet<Asset> assetHashSet;

    public WebPage(String rawPath) throws MalformedURLException {
        super(rawPath);
        this.assetHashSet = new HashSet<Asset>();
    }

    public void addAsset(Asset asset)
    {
        this.assetHashSet.add(asset); //no need to check for duplicates since hashset automatically discards them
    }

    public String[] getAssets()
    {

    }
}
