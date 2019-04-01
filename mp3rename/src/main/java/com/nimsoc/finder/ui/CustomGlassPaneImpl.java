package com.nimsoc.finder.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CustomGlassPaneImpl extends JPanel implements CustomGlassPane {

  private JLabel text = new JLabel();
  private GridBagLayout gbl = new GridBagLayout();

  public CustomGlassPaneImpl() {
    try {
      jbInit();
    } catch (Exception e) {
    }
  }

  private ImageIcon getDefaultIcon() {
    return new ImageIcon(this.getClass().getResource("wait.gif"));
  }

  private void jbInit() throws Exception {
    this.setLayout(gbl);
    this.setOpaque(false);

    text.setHorizontalAlignment(SwingConstants.CENTER);
    text.setIconTextGap(30);
    text.setPreferredSize(new Dimension(640, 480));
    text.setMinimumSize(new Dimension(640, 480));
    text.setOpaque(false);
    text.setIcon(getDefaultIcon());
    text.setFont(new Font("Tahoma", 1, 12));

    this.setInputVerifier(SKIP_INPUT_VERIFIER);
    this.addKeyListener(SKIP_KEY_LISTENER);
    this.addMouseListener(SKIP_MOUSE_LISTENER);
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    this.add(text,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
  }

  @Override
  public void setText(String text) {
    if (text != null && !text.isEmpty()) {
      this.text.setText(text);
      this.text.setForeground(Color.black);
    }
  }

  @Override
  public void setText(String text, Color color) {
    if (text != null && !text.isEmpty()) {
      this.text.setText(text);
      this.text.setForeground(color);
    }
  }

  @Override
  public void setImage(Icon imagine) {
    this.text.setIcon(imagine);
  }

  @Override
  public void inactivate() {
    if (!this.isVisible()) {
      return;
    }
    this.setVisible(false);
  }

  @Override
  public void activate() {
    if (this.isVisible()) {
      return;
    }
    this.setVisible(true);
    this.requestFocus();
  }

}
