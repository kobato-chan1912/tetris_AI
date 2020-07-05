/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.miscellaneous;

import com.harunaga.games.common.client.font.Font;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Harunaga
 */
public class SizeFixedLabel extends JLabel {

    Font font = Font.getDefaultFont();
    ImageIcon icon;
    Dimension fixedSize = null;

    public SizeFixedLabel(Icon image) {
        super(image);
        setIcon(image);
    }

    public SizeFixedLabel(String text) {
        super(text);
    }

    public void setFont(Font font) {
        if (font != null) {
            this.font = font;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Insets inset = getInsets();
        if (icon != null) {
            g.drawImage(icon.getImage(), 0, 0, null);
        }
        String text = getText();
        if (text != null) {
            g.setColor(getForeground());
            font.drawString(text, inset.left, inset.top, g,
                    getWidth() - inset.right - inset.left,
                    getHeight() - inset.bottom - inset.top,
                    getHorizontalTextPosition(), getVerticalTextPosition());
        }
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
        if (defaultIcon == null) {
            return;
        }
        icon = (ImageIcon) defaultIcon;
        if (fixedSize == null) {
            fixedSize = new Dimension(defaultIcon.getIconWidth(), defaultIcon.getIconHeight());
        } else {
            fixedSize = new Dimension(Math.max(fixedSize.width, defaultIcon.getIconWidth()),
                    Math.max(fixedSize.height, defaultIcon.getIconHeight()));
        }
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
}
