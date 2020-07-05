/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.states;

import com.harunaga.games.common.client.miscellaneous.SizeFixedButton;
import com.harunaga.games.tetris.client.form.ComponentProvider;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.tetris.TetrisGamePlayer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 *
 * @author Harunaga
 */
public class ResultState extends TetrisGameState implements ActionListener {

    SizeFixedButton back;
    SizeFixedButton save;
    BufferedImage resultImg;
    private Color[] pColors = TetrisGameConfig.PLAYER_COLORS;

    @Override
    void createGUI(ResourceManager resourceManager) {
        if (back == null) {
            ComponentProvider btProvider = new ComponentProvider(resource);
            back = btProvider.createOvalButton("back", TetrisGameConfig.SMALL_ON_FOCUS_IMG,
                    "return to menu", this, Cursor.HAND_CURSOR);
        }
        resultImg = resourceManager.getCompatibleImage(frame.getWidth(), frame.getHeight(),
                Transparency.OPAQUE);
        Graphics2D g = resultImg.createGraphics();
        g.drawImage(bg, 0, 0, frame);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Image aliveImg = resource.getImage("heart.png");
        Image deadImg = resource.getImage("skull-crossbones.png");
        int imgW = aliveImg.getWidth(null) + 10;
        int imgH = aliveImg.getHeight(null) + 10;
        final int frameW = frame.getWidth();
        int oldX = frameW >> 2;
        int x = oldX;
        int y = frame.getHeight() >> 2;
        for (TetrisGamePlayer player : net.getAllPlayers()) {
            if (player.alive) {
                g.drawImage(aliveImg, x, y, null);
            } else {
                g.drawImage(deadImg, x, y, null);
            }
            x += imgW;
            g.setColor(pColors[player.color]);
            defaultFont.drawString(player.name + "", x, y, g,
                    0, imgH, SwingConstants.LEFT, SwingConstants.CENTER);
            defaultFont.drawString(player.score + "", frameW - ((frameW) >> 2), y, g,
                    0, imgH, SwingConstants.RIGHT, SwingConstants.CENTER);
            x = oldX;
            y += defaultFont.getHeight();
        }
        TetrisGamePlayer player = net.getPlayer(net.getWinnerId());
        g.setColor(pColors[player.color]);
        defaultFont.drawString("Winner:   " + player.name + "", oldX, y + 30, g);
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        frame.getContentPane().add(back);
        //   back.setLocation(50, 50);
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(resultImg, 0, 0, null);
        frame.getLayeredPane().paintComponents(g);
    }

    public String getName() {
        return STATE_RESULT;
    }

    public void update(long elapsedTime) {
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == back) {
            nextState = STATE_GAME_MENU;
        }
    }

    @Override
    public void stop() {
        net.removeAllPlayers();
        super.stop();
    }
//    public void actionPerformed(Object provider) {
//    }
}
