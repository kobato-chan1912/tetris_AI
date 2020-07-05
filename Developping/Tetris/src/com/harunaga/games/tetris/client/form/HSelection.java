/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.form;

import com.harunaga.games.common.client.font.Font;
import com.harunaga.games.common.client.miscellaneous.SizeFixedLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Harunaga
 */
public class HSelection extends Container implements ActionListener {

    SizeFixedLabel title;
    JButton decresea;
    JButton increase;
    SizeFixedLabel main;
    ActionListener actionListener;
    ArrayList<String> values;
    ArrayList<String> keys;
    int index;
    boolean vertical;

    /**
     * 
     * @param text
     * @param decrese
     * @param increase
     */
    public HSelection(ImageIcon mainIcon, JButton decrease, JButton increase) {
        this.decresea = decrease;
        this.increase = increase;
        setLayout(new BorderLayout(1, 1));
        add(decrease, BorderLayout.LINE_START);
        add(increase, BorderLayout.LINE_END);
        keys = new ArrayList<String>(10);
        values = new ArrayList<String>(10);
        increase.addActionListener(this);
        decrease.addActionListener(this);
        main = new SizeFixedLabel(mainIcon);
        main.setHorizontalTextPosition(JLabel.CENTER);
        main.setVerticalTextPosition(JLabel.CENTER);
        add(main, BorderLayout.CENTER);
    }

    public HSelection(String text) {
        this(null, new JButton("<"), new JButton(">"));
    }

    @Override
    public void setForeground(Color c) {
        main.setForeground(c);
    }

    public void setFont(Font font) {
        main.setFont(font);
    }

    public void setIncreaseButton(JButton increaseBt) {
        increase = increaseBt;
    }

    public void setDecreseaButton(JButton decreseaBt) {
        decresea = decreseaBt;
    }

    public void setTitle(String text) {
        if (title == null) {
            title = new SizeFixedLabel(text);
            title.setFixedSize(main.getMaximumSize());
            title.setHorizontalTextPosition(JLabel.CENTER);
            title.setVerticalTextPosition(JLabel.CENTER);
            JPanel titlePane = new JPanel();
            titlePane.setLayout(vertical ? new BoxLayout(title, BoxLayout.X_AXIS)
                    : new FlowLayout(FlowLayout.CENTER));
            title.setAlignmentY(JLabel.CENTER_ALIGNMENT);
            titlePane.add(title);
            add(titlePane, vertical ? BorderLayout.LINE_START : BorderLayout.NORTH);
        } else {
            title.setText(text);
        }
    }

    public void setTitleFont(Font font) {
        title.setFont(font);
    }

    public void setTitleColor(Color color) {
    }

    public void setListener(ActionListener inputlistener) {
        this.actionListener = inputlistener;
    }

    public void setVertival(boolean flag) {
        vertical = flag;
        if (flag) {
            removeAll();
            add(decresea, BorderLayout.SOUTH);
            add(increase, BorderLayout.NORTH);
            add(main, BorderLayout.CENTER);
            if (title != null) {
                JPanel titlePane = new JPanel();
                title.setAlignmentY(JLabel.CENTER_ALIGNMENT);
                titlePane.add(title);
                add(titlePane, BorderLayout.LINE_START);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (keys.isEmpty()) {
            return;
        }
        Object src = e.getSource();
        if (src == increase) {
            if (++index >= keys.size()) {
                index = 0;
            }
            main.setText(keys.get(index));
        } else {
            if (--index < 0) {
                index = keys.size() - 1;
            }
            main.setText(keys.get(index));
        }
        ActionListener listener = actionListener;
        if (listener != null) {
            listener.actionPerformed(new ActionEvent(this, e.getID(), values.get(index),
                    e.getWhen(), e.getModifiers()));
        }
    }

    public void addOption(String key, String value) {
        if (keys.isEmpty()) {
            main.setText(key);
            add(main, BorderLayout.CENTER);
        }
        keys.add(key);
        values.add(value);
    }
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("HselectionTest");
//        HSelection selection = new HSelection("team");
//        selection.addOption("1", "1");
//        selection.addOption("2", "2");
//        selection.addOption("3", "3");
//        selection.setVertival(true);
//        frame.add(selection);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 400);
//        frame.setVisible(true);
//    }
}
