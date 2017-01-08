package aronpammer.webcrawler.store.hash;

import java.util.HashSet;

public class WebPageContainer {

    private HashSet<String> assetHashSet;

    public WebPageContainer() {
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
