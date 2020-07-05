/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JTextField;

/**
 *
 * @author Harunaga
 */
public class HTextField extends JTextField {

    public HTextField() {
        super();
    }

    
    public HTextField(int columns) {
        super(columns);
        enableEvents(KeyEvent.KEY_EVENT_MASK
                | MouseEvent.MOUSE_EVENT_MASK
                | MouseEvent.MOUSE_MOTION_EVENT_MASK
                | MouseEvent.MOUSE_WHEEL_EVENT_MASK);
        setIgnoreRepaint(true);
    }

    // alternative way to intercept key events
    @Override
    protected void processKeyEvent(KeyEvent e) {
        synchronized (getTreeLock()) {//avoid deadlock
            super.processKeyEvent(e);
        }
    }
    // alternative way to intercept mouse events

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
    }

    // alternative way to intercept mouse events
    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        e.consume();
    }

    // alternative way to intercept mouse events
    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e) {
        e.consume();
    }

    @Override
    public void setText(String t) {
        synchronized (getTreeLock()) {  //avoid deadlock
            super.setText(t);
            // System.out.println("set text" + t);
        }
    }
}
