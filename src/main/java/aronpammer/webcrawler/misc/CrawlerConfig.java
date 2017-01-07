package aronpammer.webcrawler.misc;

import aronpammer.webcrawler.parser.ParserInterface;
import aronpammer.webcrawler.store.AddressStorerInterface;

import java.net.MalformedURLException;
import java.net.URL;

public class CrawlerConfig {
    private final int maxDepth;
    private final ParserInterface parser;
    private final String rawBaseUrl;
    private final AddressStorerInterface addressStorer;
    private final String userAgent;
    private final URL baseUrl;
    private final boolean optimistUrlChecking;
    private final boolean keepHashtags;

    private final int timeout;
    public CrawlerConfig(String rawBaseUrl, AddressStorerInterface addressStorer, ParserInterface parser, String userAgent, boolean optimistUrlChecking, boolean keepHashtags, int maxDepth, int timeout) throws MalformedURLException {
        this.rawBaseUrl = rawBaseUrl;
        this.baseUrl = new URL(rawBaseUrl);
        this.optimistUrlChecking = optimistUrlChecking;
        this.keepHashtags = keepHashtags;
        this.maxDepth = maxDepth;
        this.addressStorer = addressStorer;
        this.userAgent = userAgent;
        this.parser = parser;
        this.timeout = timeout;
    }

    public String getRawBaseUrl() {
        return rawBaseUrl;
    }

    public AddressStorerInterface getAddressStorer() {
        return addressStorer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public ParserInterface getParser() {
        return parser;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isOptimistUrlChecking() {
        return optimistUrlChecking;
    }

    public boolean isKeepHashtags() {
        return keepHashtags;
    }
}
