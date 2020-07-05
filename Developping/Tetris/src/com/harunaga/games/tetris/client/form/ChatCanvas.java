/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.form;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Harunaga
 */
public class ChatCanvas extends JScrollPane {

    Dimension maxSize;
    JTextArea chatBox;

    public void setMaxSize(Dimension size) {
        maxSize = size;
    }

    public ChatCanvas() {
        super();
        chatBox = new JTextArea(30, 30);
        chatBox.setEditable(false);
        chatBox.setIgnoreRepaint(true);
        chatBox.setFocusable(false);
        setViewportView(chatBox);
        setIgnoreRepaint(true);
        maxSize = new Dimension(400, 600);
    }

    public void clearMessage() {
        Runnable updateMsgsText = new Runnable() {

            public void run() {
                chatBox.setText("");
            }
        };
        SwingUtilities.invokeLater(updateMsgsText);
    }

    public void putMessage(final String message) {
        Runnable updateMsgsText = new Runnable() {

            public void run() {
                chatBox.append(message + "\n");
                // move insertion point to the end of the text
                chatBox.setCaretPosition(chatBox.getText().length());
            }
        };
        SwingUtilities.invokeLater(updateMsgsText);
    }

//    @Override
//    public Dimension getPreferredSize() {
//        return preferredSize;
//    }
//
//    @Override
//    public Dimension getMinimumSize() {
//        return preferredSize;
//    }
    @Override
    public Dimension getMaximumSize() {
        return maxSize;
    }
}
