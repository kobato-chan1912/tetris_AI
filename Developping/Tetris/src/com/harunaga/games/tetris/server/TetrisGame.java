/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.server;

import com.harunaga.games.common.GameEvent;
import com.harunaga.games.common.Player;
import com.harunaga.games.tetris.TetrisEvent;
import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.tetris.TetrisGamePlayer;
import java.util.Collection;
import java.util.Hashtable;
import org.apache.log4j.Logger;

/**
 *
 * @author Harunaga
 */
public final class TetrisGame {

    private Logger log;
    public static final long GAME_TIME = 240000; //~1 minute
    public static final int STATE_WAITING = 0;
    public static final int STATE_STARTING = 1;
    public static final int STATE_STARTED = 2;
    public static final int STATE_TIME_UP = 4;
    public static final int STATE_DESTROYED = 5;
    private boolean paused;
    public static final int PLAYER_LIMIT = TetrisGameConfig.PLAYER_LIMIT;
    private static int nextId = 0;
    private String gameId;
    Player owner;
    private int state;
    private String gameName;
    private TetrisController gc;
    private boolean[] pColors;
    private int totalPlayer = 1;

    private synchronized static String getNextId() {
        if (++nextId == 0) {
            nextId++;
        }
        return "" + nextId;
    }
    Hashtable<String, Player> alivePlayers;
    Hashtable<String, TetrisGamePlayer> totalPlayers;

    public TetrisGame(Player owner, TetrisController gc) {
        gameId = getNextId();
        log = Logger.getLogger(getClass().getName() + " : " + gameName);
        alivePlayers = new Hashtable<String, Player>(PLAYER_LIMIT);
        totalPlayers = new Hashtable<String, TetrisGamePlayer>(PLAYER_LIMIT);
        this.owner = owner;
        state = STATE_WAITING;
        this.gc = gc;
        if (gc == null) {
            throw new IllegalArgumentException("null GameController was passed");
        }
        gameName = owner.getName();
        pColors = new boolean[PLAYER_LIMIT];
        setOwner(owner);
    }

    private void setOwner(Player player) {
        String pid = player.getPlayerId();
        player.setInGame(true);
        player.setGameId(gameId);
        pColors[0] = true;
        alivePlayers.put(pid, player);
        totalPlayers.put(pid, new TetrisGamePlayer(player.getName(), pid));
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public boolean isCanJoin() {
        return state == STATE_WAITING && totalPlayers.size() < PLAYER_LIMIT;
    }

    private void setState(int state) {
        this.state = state;
    }

    public synchronized boolean start() {
        if (checkReady()) {
            setState(STATE_STARTING);
            new Clock().start();
            return true;
        }
        return false;
    }

    private boolean checkReady() {
        if (totalPlayers.size() < 2) {
            return false;
        }
        for (TetrisGamePlayer player : totalPlayers.values()) {
            if (!player.ready) {
                return false;
            }
        }
        return true;
    }

    public String getGameId() {
        return gameId;
    }

    public Player getOwner() {
        return owner;
    }

    public Collection<Player> getAlivePlayers() {
        return alivePlayers.values();
    }

    public void updateScore(Player player, String scoreString) {
        int score = 0;
        try {
            score = Integer.parseInt(scoreString);
        } catch (Exception exc) {
            return;
        }
        log.info("Update score for " + player.getName());
        if (state != STATE_TIME_UP && score > 0) {
            TetrisGamePlayer p = totalPlayers.get(player.getPlayerId());
            if (p != null && p.score < score) {
                int amount = score - p.score;
                p.score = score;
                TetrisEvent scoreEvent = new TetrisEvent(TetrisEvent.SB_CLIENT_SCORE, scoreString);
                scoreEvent.setUsername(player.getPlayerId());
                gc.sendBroadcastEvent(scoreEvent, getAlivePlayers());
                if (amount >= TetrisGameConfig.SPROUT_UP_AMOUNT) {
                    String rows = "1";
                    TetrisEvent sproutUpEvent = new TetrisEvent(TetrisEvent.SB_SPROUT_UP, rows);
                    sproutUpEvent.setPlayerId(player.getPlayerId());
                    gc.sendBroadcastEvent(sproutUpEvent, getAlivePlayers());
                }
            }
        }
    }

    public boolean contains(Player player) {
        return totalPlayers.containsKey(player.getPlayerId());
    }

    public synchronized void askPause(String pauseStr, Player player) {
        if (state == STATE_STARTED) {
            paused = Boolean.parseBoolean(pauseStr);
            TetrisEvent pauseEvent = new TetrisEvent(TetrisEvent.SB_PAUSED, pauseStr);
            pauseEvent.setUsername(player.getPlayerId());
            gc.sendBroadcastEvent(pauseEvent, getAlivePlayers());
        }
    }

    /**
     * we use synchronized so we hope not to get in to problem that the player break the limit
     * @param player the player wanna be added
     * @return
     */
    public synchronized boolean addPlayer(Player player) {
        if (isCanJoin()) {
            String pid = player.getPlayerId();
            if (alivePlayers.put(pid, player) == null) {
                player.setInGame(true);
                player.setGameId(gameId);
                TetrisGamePlayer tp = new TetrisGamePlayer(player.getName(), pid);
                totalPlayers.put(pid, tp);
                log.info("player " + player.getName() + " joined the game");
                //find color for this player. by default is 0
                for (int i = 0; i < PLAYER_LIMIT; i++) {
                    if (pColors[i] == false) {
                        tp.color = i;
                        pColors[i] = true;
                        break;
                    }
                }
                GameEvent be = new TetrisEvent(TetrisEvent.SB_PLAYER_JOINED, tp.toString());
                be.setPlayerId(pid);
                gc.sendBroadcastEvent(be, getAlivePlayers());
            }
            GameEvent e = new TetrisEvent(TetrisEvent.S_JOIN_GAME_ACK_OK, getCurrentPlayers());
            e.setPlayerId(pid);
            gc.sendEvent(e, pid);
            //  totalPlayer++;
            return true;
        }
        return false;
    }

    public synchronized void removePlayer(Player player) {
        if (player == null) {
            return;
        }
        TetrisGamePlayer tp;
        alivePlayers.remove(player.getPlayerId());
        log.info("player " + player.getName() + " left the game");
        tp = totalPlayers.remove(player.getPlayerId());
        if (tp.alive) {
            totalPlayer--;
        }
        pColors[tp.color] = false;
        gc.sendBroadcastEvent(new TetrisEvent(TetrisEvent.SB_PLAYER_QUIT, tp.id),
                getAlivePlayers());
        player.setInGame(false);
        player.setGameId("0");
        // totalPlayer--;
    }

    public void playerDie(Player player) {
        TetrisGamePlayer tp = totalPlayers.get(player.getPlayerId());
        tp.alive = false;
        gc.sendBroadcastEvent(new TetrisEvent(TetrisEvent.SB_PLAYER_QUIT, tp.id),
                getAlivePlayers());
        totalPlayer--;
    }

    public synchronized void setReady(Player player, String ready) {
        TetrisGamePlayer tp = totalPlayers.get(player.getPlayerId());
        tp.ready = Boolean.parseBoolean(ready);
        log.info("player " + player.getName() + " - is ready : " + tp.ready);
        TetrisEvent readyEvent = new TetrisEvent(TetrisEvent.SB_PLAYER_READY, ready);
        readyEvent.setPlayerId(player.getPlayerId());
        gc.sendBroadcastEvent(readyEvent,
                getAlivePlayers());
    }

    public synchronized boolean askDestroy() {
        return askDestroy(false);
    }

    boolean askDestroy(boolean isFinish) {
        if (state == STATE_WAITING || isFinish) {
            state = STATE_DESTROYED;
            if (!isFinish) {
                GameEvent destroyEvent = new TetrisEvent(TetrisEvent.SB_GAME_DESTROYED);
                destroyEvent.setPlayerId(owner.getPlayerId());//we don't send this event to the owner
                gc.sendBroadcastEvent(destroyEvent, alivePlayers.values());
            }
            gc.removeGame("" + gameId);
            for (Player p : alivePlayers.values()) {
                p.setInGame(false);
            }
            log.info("--------------->>>>game : " + gameId + " destroyed....");
            return true;
        }
        return false;
    }

    private String getCurrentPlayers() {
        StringBuilder sb = new StringBuilder();
        for (TetrisGamePlayer p : totalPlayers.values()) {
            sb.append(p.toString()).append(TetrisGameConfig.PLAYER_DELIMITER);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return new StringBuffer().append(gameName).append(TetrisGameConfig.GAME_ATTRIBUTE_DELIMITER).
                append(gameId).toString();
    }

    class Clock extends Thread {

        Clock() {
            super("Clock : " + gameId);
            setDaemon(true);
        }

        @Override
        public void run() {
            paused = false;
            totalPlayer = alivePlayers.size();
            gc.sendBroadcastEvent(new TetrisEvent(TetrisEvent.SB_GAME_READY), getAlivePlayers());
            threadSleep(3000);
            gc.sendBroadcastEvent(new TetrisEvent(TetrisEvent.SB_GAME_START), getAlivePlayers());
            setState(STATE_STARTED);
            int time = 1000;
            threadSleep(1000);
            while (time < GAME_TIME) {
                if (totalPlayer <= 1) {
                    log.info("---------------------->>>all player left....at game: " + gameId);
                    break;
                }
                if (!paused) {
                    System.out.println("Send time event to clients++++++++++++++++++++++++++++++++++");
                    gc.sendBroadcastEvent(new TetrisEvent(TetrisEvent.SB_TIME, "" + ((GAME_TIME - time) / 1000)), getAlivePlayers());
                    time += 1000;
                }
                threadSleep(999);
            }
            if (alivePlayers.isEmpty()) {
                gc.removeGame(gameId);
                return;
            }
            gc.sendBroadcastEvent(new TetrisEvent(TetrisEvent.SB_TIME_UP), getAlivePlayers());
            setState(STATE_TIME_UP);
            //wait for all update score finish
            threadSleep(3000);
            gc.sendBroadcastEvent(calculateResult(), getAlivePlayers());
            askDestroy(true);
        }

        void threadSleep(long time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {
            }
        }

        GameEvent calculateResult() {
            StringBuilder sb = new StringBuilder();
            TetrisEvent event = new TetrisEvent(TetrisEvent.SB_RESULT);
            TetrisGamePlayer winner = null;
            for (TetrisGamePlayer player : totalPlayers.values()) {
                if (player.alive && (winner == null || winner.score < player.score)) {
                    winner = player;
                }
                sb.append(player.toString()).append(TetrisGameConfig.PLAYER_DELIMITER);
            }
            event.setMessage(sb.toString());
            if (winner != null) {
                event.setUsername(winner.id);
            }
            return event;
        }
    }
}
