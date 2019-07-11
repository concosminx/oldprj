package crc.jsoup.frames;

import com.google.common.base.Strings;
import crc.jsoup.service.JsoupEngine;
import crc.jsoup.service.impl.JsoupUtilsPref;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 *
 * @author cosmin.i
 */
public class JsoupDialog extends JDialog implements ActionListener {

  private static final long serialVersionUID = 1L;
  private JsoupEngine engine = null;
  
  private JTextField tDir = null;
  private JTextField tPages = null;
  private JTextField tCount = null;
  private JTextField tQuery = null;
  private JPanel rp = new JPanel();

  public JsoupDialog(JFrame parent, String title, String message, JsoupEngine engine) {
    super(parent, title);
    Point p = new Point(400, 400);
    setLocation(p.x, p.y);
    this.engine = engine;
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    
    initGUI();
    
    pack();
    setModal(true);
    setVisible(true);

  }

  private void initGUI() {
    Action action = new AbstractAction() {
      private static final long serialVersionUID = 1L;
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
      }
    };
    
    rp.setLayout(new GridBagLayout());
    rp.setOpaque(true);
    InputMap inputMap = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
    inputMap.put(stroke, "ESCAPE");
    rp.getActionMap().put("ESCAPE", action);
    
    this.setPreferredSize(new Dimension(400, 250));
    this.setMinimumSize(new Dimension(400, 250));
    
    JsoupEngine.JsoupConfig cfg = engine.getCfg();
    
    JLabel lDir = new JLabel("Path: ");
    tDir = new JTextField();
    tDir.setText(cfg.getDownloadDir().getAbsolutePath());
    tDir.setPreferredSize(new Dimension(150, 20));
    tDir.setMinimumSize(tDir.getPreferredSize());
    
    rp.add(lDir, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    rp.add(tDir, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    
    JLabel lPages = new JLabel("No. of pages: ");
    tPages = new JTextField();
    tPages.setText(""+cfg.getMaxPage());
    tPages.setPreferredSize(new Dimension(150, 20));
    tPages.setMinimumSize(tPages.getPreferredSize());
    
    rp.add(lPages, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    rp.add(tPages, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    
    JLabel lCount = new JLabel("Max. count: ");
    tCount = new JTextField();
    tCount.setText(""+cfg.getMaxCount());
    tCount.setPreferredSize(new Dimension(150, 20));
    tCount.setMinimumSize(tCount.getPreferredSize());
    
    rp.add(lCount, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    rp.add(tCount, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    
    JLabel lQuery = new JLabel("Query page: ");
    tQuery = new JTextField();
    tQuery.setText(cfg.getQueryPage());
    tQuery.setPreferredSize(new Dimension(150, 20));
    tQuery.setMinimumSize(tQuery.getPreferredSize());
    
    rp.add(lQuery, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    rp.add(tQuery, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    
    JButton save = new JButton("Save");
    save.setPreferredSize(new Dimension(100, 25));
    save.setMinimumSize(save.getPreferredSize());
    save.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveAction(e);
      }
    });
    
    rp.add(save, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    
    this.getContentPane().add(rp, BorderLayout.CENTER);
  }
  
  private void saveAction(ActionEvent e) {
    //iau ce era inainte si salvez doar daca este cazul
    JsoupEngine.JsoupConfig cfg = JsoupUtilsPref.loadCfgForUser();
    String dir = tDir.getText();
    if (!Strings.isNullOrEmpty(dir)) {
      cfg.setDownloadDir(new File(dir));
    }
    if (!Strings.isNullOrEmpty(tPages.getText())) {
      try {
        cfg.setMaxPage(Integer.parseInt(tPages.getText()));
      } catch (Exception ex) {
        java.util.logging.Logger.getLogger(JsoupDialog.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    if (!Strings.isNullOrEmpty(tCount.getText())) {
      try {
        cfg.setMaxCount(Integer.parseInt(tCount.getText()));
      } catch (Exception ex) {
        java.util.logging.Logger.getLogger(JsoupDialog.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    if (!Strings.isNullOrEmpty(tQuery.getText())) {
      cfg.setQueryPage(tQuery.getText());
    }
    
    JsoupUtilsPref.saveCfgForUser(cfg);
    engine.setCfg(cfg);
    dispose();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    dispose();
  }

}
