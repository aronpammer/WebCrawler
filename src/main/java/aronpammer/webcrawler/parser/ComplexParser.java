package aronpammer.webcrawler.parser;

import com.sun.tools.javac.util.ArrayUtils;
import org.jsoup.nodes.Document;

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
        long ms = System.currentTimeMillis();
        System.out.println(System.currentTimeMillis() - ms);
        for (String url: quickParser.getUrls(document)) {
            union.add(url);
        }
        System.out.println(System.currentTimeMillis() - ms);
        for (String url: regexParser.getUrls(document)) {
            union.add(url);
        }
        System.out.println(System.currentTimeMillis() - ms);
        System.out.println();
        return union.toArray(new String[union.size()]);
    }
}
