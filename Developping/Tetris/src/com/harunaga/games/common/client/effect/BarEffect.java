/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.effect;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

/**
 *
 * @author MaiHoang146ThaiHa
 */
public class BarEffect implements Effect {

    Color color = Color.BLACK;
    int numBars;
    int width;
    int height;
    int barSize;
    boolean vertival = true;
    int colorSize;
    float velocity = 0.1f;
    int originalX;
    int originalY;
    boolean revert = false;

    public void setRevert(boolean revert) {
        this.revert = revert;
        if (revert) {
            colorSize = barSize;
        } else {
            colorSize = 0;
        }
    }

    public BarEffect(int numBars, int x, int y, int width, int height) {
        this.numBars = numBars;
        this.width = width;
        this.height = height;
        barSize = width / numBars;
        color = Color.BLACK;
        originalX = x;
        originalY = y;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setVertival(boolean vertival) {
        this.vertival = vertival;
        if (vertival) {
            barSize = width / numBars;
        } else {
            barSize = height / numBars;
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isProcessing(long elapsedTime) {
        if (revert) {
            colorSize -= elapsedTime * velocity;
            if (colorSize <= 0) {
                colorSize = 0;
                return false;
            }
            return true;
        }
        colorSize += elapsedTime * velocity;
        if (colorSize >= barSize) {
            colorSize = barSize;
            return false;
        }
        return true;
    }

    public void draw(Image image, Graphics2D g) {
        if (image == null) {
            draw(g);
            return;
        }
        int x = originalX;
        int y = originalY;
        if (vertival) {
            int oldX = x;
            for (int i = 0; i < numBars; i++) {
                g.clipRect(x, y, colorSize, height);
                g.drawImage(image, oldX, y, width, height, null);
                x += barSize;
                g.setClip(oldX, y, width, height);
            }
        } else {
            int oldY = y;
            for (int i = 0; i < numBars; i++) {
                g.clipRect(x, y, width, colorSize);
                g.drawImage(image, x, oldY, width, height, null);
                y += barSize;
                g.setClip(x, oldY, width, height);
            }
        }
    }

    public void draw(String text, Graphics2D g) {
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        int x = originalX;
        int y = originalY;
        if (vertival) {
            for (int i = 0; i < numBars; i++) {
                g.fillRect(x, y, colorSize, height);
                x += barSize;
            }
        } else {
            for (int i = 0; i < numBars; i++) {
                g.fillRect(x, y, width, colorSize);
                y += barSize;
            }

        }
    }
}
