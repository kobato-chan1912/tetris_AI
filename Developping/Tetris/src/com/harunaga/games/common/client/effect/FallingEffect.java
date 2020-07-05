/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.effect;

import com.harunaga.games.common.client.font.Font;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 *
 * @author MaiHoang146ThaiHa
 */
public class FallingEffect implements Effect {

    public static final float GRAVITY = 0.0015f;
    Font font;
    //for calculating
//    int startY;
    int endY;
    float currenty;
    int x;
    float dy = 0f;
    int count;

    public int getX() {
        return x;
    }

    public int getY() {
        return Math.round(currenty);
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public FallingEffect(int startY, int height, int x) {
        count = 5;
//        this.startY = startY;
        endY = startY + height;
        this.x = x;
        currenty = startY;
    }

    public void setinitialVelocity(float v) {
        dy = v;
    }

    public boolean isProcessing(long elapsedTime) {
        if (count > 0) {
            dy += GRAVITY * elapsedTime;
            currenty += dy * elapsedTime;
            if (currenty >= endY) {
                currenty = endY;
                dy = -dy * 0.8f;
                count--;
            }
            return true;
        }
        return false;
    }

    public void draw(String text, Graphics2D g) {
        if (font == null) {
            font = Font.getDefaultFont();
        }
        font.drawString(text, x, Math.round(currenty), g);
    }

    public void draw(Image image, Graphics2D g) {
        g.drawImage(image, x, Math.round(currenty), null);

    }

    public void draw(Graphics2D g) {
    }
}
