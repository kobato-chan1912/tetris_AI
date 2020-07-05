/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.font;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.RenderingHints;

/**
 *
 * @author Harunaga
 */
class SFont extends Font {

    private java.awt.Font font;
    private java.awt.FontMetrics metrics;

    SFont(String face, int style, int size) {
        font = new java.awt.Font(face, style, size);
        metrics = new Label().getFontMetrics(font);
    }

    public int getHeight() {
        return metrics.getHeight();
    }

    public int charWidth(char ch) {
        return metrics.charWidth(ch);
    }

    public int stringWidth(String s) {
        return metrics.stringWidth(s);
    }

    public void drawString(String s, int x, int y, Graphics g) {
        if (s == null) {
            return;
        }
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(font);
        g.drawString(s, x, y + metrics.getMaxAscent());
    }
}
