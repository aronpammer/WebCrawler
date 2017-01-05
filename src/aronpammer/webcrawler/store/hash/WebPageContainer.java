package aronpammer.webcrawler.store.hash;

import java.net.MalformedURLException;
import java.util.HashSet;

/**
 * Created by aron on 05/01/17.
 */
public class WebPageContainer {

    HashSet<String> assetHashSet;

    public WebPageContainer(String absolutePath) {
        this.assetHashSet = new HashSet<>();
    }

    public void addAsset(String asset)
    {
        this.assetHashSet.add(asset); //no need to check for duplicates since hashset automatically discards them on insert
    }

    public String[] getAssets()
    {
        return assetHashSet.stream().toArray(String[]::new);
    }
}
