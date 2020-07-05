/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.form;

import com.harunaga.games.common.client.font.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Harunaga
 */
public class Menu extends JPanel implements ActionListener {

    public static int SELECT = 1;
    public static int DESELECT = 0;
    ImageIcon itemIcon = null;
    ImageIcon itemRolloverIcon;
    Font font = Font.getDefaultFont();
    ActionListener inputListener;
    int horizontalSlice = 0;
    float itemAlignment = SwingConstants.LEFT;

    public void setItemAlignment(float itemAlignment) {
        this.itemAlignment = itemAlignment;
    }

    public void setHorizontalSlice(int horizontalSlice) {
        this.horizontalSlice = horizontalSlice;
    }

    public void setListener(ActionListener listener) {
        this.inputListener = listener;
    }

    public Menu(ImageIcon itemIcon, ImageIcon itemRolloverIcon, ActionListener listener) {
        this.itemRolloverIcon = itemRolloverIcon;
        this.itemIcon = itemIcon;
        this.inputListener = listener;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        font = Font.getDefaultFont();
        setOpaque(false);
    }

    public void setFont(String fontName) {
        font = Font.getFont(fontName);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Component com : getComponents()) {
            com.setEnabled(enabled);
        }
    }

    public void addItem(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key null");
        }
        Item item = new Item(key, value);
        item.addActionListener(this);
        item.setHorizontalSlice(horizontalSlice);
        item.setHorizontalTextPosition(SwingConstants.CENTER);
        item.setIcon(itemIcon);
        item.setRolloverIcon(itemRolloverIcon);
        item.setForeground(getForeground());
//        item.setDrawIconSeperately(true);
        item.setFont(font);
        add(item);
        item.setAlignmentX(itemAlignment);
    }

//    public static void main(String[] args) throws IOException {
//        JFrame frame = new JFrame("test");
////        frame.setSize(200, 300);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        Image img = ImageIO.read(new File("test.png"));
//        HListBox box = new HListBox(img);
//        box.addItem("1", "one two");
//        box.addItem("2", "two three");
//        box.addItem("3", "three four");
//        box.setSize(200, 100);
//        frame.setLayout(new BorderLayout());
//        frame.getContentPane().add(box, BorderLayout.CENTER);
//        frame.pack();
//        frame.setVisible(true);
//    }
    public synchronized void actionPerformed(ActionEvent e) {

        ActionListener listener = inputListener;
        if (listener != null) {
            if (e.getSource() instanceof Item) {
                listener.actionPerformed(new ActionEvent(this, SELECT, ((Item) e.getSource()).value));
            } else {
                listener.actionPerformed(new ActionEvent(this, DESELECT, null));
            }
        }
    }
}
