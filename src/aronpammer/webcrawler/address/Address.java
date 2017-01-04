package aronpammer.webcrawler.address;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aron on 04/01/17.
 */
public abstract class Address {
    private URL url;

    public Address(String rawPath) throws MalformedURLException
    {
        this.url = new URL(rawPath);
    }

    public String getPath()
    {
        return url.getPath();
    }
}
