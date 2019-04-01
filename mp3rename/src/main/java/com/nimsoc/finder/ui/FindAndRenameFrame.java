package com.nimsoc.finder.ui;

import com.nimsoc.finder.impl.Mp3Finder;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class FindAndRenameFrame extends JFrame {

  public FindAndRenameFrame() {
    guiInit();
    restorFromPreferences();
  }

  private JTextArea display = null;
  private JFileChooser fcMain = null;
  private JFileChooser fcMove = null;
  private JTextField textMainFolder = null;
  private JTextField textMoveFolder = null;
  private JCheckBox chkMoveDuplicateFiles = null;
  private JCheckBox chkDeleteFilesByExtension = null;
  private JCheckBox chkRenameFiles = null;
  private JSpinner noOfDifChars = null;

  CustomGlassPane glass = new CustomGlassPaneImpl();

  private void guiInit() {
    glass.setText(null);
    this.setGlassPane((JPanel) glass);

    try {
      this.setIconImage(ImageIO.read(FindAndRenameFrame.class.getResourceAsStream("frame.png")));
    } catch (IOException ex) {
      Logger.getLogger(FindAndRenameFrame.class.getName()).log(Level.SEVERE, null, ex);
    }

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridBagLayout());

    JPanel firstPanel = new JPanel();
    firstPanel.setLayout(new GridBagLayout());
    firstPanel.setBorder(new TitledBorder(new EtchedBorder(), "Display Area"));

    display = new JTextArea(16, 48);
    display.setEditable(false);
    JScrollPane scroll = new JScrollPane(display);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    firstPanel.add(scroll, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

    JPanel secondPanel = new JPanel();
    secondPanel.setLayout(new GridBagLayout());
    secondPanel.setBorder(new TitledBorder(new EtchedBorder(), "Files Area"));

    JButton btnMainFolder = new JButton();
    btnMainFolder.setMinimumSize(new Dimension(80, 30));
    btnMainFolder.setPreferredSize(btnMainFolder.getMinimumSize());
    btnMainFolder.setText("Main Dir.");
    btnMainFolder.addActionListener(this::btnMainFolder_pressed);

    JButton btnMoveFolder = new JButton();
    btnMoveFolder.setMinimumSize(new Dimension(80, 30));
    btnMoveFolder.setPreferredSize(btnMainFolder.getMinimumSize());
    btnMoveFolder.setText("Move Dir.");
    btnMoveFolder.addActionListener(this::btnMoveFolder_pressed);

    textMainFolder = new JTextField();
    textMainFolder.setMinimumSize(new Dimension(200, 30));
    textMainFolder.setPreferredSize(textMainFolder.getMinimumSize());
    textMainFolder.setEditable(false);

    textMoveFolder = new JTextField();
    textMoveFolder.setMinimumSize(new Dimension(200, 30));
    textMoveFolder.setPreferredSize(textMoveFolder.getMinimumSize());
    textMoveFolder.setEditable(false);

    secondPanel.add(btnMainFolder, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
    secondPanel.add(textMainFolder, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
    secondPanel.add(btnMoveFolder, new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
    secondPanel.add(textMoveFolder, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));

    JPanel thirdPanel = new JPanel();
    thirdPanel.setLayout(new GridBagLayout());
    thirdPanel.setBorder(new TitledBorder(new EtchedBorder(), "Action Area"));

    chkMoveDuplicateFiles = new JCheckBox();
    chkMoveDuplicateFiles.setMinimumSize(new Dimension(100, 25));
    chkMoveDuplicateFiles.setPreferredSize(chkMoveDuplicateFiles.getMinimumSize());
    chkMoveDuplicateFiles.setText("Move duplicate files");
    chkMoveDuplicateFiles.setSelected(false);

    chkDeleteFilesByExtension = new JCheckBox();
    chkDeleteFilesByExtension.setMinimumSize(new Dimension(100, 30));
    chkDeleteFilesByExtension.setPreferredSize(chkDeleteFilesByExtension.getMinimumSize());
    chkDeleteFilesByExtension.setText("Delete files by extension");
    chkDeleteFilesByExtension.setSelected(false);

    chkRenameFiles = new JCheckBox();
    chkRenameFiles.setMinimumSize(new Dimension(100, 30));
    chkRenameFiles.setPreferredSize(chkRenameFiles.getMinimumSize());
    chkRenameFiles.setText("Rename files");
    chkRenameFiles.setSelected(false);

    JLabel labelDifChars = new JLabel();
    labelDifChars.setMinimumSize(new Dimension(100, 30));
    labelDifChars.setPreferredSize(labelDifChars.getMinimumSize());
    labelDifChars.setText("Chars diff. file name");

    noOfDifChars = new JSpinner();
    noOfDifChars.setMinimumSize(new Dimension(45, 30));
    noOfDifChars.setPreferredSize(noOfDifChars.getMinimumSize());
    noOfDifChars.setModel(new SpinnerNumberModel(Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(10), Integer.valueOf(1)));

    JButton btnStart = new JButton();
    btnStart.setMinimumSize(new Dimension(80, 30));
    btnStart.setPreferredSize(btnMainFolder.getMinimumSize());
    btnStart.setText("Start");
    btnStart.addActionListener(this::btnStartAction_pressed);

    thirdPanel.add(chkMoveDuplicateFiles, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
    thirdPanel.add(chkDeleteFilesByExtension, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
    thirdPanel.add(chkRenameFiles, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));

    thirdPanel.add(labelDifChars, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
    thirdPanel.add(noOfDifChars, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
    thirdPanel.add(btnStart, new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));

    mainPanel.add(firstPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(secondPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(thirdPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
    this.getContentPane().add(mainPanel);

    fcMain = new JFileChooser(new File("."));
    fcMain.setDialogTitle("Choose main directory");
    fcMain.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fcMain.setAcceptAllFileFilterUsed(false);

    fcMove = new JFileChooser(new File("."));
    fcMove.setDialogTitle("Choose move directory");
    fcMove.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fcMove.setAcceptAllFileFilterUsed(false);

    this.setMinimumSize(new Dimension(800, 600));
    this.setPreferredSize(this.getMinimumSize());

    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
    this.setLocation(x, y);
  }

  private void btnMainFolder_pressed(ActionEvent e) {
    if (fcMain.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      textMainFolder.setText(fcMain.getSelectedFile().getAbsolutePath());
    } else {
      //no selection
    }
  }

  private void btnMoveFolder_pressed(ActionEvent e) {
    if (fcMove.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      textMoveFolder.setText(fcMove.getSelectedFile().getAbsolutePath());
    } else {
      //no selection
    }
  }

  private void btnStartAction_pressed(ActionEvent e) {
    if (textMainFolder.getText() == null || textMainFolder.getText().isEmpty()) {
      JOptionPane.showMessageDialog(this.getParent(), "Choose 'Main Dir.'", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (textMoveFolder.getText() == null || textMoveFolder.getText().isEmpty()) {
      JOptionPane.showMessageDialog(this.getParent(), "Choose 'Move Dir.'", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    saveToPreferences();
    glass.activate();
    new FinderWorker(
            glass,
            ((Integer)noOfDifChars.getValue()).intValue(),
            chkMoveDuplicateFiles.isSelected(),
            chkDeleteFilesByExtension.isSelected(),
            chkRenameFiles.isSelected(),
            textMainFolder.getText(),
            textMoveFolder.getText(),
            display
    ).execute();
  }

  private static class FinderWorker extends SwingWorker<String, String> {

    private final CustomGlassPane cgp;
    private final int noOfDifChars;
    private final boolean moveDuplicateFiles;
    private final boolean deleteFileByExtension;
    private final boolean renameFileByName;
    private final String mainDir;
    private final String moveDir;
    private final JTextArea textArea;

    public FinderWorker(CustomGlassPane cgp, int noOfDifChars, boolean moveDuplicateFiles, boolean deleteFileByExtension, boolean renameFileByName, String mainDir, String moveDir, JTextArea textArea) {
      this.cgp = cgp;
      this.noOfDifChars = noOfDifChars;
      this.moveDuplicateFiles = moveDuplicateFiles;
      this.deleteFileByExtension = deleteFileByExtension;
      this.renameFileByName = renameFileByName;
      this.mainDir = mainDir;
      this.moveDir = moveDir;
      this.textArea = textArea;
    }

    @Override
    protected String doInBackground() throws Exception {
      File startingDir = new File(mainDir);
      File renameToDir = new File(moveDir);
      Mp3Finder finder = new Mp3Finder(renameToDir, noOfDifChars, moveDuplicateFiles, deleteFileByExtension, renameFileByName);
      Files.walkFileTree(startingDir.toPath(), finder);
      String result = finder.printStats();
      publish();
      return result;
    }

    @Override
    protected void process(List<String> chunks) {
    }

    @Override
    protected void done() {
      super.done();
      cgp.inactivate();
      try {
        textArea.setText(this.get());
      } catch (InterruptedException | ExecutionException ex) {
        Logger.getLogger(FindAndRenameFrame.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
  
  private final String PREF_PATH = "com/nimsoc/finder/mp3rename/prefs";
  
  private void saveToPreferences() {
    Preferences p = Preferences.userRoot().node(PREF_PATH);
    p.put("main", textMainFolder.getText());
    p.put("move", textMoveFolder.getText());
    //...
  }
  
  private void restorFromPreferences() {
    Preferences p = Preferences.userRoot().node(PREF_PATH);
    textMainFolder.setText(p.get("main", null));
    textMoveFolder.setText(p.get("move", null));
  }
}
