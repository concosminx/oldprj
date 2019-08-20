package crc.jsoup.service.impl;

import crc.jsoup.service.JsoupEngine;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cosmin.i
 */
public class GoodFonJsoupEngineImpl implements JsoupEngine {

  private static final Logger LOG = LoggerFactory.getLogger(GoodFonJsoupEngineImpl.class);

  private static final String USER_AGENT = "Mozilla";
  private static final int CONNECTION_TIMEOUT = 10000;
  private JsoupConfig cfg = null;
  private String queryContent = "";
  private final StringBuilder stats = new StringBuilder();
  private final static String LS = "\n";

  @Override
  public void initEngine(JsoupConfig _cfg) {
    this.cfg = _cfg;
    if (!cfg.getDownloadDir().exists()) {
      cfg.getDownloadDir().mkdirs();
    }
    stats.setLength(0);
    stats.append("Initialization ended at ").append(new Date()).append(LS);
  }

  @Override
  public JsoupConfig getCfg() {
    return cfg;
  }

  @Override
  public void setCfg(JsoupConfig cfg) {
    this.cfg = cfg;
    if (!this.cfg.getDownloadDir().exists()) {
      this.cfg.getDownloadDir().mkdirs();
    }
  }

  @Override
  public String getQueryContent() {
    return queryContent;
  }

  @Override
  public void setQueryContent(String queryContent) {
    this.queryContent = queryContent;
  }

  private void downloadCatalogPage(File f, String url, AtomicInteger counter) throws IOException {
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
      if (counter.get() >= this.getCfg().getMaxCount()) {
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

  @Override
  public void startDownload(String _queryContent) {
    setQueryContent(_queryContent);
    AtomicInteger counter = new AtomicInteger(0);
    for (int i = 0; i < cfg.getMaxPage(); i++) {
      String page = "";
      try {
        page = cfg.getQueryPage() + ("?q=" + queryContent) + (i == 0 ? "" : "&") + (i == 0 ? "" : ("page=" + (i + 1)));
        downloadCatalogPage(this.getCfg().getDownloadDir(), page, counter);
      } catch (IOException exc) {
        LOG.error("Error on download catalog " + page, exc);
      }
    }
    stats.append("Download ended at ").append(new Date()).append("; number of files downloaded: ").append(counter.get()).append(LS);
  }

  private String getFileNameFromLink(String href) {
    String fileName = "nope.txt";
    if (href != null) {
      fileName = href.substring(href.lastIndexOf('/') + 1);
    }
    return fileName;
  }

  @Override
  public String getStatistics() {
    return stats.toString();
  }

}
