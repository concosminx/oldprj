package com.nimsoc.finder.main;

import com.nimsoc.finder.ui.FindAndRenameFrame;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class Main {
  public static void main(String[] args) throws IOException {

    try {
      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
      //nimbus not available
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        FindAndRenameFrame far = new FindAndRenameFrame();
        far.pack();
        far.setVisible(true);
        far.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      }
    });
  }
}
