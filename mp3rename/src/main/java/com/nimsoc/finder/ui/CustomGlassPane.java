package com.nimsoc.finder.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

public interface CustomGlassPane {

  public void setText(String text);

  public void setText(String text, Color color);

  public void setImage(Icon imagine);

  public void inactivate();

  public void activate();

  public final static MouseListener SKIP_MOUSE_LISTENER = new MouseListener() {

    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getComponent().isVisible()) {
        e.consume();
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (e.getComponent().isVisible()) {
        e.consume();
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (e.getComponent().isVisible()) {
        e.consume();
      }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
  };
  public final static KeyListener SKIP_KEY_LISTENER = new KeyListener() {

    @Override
    public void keyTyped(KeyEvent e) {
      if (e.getComponent().isVisible()) {
        e.consume();
      }

    }

    @Override
    public void keyPressed(KeyEvent e) {
      if (e.getComponent().isVisible()) {
        e.consume();
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
      if (e.getComponent().isVisible()) {
        e.consume();
      }
    }
  };

  public static final InputVerifier SKIP_INPUT_VERIFIER = new InputVerifier() {
    @Override
    public boolean verify(JComponent input) {
      if (input.isVisible()) {
        return false;
      }
      return true;
    }
  };
}
