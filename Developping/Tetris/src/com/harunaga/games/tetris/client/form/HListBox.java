/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.form;

import com.harunaga.games.common.client.font.Font;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Harunaga
 */
public class HListBox extends JScrollPane implements ActionListener {

    public static int SELECT = 1;
    public static int DESELECT = 0;
    Dimension maxSize;
    ImageIcon itemRolloverIcon;
    Font font;
    ActionListener inputListener;
    String selectionItem;
    JPanel pane;

    public String getSelectionItem() {
        return selectionItem;
    }

    public void setListener(ActionListener listener) {
        this.inputListener = listener;
    }

    public HListBox(ImageIcon rolloverItem, ActionListener listener) {
        itemRolloverIcon = rolloverItem;
        inputListener = listener;
        font = Font.getDefaultFont();
        pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setOpaque(false);
        setViewportView(pane);
        setIgnoreRepaint(true);
        setOpaque(false);
        setBorder(null);
        getViewport().setOpaque(false);
        getViewport().setBorder(null);
        maxSize = new Dimension(rolloverItem.getIconWidth() + 30, 100);
        //maxSize = new Dimension(400, 600);
//        setSize(new Dimension(rolloverItem.getIconWidth(), 50));
    }

    public void clear() {
        pane.removeAll();
    }

    public void setFont(String fontName) {
        font = Font.getFont(fontName);
    }

    public void addItem(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key null");
        }
        Item item = new Item(key, value);
        item.addActionListener(this);
        item.setHorizontalTextPosition(SwingConstants.LEFT);
        item.setRolloverIcon(itemRolloverIcon);
        item.setForeground(getForeground());
//        item.setDrawIconSeperately(true);
        item.setFont(font);
        item.setMargin(new Insets(0, 10, 0, 0));
        add(item);
        pane.add(item);
        // pane.setSize(pane.getPreferredSize());
    }

//    public static void main(String[] args) throws IOException {
//        JFrame frame = new JFrame("test");
////        frame.setSize(200, 300);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        Image img = ImageIO.read(new File("textrollover.png"));
//        HListBox box = new HListBox(new ImageIcon(img), new ActionListener() {
//
//            public void actionPerformed(Object provider) {
//                System.out.println("aaaaaa");
//            }
//        });
//        box.addItem("1", "one two");
//        box.addItem("2", "two three");
//        box.addItem("3", "three four");
//        box.addItem("4", "abc four");
//        box.addItem("5", "abc four");
//        box.addItem("6", "abc four");
//        box.addItem("7", "abc four");
//        box.addItem("8", "abc four");
//        frame.setLayout(new BorderLayout());
//        frame.getContentPane().add(box, BorderLayout.CENTER);
//        frame.setSize(frame.getPreferredSize());
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

    @Override
    public Dimension getMinimumSize() {
        return maxSize;
    }

    @Override
    public Dimension getPreferredSize() {
        return maxSize;
    }

    @Override
    public Dimension getMaximumSize() {
        return maxSize;
    }
}
