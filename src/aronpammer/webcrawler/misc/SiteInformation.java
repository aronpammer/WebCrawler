package aronpammer.webcrawler.misc;


public class SiteInformation
{
    private final String site;
    private final String parentSite;
    private final int depth;

    public SiteInformation(String site, String parentSite, int depth) {
        this.site = site;
        this.parentSite = parentSite;
        this.depth = depth;
    }

    public String getSite() {
        return site;
    }

    public String getParentSite() {
        return parentSite;
    }

    public int getDepth() {
        return depth;
    }
}
