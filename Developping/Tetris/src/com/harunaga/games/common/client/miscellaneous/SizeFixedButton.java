/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.miscellaneous;

import com.harunaga.games.common.client.font.Font;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

/**
 *
 * @author Harunaga
 */
public class SizeFixedButton extends JButton implements KeyListener {

    ImageIcon rolloverIcon = null;
    ImageIcon icon = null;
    ImageIcon disableIcon = null;
    int horizontalSlice = 0;
    int verticalSlice = 0;
    Dimension fixedSize = null;
    boolean drawIconSeperately = true;
    Font font = Font.getDefaultFont();
    int offsetX = 0;
    int offsetY = 0;

    public void setFont(Font font) {
        if (font != null) {
            this.font = font;
        }
    }

    public void setDrawIconSeperately(boolean aFlag) {
        drawIconSeperately = aFlag;
    }

    public void setVerticalSlice(int verticalSlice) {
        this.verticalSlice = verticalSlice;
        if (fixedSize != null) {
            int h = 0;
            if (icon != null) {
                h = Math.max(h, icon.getIconHeight());
                h = Math.max(h, rolloverIcon.getIconHeight());
            }
            if (h != 0) {
                h += horizontalSlice;
                fixedSize = new Dimension(fixedSize.width, h);
            }
        }
    }

    public void setHorizontalSlice(int horizontalSlice) {
        this.horizontalSlice = horizontalSlice;
        if (fixedSize != null) {
            int w = 0;
            if (icon != null) {
                w = Math.max(w, icon.getIconWidth());
                w = Math.max(w, rolloverIcon.getIconWidth());
            }
            if (w != 0) {
                w += horizontalSlice;
                fixedSize = new Dimension(w, fixedSize.height);
            }
        }
    }

    public SizeFixedButton() {
        init();
    }

    public SizeFixedButton(String text) {
        setText(text);
        init();
    }

    private void init() {
        addKeyListener(this);
    }

    /**
     * set the size for the button. the button's size will be <strong>update</strong> if <br>
     * the method <strong>setRolloverIcon</strong> or <strong>setIcon</strong> is invoked
     * @param fixedSize the size for this button
     */
    public void setFixedSize(Dimension fixedSize) {
        this.fixedSize = fixedSize;
    }

    /**
     * Sets the rollover icon for the button.
     *<br>call this method will update the size for this button
     * @param rolloverIcon the icon used as the "rollover" image
     * @see #getRolloverIcon
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The rollover icon for the button.
     */
    @Override
    public void setRolloverIcon(Icon rolloverIcon) {
        this.rolloverIcon = (ImageIcon) rolloverIcon;
        if (fixedSize == null) {
            fixedSize = new Dimension(rolloverIcon.getIconWidth() + Math.abs(horizontalSlice),
                    rolloverIcon.getIconHeight() + Math.abs(verticalSlice));
        } else {
            int w = Math.max(fixedSize.width - Math.abs(horizontalSlice), rolloverIcon.getIconWidth());
            int h = Math.max(fixedSize.height - Math.abs(verticalSlice), rolloverIcon.getIconHeight());
            fixedSize = new Dimension(w + Math.abs(horizontalSlice), h + Math.abs(verticalSlice));
        }
    }

    /**
     * Sets the button's default icon. This icon is
     * also used as the "pressed" and "disabled" icon if
     * there is no explicitly set pressed icon.
     * call this method will update the size for this button
     * @param defaultIcon the icon used as the default image
     * @see #getIcon
     * @see #setPressedIcon
     * @beaninfo
     *           bound: true
     *       attribute: visualUpdate true
     *     description: The button's default icon
     */
    @Override
    public void setIcon(Icon defaultIcon) {
        icon = (ImageIcon) defaultIcon;
        if (rolloverIcon == null) {
            rolloverIcon = icon;
        }
        if (fixedSize == null) {
            fixedSize = new Dimension(defaultIcon.getIconWidth() + Math.abs(horizontalSlice),
                    defaultIcon.getIconHeight() + Math.abs(verticalSlice));
        } else {
            int w = Math.max(fixedSize.width - Math.abs(horizontalSlice), defaultIcon.getIconWidth());
            int h = Math.max(fixedSize.height - Math.abs(verticalSlice), defaultIcon.getIconHeight());
            fixedSize = new Dimension(w + Math.abs(horizontalSlice), h + Math.abs(verticalSlice));
        }
    }

    /**
     * Sets the disabled icon for the button.
     * <br>call this method will <strong>not</strong> update the size for the button
     * @param disabledIcon the icon used as the disabled image
     * @see #getDisabledIcon
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The disabled icon for the button.
     */
    @Override
    public void setDisabledIcon(Icon disabledIcon) {
        disabledIcon = (ImageIcon) disabledIcon;
    }

    @Override
    public Dimension getPreferredSize() {
        if (fixedSize == null) {
            return super.getPreferredSize();
        }
        return fixedSize;
    }

    @Override
    public Dimension getMinimumSize() {
        if (fixedSize == null) {
            return super.getMinimumSize();
        }
        return fixedSize;
    }

    @Override
    public Dimension getMaximumSize() {
        if (fixedSize == null) {
            return super.getMaximumSize();
        }
        return fixedSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth() - Math.abs(horizontalSlice);
        int h = getHeight() - Math.abs(verticalSlice);
        int x = horizontalSlice < 0 ? -horizontalSlice : 0,
                y = verticalSlice < 0 ? -verticalSlice : 0;
        if (isEnabled()) {
            boolean drawIcon = true;
            if (model.isPressed()) {
                x++;
                y += 2;
            }
            if (model.isRollover() || hasFocus()) {
                if (offsetX < horizontalSlice) {
                    offsetX++;
                } else if (offsetX > horizontalSlice) {
                    offsetX--;
                }
                if (offsetY < verticalSlice) {
                    offsetY++;
                } else if (offsetY > verticalSlice) {
                    offsetY--;
                }
                x += offsetX;
                y += offsetY;
                if (rolloverIcon != null) {
//                    System.out.println("Me kiep. sao ay nhi: " + getText() + " x: " + x + " y:" + y);
                    g.drawImage(rolloverIcon.getImage(), x + ((w - rolloverIcon.getIconWidth()) >> 1),
                            y + ((h - rolloverIcon.getIconHeight()) >> 1),
                            this);
                    if (drawIconSeperately) {
                        drawIcon = false;
                    }
                }
            } else {
                offsetX = offsetY = 0;
            }
            if (icon != null && drawIcon) {
                g.drawImage(icon.getImage(), x + ((w - icon.getIconWidth()) >> 1),
                        y + ((h - icon.getIconHeight()) >> 1),
                        this);
            }
//            else {
//                super.paint(g);
//            }
            drawText(x, y, w, h, g);
        } else if (disableIcon != null) {
            g.drawImage(disableIcon.getImage(), x, y, this);
            drawText(x, y, w, h, g);
        } else {
            super.paintComponent(g);
        }

    }

    private void drawText(int x, int y, int w, int h, Graphics g) {
        String text = getText();
        if (text != null) {
            g.setColor(getForeground());
            Insets inset = getMargin();
            font.drawString(text, x + inset.left, y + inset.top, g,
                    w - inset.right - inset.left, h - inset.bottom - inset.top,
                    getHorizontalTextPosition(), getVerticalTextPosition());
        }
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("test");
////        frame.setSize(200, 300);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        ActionListener listener = new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("hi hi hi");
//            }
//        };
//        SizeFixedButton a = new SizeFixedButton("a aaaaaa");
////        SizeFixedButton b = new SizeFixedButton("ab");
////        SizeFixedButton c = new SizeFixedButton("abc");
////        SizeFixedButton d = new SizeFixedButton("abcd");
//        ImageIcon icon = new ImageIcon("back.png");
//        if (icon.getImage() == null) {
//            System.out.println("Nulllllllllllllllll");
//        }
//        a.setIcon(icon);
////        b.setIcon(icon);
////        c.setIcon(icon);
////        d.setIcon(icon);
//        icon = new ImageIcon("Smallfocus.png");
//        if (icon.getImage() == null) {
//            System.out.println("Nulllllllllllllllll");
//        }
//        a.setHorizontalTextPosition(SwingConstants.CENTER);
//        a.setVerticalTextPosition(SwingConstants.CENTER);
//        a.setRolloverIcon(icon);
////        b.setRolloverIcon(icon);
////        c.setRolloverIcon(icon);
////        d.setRolloverIcon(icon);
//        //a.setEnabled(false);
//        frame.getContentPane().setLayout(new FlowLayout());
//        frame.getContentPane().add(a);
////        frame.getContentPane().add(b);
////        frame.getContentPane().add(c);
////        frame.getContentPane().add(d);
////        b.setEnabled(false);
//        a.setToolTipText("hi hi hi hi");
//        frame.pack();
//        frame.setVisible(true);
//    }
    public final void keyTyped(KeyEvent e) {
    }

    public synchronized final void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && isShowing()) {
            fireActionPerformed(new ActionEvent(this, 154, "fired via press enter"));
        }
    }

    public final void keyReleased(KeyEvent e) {
    }
}
