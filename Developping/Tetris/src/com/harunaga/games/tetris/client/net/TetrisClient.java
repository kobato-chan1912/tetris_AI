/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.net;

import com.harunaga.games.common.client.net.NetManager;
import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.common.GameEvent;
import com.harunaga.games.common.GameEventDefault;
import com.harunaga.games.tetris.TetrisEvent;
import com.harunaga.games.tetris.TetrisGamePlayer;
import java.util.Collection;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 *
 * @author Harunaga
 */
public class TetrisClient extends NetManager {

    String password;
    String username;
    Hashtable<String, TetrisGamePlayer> players =
            new Hashtable<String, TetrisGamePlayer>(TetrisGameConfig.PLAYER_LIMIT);
    boolean gameOwner;
    String winnerId;

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public TetrisClient() {
        setLogin(false);
    }

    public void setGameOwner(boolean aFlag) {
        gameOwner = aFlag;
    }

    public boolean isGameOwner() {
        return gameOwner;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String getGameName() {
        return TetrisGameConfig.GAME_NAME;
    }

    @Override
    public GameEvent createGameEvent() {
        return new TetrisEvent();
    }

    @Override
    public GameEvent createLoginEvent() {
        TetrisEvent ge = new TetrisEvent(GameEventDefault.C_LOGIN);
        ge.setUsername(username);
        ge.setMessage(password);
        return ge;
    }

    @Override
    public GameEvent createDisconnectEvent(String reason) {
        return new TetrisEvent(GameEventDefault.S_DISCONNECT, reason);
    }

    public Collection<TetrisGamePlayer> getAllPlayers() {
        return players.values();
    }

    /**
     * get player with specified id
     * @param id
     * @return
     */
    public TetrisGamePlayer getPlayer(String id) {
        return players.get(id);
    }

    /**
     * add many player
     * @param players
     */
    public void addPlayers(String players) {
        StringTokenizer st = new StringTokenizer(players, "" + TetrisGameConfig.PLAYER_DELIMITER);
        while (st.hasMoreTokens()) {
            addPlayer(st.nextToken());
        }
    }

    /**
     * add one player
     * @param player
     */
    public void addPlayer(String player) {
        addPlayer(TetrisGamePlayer.getPlayer(player));
    }

    /**
     * add one player
     * @param player
     */
    public void addPlayer(TetrisGamePlayer player) {
        players.put(player.id, player);
    }

    /**
     * remove player with specified id
     * @param id
     */
    public void removePlayer(String id) {
        players.remove(id);
    }

    public void removeAllPlayers() {
        players.clear();
    }

    public boolean isMe(TetrisGamePlayer player) {
        return getMe() == player;
    }

    public boolean isMe(String id) {
        if (playerId == null) {
            return false;
        } else {
            return playerId.equals(id);
        }
    }

    public TetrisGamePlayer getMe() {
        return getPlayer(playerId);
    }
}
