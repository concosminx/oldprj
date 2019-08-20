package crc.jsoup.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class HelperClass {

  public static final int CONNECTION_TIMEOUT = 3000;

  static boolean downloadFile(String absHref, File f) throws IOException {
    boolean output = false;
    FileOutputStream fos = null;
    try {
      URL website = new URL(absHref);
      ReadableByteChannel rbc = Channels.newChannel(website.openStream());
      File file = new File(f, getFileNameFromLink(absHref));
      if (!file.exists()) {
        fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        output = true;
      }
    } finally {
      if (fos != null) {
        fos.close();
      }
    }
    return output;
  }

  static String getFileNameFromLink(String href) {
    String fileName = "nope.txt";
    if (href != null) {
      fileName = href.substring(href.lastIndexOf('/') + 1);
    }
    return fileName;
  }
}
