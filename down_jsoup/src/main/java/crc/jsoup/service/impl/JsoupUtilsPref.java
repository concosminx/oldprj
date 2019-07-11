package crc.jsoup.service.impl;

import crc.jsoup.service.JsoupEngine;
import static crc.jsoup.service.JsoupEngine.DEFAULT_DOWNLOAD_PATH;
import static crc.jsoup.service.JsoupEngine.DEFAULT_MAX_COUNT;
import static crc.jsoup.service.JsoupEngine.DEFAULT_MAX_PAGE_NUMBER;
import static crc.jsoup.service.JsoupEngine.DEFAULT_QUERY_PAGE;
import static crc.jsoup.service.JsoupEngine.PREF_KEY_MAX_MAX_COUNT;
import static crc.jsoup.service.JsoupEngine.PREF_KEY_MAX_PAGE_NUMBER;
import static crc.jsoup.service.JsoupEngine.PREF_KEY_PATH;
import static crc.jsoup.service.JsoupEngine.PREF_KEY_QUERY_PAGE;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author cosmin.i
 */
public class JsoupUtilsPref {
  
  public static JsoupEngine.JsoupConfig loadCfgForUser() {
    Preferences preferences = Preferences.userNodeForPackage(crc.jsoup.service.JsoupEngine.class);
    File downloadDir = new File(preferences.get(PREF_KEY_PATH, DEFAULT_DOWNLOAD_PATH));
    int noOfPagesToDownload = preferences.getInt(PREF_KEY_MAX_PAGE_NUMBER, DEFAULT_MAX_PAGE_NUMBER);
    int maxCount = preferences.getInt(PREF_KEY_MAX_MAX_COUNT, DEFAULT_MAX_COUNT);
    String queryPage = preferences.get(PREF_KEY_QUERY_PAGE, DEFAULT_QUERY_PAGE);
    return new JsoupEngine.JsoupConfig(downloadDir, noOfPagesToDownload, maxCount, queryPage);
  }
  
  public static void saveCfgForUser(JsoupEngine.JsoupConfig cfg) {
    Preferences preferences = Preferences.userNodeForPackage(crc.jsoup.service.JsoupEngine.class);
    preferences.put(PREF_KEY_PATH, cfg.getDownloadDir().getAbsolutePath());
    preferences.put(PREF_KEY_MAX_PAGE_NUMBER, ""+cfg.getMaxPage());
    preferences.put(PREF_KEY_MAX_MAX_COUNT, ""+cfg.getMaxCount());
    preferences.put(PREF_KEY_QUERY_PAGE, cfg.getQueryPage());
    
    try {
      preferences.flush();
    } catch (BackingStoreException ex) {
      Logger.getLogger(JsoupUtilsPref.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  
}
