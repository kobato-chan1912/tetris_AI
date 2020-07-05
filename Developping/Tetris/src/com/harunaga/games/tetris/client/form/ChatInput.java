/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.form;

import com.harunaga.games.common.client.input.HTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Harunaga
 */
public class ChatInput extends JPanel implements ActionListener {

    ActionListener inputListener;
    HTextField input;
    public static ChatInput instance = new ChatInput(null);

    public ChatInput(ActionListener listener) {
        inputListener = listener;
        input = new HTextField(50);
        JLabel label = new JLabel("Message:");
        label.setLabelFor(input);
        add(label);
        add(input);
        setSize(getPreferredSize());
        input.addActionListener(this);
    }

    public void clear() {
        input.setText("");
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            input.requestFocus();
        }
    }

    public void setListener(ActionListener inputListener) {
        this.inputListener = inputListener;
    }

    public void actionPerformed(ActionEvent e) {
        ActionListener listener = inputListener;
        if (listener != null) {
            listener.actionPerformed(new ActionEvent(this, 1, input.getText()));
        }
    }
}
