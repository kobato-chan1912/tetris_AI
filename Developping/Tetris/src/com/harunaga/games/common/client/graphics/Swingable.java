/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.graphics;

import java.awt.Graphics2D;

/**
 *
 * @author Harunaga
 */
public class Swingable extends Sprite {

    private float faceAngle;
    private float velocityAngle;

    public Swingable(Animation anim) {
        super(anim);
        faceAngle = 0;
        velocityAngle = 0;
    }

    public float getFaceAngle() {
        return faceAngle;
    }

    public void setFaceAngle(float faceAngle) {
        this.faceAngle = faceAngle;
    }

    public float getVelocityAngle() {
        return velocityAngle;
    }

    public void setVelocityAngle(float angle) {
        this.velocityAngle = angle;
    }

    @Override
    public void update(long elapsedTime) {
        super.update(elapsedTime);
        faceAngle += elapsedTime * velocityAngle;
    }

    public void draw(Graphics2D g, int x, int y) {
        g.rotate(Math.toRadians(getFaceAngle()));
        g.drawImage(getImage(), x, y, null);
    }
}
