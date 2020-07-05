package com.harunaga.games.common;

import java.nio.channels.SocketChannel;

/**
 * Player.java
 *
 * Interface for Players, all player classes must implement this interface
 * 
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public interface Player {

    public String getName();

    public void setName(String name);

    public void setGameNameHash(String gameName);

    public int getGameNameHash();

    public String getPlayerId();

    public void setPlayerId(String id);

    public String getSessionId();

    public void setSessionId(String id);

    public SocketChannel getChannel();

    public void setChannel(SocketChannel channel);

    public boolean loggedIn();

    public void setLoggedIn(boolean in);

    public boolean inGame();

    public void setInGame(boolean in);

    public String getGameId();

    public void setGameId(String gid);
}
