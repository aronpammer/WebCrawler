package aronpammer.webcrawler;

import aronpammer.webcrawler.exception.WrongInitialWebPageException;
import aronpammer.webcrawler.logic.Crawler;
import aronpammer.webcrawler.misc.CrawlerConfig;
import aronpammer.webcrawler.parser.ComplexParser;
import aronpammer.webcrawler.parser.QuickParser;
import aronpammer.webcrawler.store.hash.HashAddressStorer;
import org.apache.commons.cli.*;

import java.net.MalformedURLException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    private static Options setupOptions()
    {
        Options options = new Options();
        options.addOption("website", true, "The starting URL");
        options.addOption("useragent", true, "The user agent to use to load the websites");
        options.addOption("maxdepth", true, "The maximum level of depth while crawling");
        options.addOption("timeout", true, "Timeout to use while loading the websites");
        options.addOption("verbose", false, "Output log messages");
        options.addOption("optimistic", false, "Optimistic URL checking; don't add URLs to the queue that isn't in the same domain/subdomain");
        options.addOption("keephashtags", false, "An url with a different hashtag at the end is the same url");
        options.addOption("?", "help", false, "Print this message and exit");
        return options;
    }

    public static void main(String[] args) {

        try {
            CommandLineParser parser = new BasicParser();
            Options options = setupOptions();
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption("help"))
            {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("WebCrawler", options, true);
                return;
            }
            String website;
            String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
            int maxDepth = 5;
            int timeout = 3000;

            boolean optimistUrlChecking = cmd.hasOption("optimistic");
            boolean keepHashtags = cmd.hasOption("keephashtags");

            if(cmd.hasOption("website"))
                website = cmd.getOptionValue("website");
            else
                throw new WrongInitialWebPageException();

            if(cmd.hasOption("useragent"))
                userAgent = cmd.getOptionValue("useragent");

            if(cmd.hasOption("maxdepth"))
                maxDepth = Integer.parseInt(cmd.getOptionValue("maxdepth"));

            if(cmd.hasOption("timeout"))
                timeout = Integer.parseInt(cmd.getOptionValue("timeout"));

            if(!cmd.hasOption("verbose"))
            {
                LogManager.getLogManager().reset();
                Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
                globalLogger.setLevel(java.util.logging.Level.OFF);
            }

            CrawlerConfig crawlerConfig = new CrawlerConfig(website, new HashAddressStorer(), new ComplexParser(), userAgent, optimistUrlChecking, keepHashtags, maxDepth, timeout);
            Crawler crawler = new Crawler(crawlerConfig);
            String result = crawler.retrieveSiteAddresses();
            System.out.println(result);
        } catch (WrongInitialWebPageException e) {
            System.out.println("The initial url was not pointing to an html");
        } catch (MalformedURLException e) {
            System.out.println("Malformed initial url");
        } catch (NumberFormatException e) {
            System.out.println("Wrong parameter format");
        } catch (ParseException e) {
            System.out.println("Command line parse error: " + e.getMessage());
        }

    }
}
