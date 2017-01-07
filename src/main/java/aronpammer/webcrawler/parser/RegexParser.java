package aronpammer.webcrawler.parser;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser implements ParserInterface {


    @Override
    public String[] getUrls(Document document) {
        String regex = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(document.html());
        ArrayList<String> urls = new ArrayList<>();
        while(matcher.find())
        {
            urls.add(matcher.group());
        }
        return urls.toArray(new String[urls.size()]);
    }
}
