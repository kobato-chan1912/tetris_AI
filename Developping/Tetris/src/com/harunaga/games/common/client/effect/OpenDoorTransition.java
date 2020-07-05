/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.effect;

import java.awt.Graphics2D;
import java.awt.Image;

/**
 *
 * @author MaiHoang146ThaiHa
 */
public class OpenDoorTransition implements Transition {

    int width;
    int height;
    float v;
    int offSet;
    boolean vertical = false;

    public OpenDoorTransition(int width, int height, float vilocity, boolean vertical) {
        this.width = width;
        this.height = height;
        this.vertical = vertical;
        v = vilocity;
    }

    public boolean isProcessing(long elapsedTime) {
        offSet += v * elapsedTime;
        if (vertical) {
            if (offSet >= width) {
                offSet = width;
                return false;
            }
            return true;
        }
        if (offSet >= height) {
            offSet = height;
            return false;
        }
        return true;
    }

    public void begin(Image image, Graphics2D g) {
        g.drawImage(image, 0, 0, null);
        if (vertical) {
            g.clipRect((width - offSet) >> 1, 0, offSet, height);
        } else {
            g.clipRect(0, (height - offSet) >> 1, width, offSet);
        }
    }

    public void end(Image image, Graphics2D g) {
    }
}
