package aronpammer.webcrawler.misc;

public class CrawlerConfig {
    private String baseUrl;
    private int maxDepth;
    public CrawlerConfig(String baseUrl, int maxDepth)
    {
        this.baseUrl = baseUrl;
        this.maxDepth = maxDepth;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
