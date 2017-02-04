# Web Crawler

This Web Crawler is a simple java application to crawl a given domain without crossing subdomains.
Output: in JSON format listing the URLs of every static asset, grouped by page
For example:
```sh
[
  {
    "url": "http://www.example.org",
    "assets": [
      "http://www.example.org/image.jpg",
      "http://www.example.org/script.js"
    ]
  },
  {
    "url": "http://www.example.org/about",
    "assets": [
      "http://www.example.org/company_photo.jpg",
      "http://www.example.org/script.js"
    ]
  },
  ..
]
```

### Installation - UNIX
  - Clone/Download project
```
git clone https://github.com/velvetz7/WebCrawler.git
```
  - Make sure that the correct java version (1.8+) is installed on your computer
  - In terminal go into the project root folder
  - [Create a .jar file (you need to have gradle [2.14+])](#creating-a-jar-file) and/or
  - [Run the created/prebuilt JAR file](#running-the-createdprebuilt-jar-file-make-sure-you-are-still-in-the-project-root-folder)

#### Creating a JAR file
  - In the root folder create the jar file then move it to the current (project root) folder:

```
$ gradle jar
$ mv build/libs/WebCrawler-1.0.jar ./WebCrawler.jar
```

#### Running the created/prebuilt jar file (make sure you are still in the project root folder)
```
$ java -jar <name of .jar file> <flags>
usage: WebCrawler [-?] [-keephashtags] [-maxdepth <arg>] [-optimistic]
       [-timeout <arg>] [-useragent <arg>] [-verbose] [-website <arg>]
 -?,--help          Print this message and exit
 -keephashtags      An url with a different hashtag at the end is a different url
 -maxdepth <arg>    Maximum depth (default: 99999)
 -optimistic        Optimistic URL checking; don't add URLs to the queue
                    that isn't in the same domain/subdomain
 -timeout <arg>     Timeout to use while loading the websites (default: 5000)
 -useragent <arg>   The user agent to use to load the websites (default: "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
 -verbose           Output log messages
 -website <arg>     The starting URL - REQUIRED

example (recommended):
$ java -jar WebCrawler.jar -website "http://www.example.com/" -optimistic
```

### Flowchart
![flowchart](https://raw.githubusercontent.com/velvetz7/WebCrawler/master/flowchart.png)

### Project decisions
The first thing I had in mind while developing is to be as object oriented as possible to be able to expand the application later on.
The current project uses HashMaps and HashSets to efficiently store the informations about the websites, 
however this can be changed by creating a class that implements the AddressStorerInterface and giving that to CrawlConfig. 
One possible extension could be to use an sql or no-sql database to store the urls.
There is also a way to use different HTML parsers.
####I included three different types:
#####QuickParser.java
Simple url extraction by selecting
```
a[href]
[src]
link[href]
```
This proved to be efficient (avg. 1ms / page), however it can miss a few URL-s if they aren't properly tagged in the HTML code. (eg. the url is in a span)

#####RegexParser.java
Selects strings that match the following regex:
```
(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?
```
Slightly slower (avg. 5ms/page) than QuickParser, and misses relative urls.
#####ComplexParser.java
Uses both QuickParser and ComplexParser. Slow (avg 6ms / page), but very accurate.

#### How to tell if the URL points to an asset or an html site?
Let's say there is this URL on the website: goo.gl/abcd.
**There is no way to know whether this is an *asset* or an *html site* just by looking at the url.**

To find out I used the jsoup library that handles all the redirects and gets the final URL. (To save some bandwidth I also made sure to only send HEAD requests.)

This way I could easily determine the content hiding under that url by reading out the Content-Type header information. 
(that is, if it exists. if not, then I send a GET request to check the file content)

#### Include an URL that is in another subdomain/domain, but redirects to the same domain as the initial one?
Using the same example as above, if the goo.gl/abcd redirects me to the initial domain, the application includes the URL it was redirected to (not the original).

Moreover, if an asset was found on a webpage (even if the asset's domain was not the same as the initial domain) I decided to include that in the result set.

*Add the "-optimistic" flag to only add URLs to the queue that is in the same domain/subdomain*
