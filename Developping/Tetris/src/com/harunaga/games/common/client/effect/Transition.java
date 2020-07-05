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
public interface Transition {

    public boolean isProcessing(long elapsedTime);

    public void begin(Image image, Graphics2D g);

    public void end(Image image, Graphics2D g);
}
