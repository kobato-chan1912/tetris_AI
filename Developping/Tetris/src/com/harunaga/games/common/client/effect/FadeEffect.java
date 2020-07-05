/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.effect;

import com.harunaga.games.common.client.font.Font;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 *
 * @author Harunaga
 */
public class FadeEffect implements Effect {

    AlphaComposite transparency05 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    AlphaComposite transparency025 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f);
    int time;
    int duration;
    Font font;
    int x, y;

    public void setFont(Font font) {
        this.font = font;
    }

    public FadeEffect(int duration, int x, int y) {
        this.duration = duration;
        time = duration;
        this.x = x;
        this.y = y;
    }

    public boolean isProcessing(long elapsedTime) {
        if (time > 0) {
            time -= elapsedTime;
            return true;
        } else {
            return false;
        }
    }

    public void draw(String text, Graphics2D g) {
        if (font == null) {
            font = Font.getDefaultFont();
        }
        setAlpha(g);
        if (time > 0) {
            font.drawString(text, x, y, g);
        }
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void setAlpha(Graphics2D g) {
        if (time < (duration >> 2)) {
            g.setComposite(transparency025);
        } else if (time < (duration >> 1)) {
            g.setComposite(transparency05);
        }
    }

    public void draw(Image image, Graphics2D g) {
        setAlpha(g);
        if (time > 0) {
            g.drawImage(image, x, y, null);
        }
        g.setComposite(AlphaComposite.SrcOver);
    }

    public void draw(Graphics2D g) {
    }
}
