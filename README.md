# Web Crawler

This Web Crawler is a simple java application to crawl a given domain without crossing subdomains. (there are some edge cases however, see later)

### Installation - UNIX
  - Clone/Download project
  - Make sure that the correct java version (1.8+) and gradle (2.14+) is installed on your computer
  - In terminal go into the project root folder
  - 1, Create a .jar file or
  - 2, Run the prebuilt (root folder, name: WebCrawler.jar) JAR file

#### 1, Creating a JAR file
  - In the root folder create the jar file then move it to the current (project root) folder:

```sh
$ gradle jar
$ mv build/libs/WebCrawler-1.2.jar ./WebCrawler.jar
```

#### 2, Running the created/prebuilt jar file (make sure you are still in the project root folder)
```sh
$ java -jar <name of .jar file> <website> <useragent> <maxdepth>
example:
$ java -jar <name of .jar file> "http://www.example.com/" "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36" 2
```

### Flowchart
![flowchart](https://raw.githubusercontent.com/velvetz7/WebCrawler/master/flowchart.png)

### Project decisions
The first thing I had in mind while developing is to be as object oriented as possible and to be able to expand the application later on. 
The current project uses HashMaps and HashSets to efficiently store the informations about the websites, 
however this can be changed by creating a class that implements the AddressStorerInterface and giving that to CrawlConfig. 
One possible extension could be to use an sql or no-sql database to store the urls.
There is also a way to use different HTML parsers, the one I used was to simply extract the URLs by selecting
```sh
a[href]
[src]
link[href]
```
This proved to be the most efficient one, however this can miss a few URL-s if they aren't properly tagged in the HTML code. (eg. the url is in a span)

I also had to take into consideration a few edge cases. The first one was that what should happen in case an url points to eg. goo.gl/abcd. 
There is no way to know just by looking at the url whether this is an asset or a website. 
To find out I used the jsoup library that handles all the redirects and in the end I get the final URL. 
To save some bandwidth here I also made sure to only send HEAD requests. 
This way I could easily determine the content hiding under that url by reading out the Content-Type header information. 
(that is, if it exists. if not, then a GET request is made to check)
The second edge case; do I include an URL that is in another subdomain/domain, eg. goo.gl/abcd, 
but forwards to the same domain as the initial one? 
In my opinion this isn't subdomain crossing (well, in a way it is, and in a way it isn't), so I chose to include these. 
One change I made however; I added the forwarded url into the final result set.

Moreover, if an asset was found on a webpage (even if the asset's domain was not the same as the initial domain) I decided to include that in the result set.

There are several performance optimizations. The first one is to always check if we already determined if an url is an asset to not send the same request twice.