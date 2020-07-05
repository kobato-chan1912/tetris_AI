/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.states;

import com.harunaga.games.common.GameEvent;
import com.harunaga.games.common.client.font.Font;
import com.harunaga.games.common.client.input.InputManager;
import com.harunaga.games.common.client.effect.SliceTransition;
import com.harunaga.games.common.client.effect.Transition;
import com.harunaga.games.common.client.net.GameEventListener;
import com.harunaga.games.common.client.net.NetManager;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.tetris.TetrisEvent;
import com.harunaga.games.tetris.TetrisGameConfig;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

/**
 *
 * @author Harunaga
 */
public class SplashGameState extends TetrisGameState implements GameEventListener {

    String countDown;
    long totalElapsedTime;
    Font font;
    boolean done;

    public void setFont(String fontName) {
        font = Font.getFont(fontName);
    }

    public SplashGameState() {
        font = Font.getFont("System", Font.STYLE_BOLD, 48);
    }

    public String getName() {
        return STATE_SPLASH;
    }

    @Override
    public void start(InputManager inputManager, NetManager netManager) {
        super.start(inputManager, netManager);
        countDown = "three";
        totalElapsedTime = 0;
        done = false;
    }

    @Override
    public void stop() {
    }

    public void update(long elapsedTime) {
        if (net.isInMultiplayerGame()) {
            return;
        }
        totalElapsedTime += elapsedTime;
        if (totalElapsedTime > 3000) {
            countDown = "go";
            nextState = STATE_PLAYING;
        } else if (totalElapsedTime > 2000) {
            countDown = "one";
        } else if (totalElapsedTime > 1000) {
            countDown = "two";
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (bg != null) {
            g.drawImage(bg, 0, 0, frame.getWidth(), frame.getHeight(), frame);
        }
        String s = "Waiting server .... are you ready ...?";
        if (!net.isInMultiplayerGame()) {
            s = "are you ready ....?";
        }
        g.setColor(Color.RED);
        int x = frame.getWidth() - font.stringWidth(s);
        int y = frame.getHeight() >> 1;
        font.drawString(s, x >> 1, y, g);
        font.drawString(countDown, x, y + font.getHeight(), g);
    }

    @Override
    public void processEvent(GameEvent e) {
        super.processEvent(e);
        int eventType = e.getType();
        if (eventType == TetrisEvent.SB_GAME_START) {
            nextState = STATE_PLAYING;
        }
    }

    @Override
    void createGUI(ResourceManager resourceManager) {
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void actionPerformed(Object provider) {
    }

    @Override
    public Transition getEffect() {
        return new SliceTransition(frame.getWidth(), frame.getHeight(), 2f);
    }
}
