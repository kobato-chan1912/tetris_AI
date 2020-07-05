/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.effect;

import java.awt.Graphics2D;

/**
 *
 * @author MaiHoang146ThaiHa
 */
public interface EffectProvider {

    public static final int TYPE_FALLING = 0;
    public static final int TYPE_ROTATING = 1;

    public void init();

    public void process(Graphics2D g);
}
