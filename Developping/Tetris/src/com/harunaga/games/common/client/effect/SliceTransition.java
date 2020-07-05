/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.effect;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;

/**
 *
 * @author MaiHoang146ThaiHa
 */
public class SliceTransition implements Transition {

    public static final int LEFT_2_RIGHT = 0;
    public static final int RIGHT_2_LEFT = 1;
    public static final int TOP_2_BOTTOM = 1 << 1;
    public static final int BOTTOM_2_LEFT = 0 << 1;
    int w;
    int h;
    float v;
    int offSetX;
    int offSetY;
    boolean revert = false;
    int direction;
    boolean horizontal = true;
    boolean vertical = false;

    public void setSliceVertival(boolean sliveVertival) {
        this.vertical = sliveVertival;
    }

    public void setSliceHorizontal(boolean sliceHorizontal) {
        this.horizontal = sliceHorizontal;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setRevert(boolean revert) {
        this.revert = revert;
    }

    public SliceTransition(int width, int height, float vilocity) {
        offSetX = w = width;
        offSetY = h = height;
        v = vilocity;
    }

    public boolean isProcessing(long elapsedTime) {
        float s = v * elapsedTime;
        offSetX -= s;
        if (offSetX <= 0) {
            offSetX = 0;
            return false;
        }
        return true;
    }

    public void begin(Image image, Graphics2D g) {
        g.drawImage(image, 0, 0, null);
        g.translate(offSetX, 0);
    }

    public void end(Image image, Graphics2D g) {
    }
}
