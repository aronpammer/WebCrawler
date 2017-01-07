package aronpammer.webcrawler.parser;

import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.HashSet;

public class ComplexParser implements ParserInterface {
    private QuickParser quickParser;
    private RegexParser regexParser;

    public ComplexParser()
    {
        this.quickParser = new QuickParser();
        this.regexParser = new RegexParser();
    }

    @Override
    public String[] getUrls(Document document) {
        HashSet<String> union = new HashSet<>();
        Collections.addAll(union, quickParser.getUrls(document));
        Collections.addAll(union, regexParser.getUrls(document));
        return union.toArray(new String[union.size()]);
    }
}
