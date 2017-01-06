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

    public CrawlerConfig(String rawBaseUrl, AddressStorerInterface addressStorer, ParserInterface parser, String userAgent, int maxDepth) throws MalformedURLException {
        this.rawBaseUrl = rawBaseUrl;
        this.baseUrl = new URL(rawBaseUrl);
        this.maxDepth = maxDepth;
        this.addressStorer = addressStorer;
        this.userAgent = userAgent;
        this.parser = parser;
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
}
