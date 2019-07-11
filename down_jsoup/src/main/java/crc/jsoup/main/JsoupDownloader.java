package crc.jsoup.main;

import crc.jsoup.frames.JsoupFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author cosmin.i
 */
public class JsoupDownloader {

  public static void main(String[] args) {
    final JsoupFrame frame = new JsoupFrame();
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
      }
    });
  }

}
