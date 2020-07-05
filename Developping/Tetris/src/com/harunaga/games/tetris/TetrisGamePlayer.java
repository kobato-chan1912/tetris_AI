/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris;

import java.util.StringTokenizer;

/**
 *
 * @author Harunaga
 */
public class TetrisGamePlayer {

    public TetrisGamePlayer(String name, String id) {
        alive = true;
        score = 0;
        ready = false;
        color = 0;
        this.name = name;
        this.id = id;
    }
    public String id;
    public boolean alive;
    public int score;
    public String name;
    public boolean ready;
    public int color;

    public static TetrisGamePlayer getPlayer(String data) {
        StringTokenizer st = new StringTokenizer(data, "" + TetrisGameConfig.PLAYER_ATTRIBUTE_DELIMITER);
        if (st.countTokens() >= 6) {
            try {
                String pid = st.nextToken();
                String pname = st.nextToken();
                TetrisGamePlayer player = new TetrisGamePlayer(pname, pid);
                player.color = Integer.parseInt(st.nextToken());
                player.alive = Boolean.parseBoolean(st.nextToken());
                player.score = Integer.parseInt(st.nextToken());
                player.ready = Boolean.parseBoolean(st.nextToken());
                return player;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(TetrisGameConfig.PLAYER_ATTRIBUTE_DELIMITER);
        sb.append(name).append(TetrisGameConfig.PLAYER_ATTRIBUTE_DELIMITER);
        sb.append(color).append(TetrisGameConfig.PLAYER_ATTRIBUTE_DELIMITER);
        sb.append(alive).append(TetrisGameConfig.PLAYER_ATTRIBUTE_DELIMITER);
        sb.append(score).append(TetrisGameConfig.PLAYER_ATTRIBUTE_DELIMITER);
        sb.append(ready).append(TetrisGameConfig.PLAYER_ATTRIBUTE_DELIMITER);
        return sb.toString();
    }
}
