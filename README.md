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
  - Make sure that the correct java version (1.8+) is installed on your computer
  - In terminal go into the project root folder
  - [Create a .jar file (you need to have gradle [2.14+])](#creating-a-jar-file) and/or
  - [Run the created/prebuilt JAR file](#running-the-createdprebuilt-jar-file-make-sure-you-are-still-in-the-project-root-folder)

#### Creating a JAR file
  - In the root folder create the jar file then move it to the current (project root) folder:

```sh
$ gradle jar
$ mv build/libs/WebCrawler-1.2.jar ./WebCrawler.jar
```

#### Running the created/prebuilt jar file (make sure you are still in the project root folder)
```sh
$ java -jar <name of .jar file> <flags>
Flags:
-website <url of the website> - required
-useragent <user agent to use to load the websites> - optional
-maxdepth <the maximum level of depth while crawling> - optional (default: 5)
-timeout <the timeout in milliseconds for loading the websites> - optional (default: 5000)
-verbose - print out log messages - optional
example:
$ java -jar <name of .jar file> -website "http://www.example.com/" -useragent "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36" -maxdepth 2 -timout 3000 -verbose
```

### Flowchart
![flowchart](https://raw.githubusercontent.com/velvetz7/WebCrawler/master/flowchart.png)

### Project decisions
The first thing I had in mind while developing is to be as object oriented as possible and to be able to expand the application later on. 
The current project uses HashMaps and HashSets to efficiently store the informations about the websites, 
however this can be changed by creating a class that implements the AddressStorerInterface and giving that to CrawlConfig. 
One possible extension could be to use an sql or no-sql database to store the urls.
There is also a way to use different HTML parsers. The one I used (QuickParser.java) was a simple url extraction by selecting
```sh
a[href]
[src]
link[href]
```
This proved to be efficient, however it can miss a few URL-s if they aren't properly tagged in the HTML code. (eg. the url is in a span)

#### How to tell if the URL points to an asset or an html site?
Let's say there is this URL on the website: goo.gl/abcd.
**There is no way to know whether this is an *asset* or an *html site* just by looking at the url.**

To find out the I used the jsoup library that handles all the redirects and gets the final URL. (To save some bandwidth I also made sure to only send HEAD requests.)

This way I could easily determine the content hiding under that url by reading out the Content-Type header information. 
(that is, if it exists. if not, then I send a GET request to check the file content)

#### Do I include an URL that is in another subdomain/domain, but forwards to the same domain as the initial one?
Using the same example as above, if the goo.gl/abcd redirects me to the initial domain, the application includes the final redirected URL (not the original).

Moreover, if an asset was found on a webpage (even if the asset's domain was not the same as the initial domain) I decided to include that in the result set.