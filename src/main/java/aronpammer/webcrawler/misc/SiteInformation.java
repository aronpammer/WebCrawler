package aronpammer.webcrawler.misc;


public class SiteInformation
{
    private String site;
    private final String parentSite;
    private final int currentDepth;
    private String contentType;

    public SiteInformation(String site, String parentSite, int depth) {
        this.site = site;
        this.parentSite = parentSite;
        this.currentDepth = depth;
        this.contentType = null;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site)
    {
        this.site = site;
    }

    public String getParentSite() {
        return parentSite;
    }

    public int getCurrentDepth() {
        return currentDepth;
    }
}
