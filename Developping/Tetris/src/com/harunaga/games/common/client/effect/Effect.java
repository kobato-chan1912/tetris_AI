/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.effect;

import java.awt.Graphics2D;
import java.awt.Image;

/**
 *
 * @author Harunaga
 */
public interface Effect {

    public boolean isProcessing(long elapsedTime);

    public void draw(String text, Graphics2D g);

    public void draw(Image image, Graphics2D g);

    public void draw(Graphics2D g);
}
