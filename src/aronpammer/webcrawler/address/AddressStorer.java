package aronpammer.webcrawler.address;

public interface AddressStorer {
    void storeUrl(String url);
    void urlExists(String url);

}
