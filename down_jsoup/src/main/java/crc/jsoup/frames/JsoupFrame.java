package crc.jsoup.frames;

import crc.jsoup.service.JsoupEngine;
import crc.jsoup.service.impl.GoodFonJsoupEngineImpl;
import crc.jsoup.service.impl.JsoupUtilsPref;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

/**
 *
 * @author cosmin.i
 */
public class JsoupFrame extends JFrame {

  private final JTextArea console = new JTextArea(20, 30);
  private final JTextField searchTerm = new JTextField();
  private final JButton start = new JButton("Start");
  private final JButton clear = new JButton("Clear");
  private final JsoupEngine engine = new GoodFonJsoupEngineImpl();
  private DownloadEngine de = null;
  
  public JsoupFrame() {
    super("JsoupDownload");
    initGUI();
    engine.initEngine(JsoupUtilsPref.loadCfgForUser());
    console.setText(engine.getStatistics());
  }

  private void initGUI() {

    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.setPreferredSize(new Dimension(480, 320));
    
    JMenuBar menubar = new JMenuBar();
    JMenu menu = new JMenu("...");
    JMenuItem pref = new JMenuItem("Preferences");
    pref.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        launchDialog();
      }
    });
    menu.add(pref);
    menubar.add(menu);
    this.setJMenuBar(menubar);

    JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new GridBagLayout());

    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setPreferredSize(new Dimension(100, 300));
    buttonsPanel.setMinimumSize(buttonsPanel.getPreferredSize());
    buttonsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
    buttonsPanel.setLayout(new GridBagLayout());

    
    start.setPreferredSize(new Dimension(80, 25));
    start.setMnemonic('S');
    start.setMinimumSize(start.getPreferredSize());
    start.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        (de = new DownloadEngine()).execute();
        start.setEnabled(false);
        clear.setEnabled(false);
        searchTerm.setEnabled(false);
      }
    });

    JButton cancel = new JButton("Close");
    cancel.setPreferredSize(new Dimension(80, 25));
    cancel.setMnemonic('C');
    cancel.setMinimumSize(cancel.getPreferredSize());
    cancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    clear.setPreferredSize(new Dimension(80, 25));
    clear.setMinimumSize(clear.getPreferredSize());
    clear.setMnemonic('r');
    clear.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        console.setText(null);
        searchTerm.setText(null);
      }
    });

    JLabel dummy = new JLabel("");

    buttonsPanel.add(start, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(cancel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(clear, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    buttonsPanel.add(dummy, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

    JPanel textPanel = new JPanel();
    textPanel.setPreferredSize(new Dimension(100, 300));
    textPanel.setMinimumSize(textPanel.getPreferredSize());
    textPanel.setLayout(new GridBagLayout());

    searchTerm.setPreferredSize(new Dimension(160, 20));
    searchTerm.setMinimumSize(searchTerm.getPreferredSize());

    searchTerm.setText("test");
    JLabel searchTermLabel = new JLabel("Search: ");

    console.setEnabled(false);
    JScrollPane scroll = new JScrollPane(console);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    textPanel.add(searchTermLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 0, 0), 0, 0));
    textPanel.add(searchTerm, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 0, 0), 0, 0));
    textPanel.add(scroll, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(3, 0, 0, 0), 0, 0));

    mainPanel.add(textPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(buttonsPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    this.setLayout(new GridBagLayout());
    getContentPane().add(mainPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

  }
  
  private void launchDialog() {
    JsoupDialog dialog = new JsoupDialog(this, "Preferences", null, engine);
    dialog.setSize(400, 200);
  }

  private class DownloadEngine extends SwingWorker<Void, Void> {
    @Override
    protected Void doInBackground() throws Exception {
      engine.startDownload(searchTerm.getText());
      return null;
    }

    @Override
    protected void done() {
      start.setEnabled(true);
      clear.setEnabled(true);
      searchTerm.setEnabled(true);
      
      console.setText(engine.getStatistics());
    }
  }

}
