package crc.jsoup.service;

import java.io.File;

/**
 *
 * @author cosmin.i
 */
public interface JsoupEngine {
  
  public static final String PREF_KEY_PATH = "pref_key_path";
  public static final String PREF_KEY_MAX_PAGE_NUMBER = "pref_key_max_page_number";
  public static final String PREF_KEY_MAX_MAX_COUNT = "pref_key_max_max_count";
  public static final String PREF_KEY_QUERY_PAGE = "pref_key_query_page";
  
  public static final String DEFAULT_DOWNLOAD_PATH = "D:\\DOWNLOAD";
  public static final int DEFAULT_MAX_PAGE_NUMBER = 2;
  public static final int DEFAULT_MAX_COUNT = 10;
  public static final String DEFAULT_QUERY_PAGE = "https://www.goodfon.com/search/";
  
  public void initEngine(JsoupConfig cfg);
  
  public void startDownload(String _queryContent);
  
  public String getStatistics();
  
  public JsoupConfig getCfg();
  
  public void setCfg(JsoupConfig cfg);

  public String getQueryContent();

  public void setQueryContent(String queryContent);
  
  
  public static final class JsoupConfig {
    private File downloadDir;
    private int maxPage;
    private int maxCount; 
    private String queryPage;

    public JsoupConfig(File downloadDir, int maxPage, int maxCount, String queryPage) {
      this.downloadDir = downloadDir;
      this.maxPage = maxPage;
      this.maxCount = maxCount;
      this.queryPage = queryPage;
    }
    
    public File getDownloadDir() {
      return downloadDir;
    }

    public void setDownloadDir(File downloadDir) {
      this.downloadDir = downloadDir;
    }

    public int getMaxPage() {
      return maxPage;
    }

    public void setMaxPage(int maxPage) {
      this.maxPage = maxPage;
    }

    public int getMaxCount() {
      return maxCount;
    }

    public void setMaxCount(int maxCount) {
      this.maxCount = maxCount;
    }

    public String getQueryPage() {
      return queryPage;
    }

    public void setQueryPage(String queryPage) {
      this.queryPage = queryPage;
    }

    
  }
  
}
