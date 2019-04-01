package com.nimsoc.jsoup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsoupDownloaderGoodFon {

  private static final int MAX_PAGE_NUMBER = 1;
  private static final String USER_AGENT = "Mozilla";
  private static final int MAX_COUNT = 5;
  private static final String QUERY_PAGE = "http://www.some_site.com/search/?";
  private static final String QUERY_CONTENT = "some_search";

  private static final Logger LOG = LoggerFactory.getLogger(JsoupDownloaderGoodFon.class);

  public static void main(String[] args) throws IOException {
    SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
    String user_home = System.getProperty("user.home");

    File f = new File(user_home + File.pathSeparator + sdf.format(new java.util.Date()));
    if (!f.exists()) {
      f.mkdirs();
    }

    LOG.info("computed download folder: " + f.getAbsolutePath());

    //downloaded images counter
    AtomicInteger c = new AtomicInteger(0);

    for (int i = 0; i < MAX_PAGE_NUMBER; i++) {
      String page = "";
      try {
        page = QUERY_PAGE + (i == 0 ? "" : ("p=" + (i + 1))) + (i == 0 ? "" : "&") + "q=" + QUERY_CONTENT;
        downloadCatalogPage(f, page, c);
      } catch (IOException exc) {
        LOG.error("Error on download catalog " + page, exc);
      }
    }
  }

  private static void downloadCatalogPage(File f, String url, AtomicInteger counter) throws IOException {
    LOG.info("catalog page url: " + url);

    //root catalog page connection
    Document doc = Jsoup.connect(url)
            .userAgent(USER_AGENT)
            .timeout(HelperClass.CONNECTION_TIMEOUT)
            .get();

    //https://jsoup.org/cookbook/extracting-data/selector-syntax
    Elements links = doc.select("a[href]");
    List<String> imgLinks = new ArrayList<>();

    //iterate through all links and collect image links
    links.stream().map((link) -> link.attr("abs:href")).filter((absHref) -> (acceptLevel1Link(absHref))).forEachOrdered((absHref) -> {
      imgLinks.add(absHref);
    });

    //iterate accepted links
    for (String imgLink : imgLinks) {
      if (counter.get() >= MAX_COUNT) {
        break;
      }

      Document imgDoc = Jsoup.connect(imgLink)
              .userAgent(USER_AGENT)
              .timeout(HelperClass.CONNECTION_TIMEOUT)
              .get();

      //get download original button (using css class)
      Elements imageLinks = imgDoc.select("a.wallpaper__download__rbut");
      for (Element link : imageLinks) {
        String absHref = link.attr("abs:href");
        if (acceptLevel2Link(absHref)) {
          Document downloadDoc = Jsoup.connect(absHref)
                  .userAgent(USER_AGENT)
                  .timeout(HelperClass.CONNECTION_TIMEOUT)
                  .get();

          //get links and keep only download JPG file link
          Elements downloadLinks = downloadDoc.select("a[href]");
          for (Element dLink : downloadLinks) {
            String h = dLink.attr("abs:href");
            logLink(h);
            if (acceptLevel3Link(h)) {
              if (HelperClass.downloadFile(h, f)) {
                counter.getAndIncrement();
              }
            }
          }
        }
      }
    }
  }

  private static void logLink(String absHref) {
    LOG.info("link: " + absHref);
  }

  private static boolean acceptLevel2Link(String absHref) {
    return absHref != null && absHref.contains("/download/");
  }

  private static boolean acceptLevel3Link(String absHref) {
    return absHref != null && absHref.contains("original") && absHref.endsWith(".jpg");
  }

  private static boolean acceptLevel1Link(String href) {
    return href != null && href.contains(".html") && !href.contains("index");
  }

}
