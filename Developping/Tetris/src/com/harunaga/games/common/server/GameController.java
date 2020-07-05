package com.harunaga.games.common.server;

import com.harunaga.games.common.*;
import com.harunaga.games.common.server.GameServer;
import java.util.*;

/**
 * GameController.java
 *
 * Base class for all server-side logic implementations.
 * Extends from Wrap to provide a backing thread pool 
 * and incoming EventQueue
 *
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public abstract class GameController extends Wrap {

    /** players keyed by playerId */
    protected Hashtable<String, Player> playersByPlayerId;
    /** reference to the GameServer */
    private GameServer gameServer;

    /**
     * fetches the Player for a given playerId
     */
    public Player getPlayerById(String id) {
        return playersByPlayerId.get(id);
    }

    /**
     * GameServer will call this init method immediately after construction.
     * It is final so that this initialization does not got overridden by subclasses.
     * Initialization for subclasses is done in the initController() method below.
     */
    public final boolean init(GameServer s, GameConfig gc) {
        this.gameServer = s;
        playersByPlayerId = new Hashtable<String, Player>(30);
        // todo: get the preferred number of workers from the GameConfig
        //	int nw = gc.getInt("NUM_WORKERS", 5);
        if (initController(gc)) {
            initWrap(Globals.DEFAULT_CONTROLLER_WORKERS);
            return true;
        } else {
            return false;
        }
//        // init the Wrap first
//        initWrap(Globals.DEFAULT_CONTROLLER_WORKERS);
//        // now call the subclasses' init
//        initController(gc);
    }

    /**
     * send event to player. if possible try to use the sendEvent(GameEvent e, String recipientId) method
     * because it give a better performance in runtime more or less
     * utility method for sending events
     * @see #sendEvent(GameEvent,String) 
     * @see #sendBroadcastEvent(GameEvent, Collection Players) 
     * @see #sendBroadcastEvent(GameEvent, String[]) 
     * @param e event to send
     * @param p the recipient 
     */
    public void sendEvent(GameEvent e, Player p) {
        if (p != null) {
            sendEvent(e, p.getPlayerId());
        }
    }

    /**
     * send the given event to the player
     * @see #sendBroadcastEvent(GameEvent, String[] recipientId) 
     * @param e event to send
     * @param recipientId Id of recipient
     */
    public void sendEvent(GameEvent e, String recipientId) {
        if (recipientId != null) {
            e.setGameName(getGameName());
            e.setRecipients(new String[]{recipientId});
            gameServer.sendEvent(e);
        }
    }

    /**
     * utility method for sending events to multiple players
     * it will check to avoid send this event to the owner (the sender) in set of given players
     * @param e
     * @param players  
     * @see #sendBroadcastEvent(GameEvent,String[]) 
     */
    public void sendBroadcastEvent(GameEvent e, Collection<Player> players) {
        if (players == null) {
            return;
        }
        e.setGameName(getGameName());
        Iterator<Player> i = players.iterator();
        String[] recipients = new String[players.size()];
        int j = 0;
        String senderId = e.getPlayerId();
        while (i.hasNext()) {
            Player p = i.next();
            String recipientId = p.getPlayerId();
            if (!(recipientId.equals(senderId))) {
                recipients[j++] = recipientId;
            }
        }
        e.setRecipients(recipients);
        gameServer.sendEvent(e);
    }

    /**
     * utility method for sending events to multiple players
     * @param e
     * @param recipientIds
     * @see #sendEvent(GameEvent,String) 
     */
    public void sendBroadcastEvent(GameEvent e, String[] recipientIds) {
        if (recipientIds != null) {
            e.setRecipients(recipientIds);
            gameServer.sendEvent(e);
        }
    }

    /**
     * GameController subclasses should implement initController
     * in order to do any initialization they require.
     */
    protected abstract boolean initController(GameConfig gc);

    public Player createNextPlayer() {
        Player player = createPlayer();
        player.setGameNameHash(getGameName());
        player.setPlayerId(gameServer.nextSessionId());
        playersByPlayerId.put(player.getPlayerId(), player);
        return player;
    }

    public boolean removePlayer(Player player) {
        if (playersByPlayerId.remove(player.getPlayerId()) != null) {
            return true;
        }
        return false;
    }

    public boolean removePlayerById(String playerId) {
        if (playersByPlayerId.remove(playerId) != null) {
            return true;
        }
        return false;
    }

    /**
     * subclasses must implement to provide their GameName
     */
    public abstract String getGameName();

    /**
     * factory method for fetching Player objects
     */
    protected abstract Player createPlayer();

    /**
     * factory method for fetching GameEvent objects
     */
    public abstract GameEvent createGameEvent();

    public abstract GameEvent createDisconnectEvent(Player player, int type, String reason);
}
