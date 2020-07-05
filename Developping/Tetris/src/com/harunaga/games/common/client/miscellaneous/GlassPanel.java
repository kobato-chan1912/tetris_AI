/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.miscellaneous;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Harunaga
 */
public class GlassPanel extends Container
        implements KeyListener, MouseListener {

    public GlassPanel() {
        addKeyListener(this);
        addMouseListener(this);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
