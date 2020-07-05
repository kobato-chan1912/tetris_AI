/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.server;

import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.common.GameConfig;
import com.harunaga.games.common.GameEvent;
import com.harunaga.games.common.GameEventDefault;
import com.harunaga.games.common.Player;
import com.harunaga.games.common.sql.MySQL;
import com.harunaga.games.common.server.GameServer;
import com.harunaga.games.common.server.GameController;
import com.harunaga.games.tetris.TetrisEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import javax.swing.JOptionPane;

/**
 *
 * @author Harunaga
 */
public class TetrisController extends GameController {

    Connection conn;
    /** list of games */
    protected Hashtable<String, TetrisGame> games;

    /**
     * return the gameName for this controller
     */
    public String getGameName() {
        return TetrisGameConfig.GAME_NAME;
    }

    /**
     * do ChatController specific initialization here
     */
    protected boolean initController(GameConfig gc) {
        log.info("initController: " + getGameName());
        //	clients = new HashMap();
        games = new Hashtable<String, TetrisGame>();
//        conn = MySQL.getConnection("tetris");
//        if (conn == null) {
//            JOptionPane.showMessageDialog(null, "Cannot connect to DBMS, Please check you system for DBMS",
//                    "DBMS error", JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
        return true;
    }

    /**
     * just use the default Player class
     */
    public Player createPlayer() {
//        PlayerDefault p = new PlayerDefault();
//        return p;
        return new TetrisPlayer();
    }

    /**
     * just use the default GameEvent class
     * @return
     */
    public GameEvent createGameEvent() {
        return new TetrisEvent();
    }

    /**
     * process events pulled form the queue
     * @param e
     */
    public void processEvent(GameEvent e) {
        int eventType = e.getType();
        log.info("Process event : " + eventType + "  Player id=" + e.getPlayerId());
        Player player = getPlayerById(e.getPlayerId());
        if (player != null) {
            if (eventType == TetrisEvent.C_LOGIN) {//12                               ----ok-------
                login(e, player);
                log.info("return from login process");
                return;
            } else if (!player.loggedIn()) {
                log.info("Player not login send the event");
                return;
            }
            switch (eventType) {
                case TetrisEvent.C_SAVE_SCORE://5
                    saveScore(e, player);
                    log.info("return from save process");
                    return;
                case TetrisEvent.C_JOIN_GAME:
                    join(e, player);
                    log.info("return from client joingame process");
                    return;
                case TetrisEvent.C_CREATE_GAME:
                    createGame(e, player);
                    log.info("return from client create game process");
                    return;
                case TetrisEvent.C_GET_LIST_CREATED_GAME:
                    responseListOfGames(e, player);
                    log.info("return from client get list game process");
                    return;
                case TetrisEvent.C_DISCONNECT:
                    disconnect(e);
                    log.info("return from client disconnect process");
                    return;
                case TetrisEvent.C_LOGOUT:
                    //process
                    logout(e);
                    log.info("return from logout process");
                    return;
                case TetrisEvent.C_REQUEST_SCORE://15
                    //process
                    log.info("return from request score process");
                    return;
            }
            TetrisGame game = games.get(player.getGameId());
            if (game != null) {
                switch (eventType) {
                    case TetrisEvent.C_REQUEST_PAUSE:
                        game.askPause(e.getMessage(), player);
                        return;
                    case TetrisEvent.C_KICK_PLAYER:
                        //process
                        log.info("return from kick player process");
                        return;
                    case TetrisEvent.C_SEND_SCORE:
                        game.updateScore(player, e.getMessage());
                        log.info("return from send score process");
                        return;
                    case TetrisEvent.C_CHAT_MSG:
                        chat(e, game);
                        log.info("return from chat process");
                        return;
                    case TetrisEvent.C_READY:
                        game.setReady(player, e.getMessage());
                        log.info("return from client ready process");
                        return;
                    case TetrisEvent.C_START_GAME:
                        gameStart(e, player, game);
                        log.info("return from start game process");
                        return;
                    case TetrisEvent.C_GAME_OVER:
                        game.playerDie(player);
                        log.info("return from player die process");
                        return;
                    case TetrisEvent.C_QUIT_GAME:
                        quit(e, player, game);
                        log.info("return from quit game process");
                        return;
                }
            } else {
                log.warn("game null");
            }
        }
        log.warn("event was ignored: " + eventType);
    }

    private void gameStart(GameEvent e, Player player, TetrisGame game) {
        if (game.getOwner() == player) {
            if (!game.start()) {
                sendEvent(new TetrisEvent(TetrisEvent.S_START_GAME_FAIL), player);
            }
        }
    }

    private void responseListOfGames(GameEvent e, Player player) {
        StringBuilder sb = new StringBuilder();
        for (TetrisGame game : games.values()) {
            if (game.isCanJoin()) {
                sb.append(game.toString()).append(TetrisGameConfig.GAME_DELIMITER);
            }
        }
        e.setType(TetrisEvent.S_LIST_CREATED_GAME);
        e.setMessage(sb.toString());
        sendEvent(e, player);
    }

    private int checkLogin(String username, String pass) throws SQLException {
        return 10;
        // do some stuff here to check the validation of player
//        Statement stm = null;
//        String sql = "select password,score from client where username='" + username + "'";
//        stm = conn.createStatement();
//        ResultSet rs = stm.executeQuery(sql);
//        if (rs.next()) {
//            String password = rs.getString(1);
//            int score = rs.getInt(2);
//            if (pass.equals(password)) {
//                stm.close();
//                return score;
//            }
//        }
//        return -1;
    }

    private synchronized boolean doSave(String userName, int score) throws SQLException {
//        Statement stm = null;
//        final String sql = "UPDATE client set score=" + score + " where username='" + userName + "'";
//        stm = conn.createStatement();
//        if (stm.executeUpdate(sql) == 1) {
//            return true;
//        }
//        stm.close();
        return false;
    }

    private void saveScore(GameEvent e, Player player) {
        boolean saveOK = true;
        try {
            int score = Integer.parseInt(e.getMessage());
            String userName = ((TetrisEvent) e).getUsername();
            if (((TetrisPlayer) player).score < score) {
                log.info("query to DBMS");
                saveOK = doSave(userName, score);
            }
            if (saveOK) {
                e.setMessage(getTopScore());
            }
            log.info("PLayer " + userName + " save score :" + score);
        } catch (Exception ex) {
            saveOK = false;
        }
        if (saveOK) {
            e.setType(TetrisEvent.S_SAVE_SCORE_ACK_OK);
        } else {
            e.setType(TetrisEvent.S_SAVE_SCORE_ACK_FAIL);
        }
        sendEvent(e, player);
    }

    private String getTopScore() throws SQLException {
        Statement stm = null;
        String sql = "SELECT username,score  from CLIENT ORDER BY score DESC  LIMIT "
                + TetrisGameConfig.PLAYER_LIMIT;
        stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        StringBuilder sb = new StringBuilder(100);
        while (rs.next()) {
            sb.append(rs.getString(1)).append(TetrisGameConfig.TOPSCORE_ATTRIBUTE_DELIMITER).
                    append(rs.getString(2)).append(TetrisGameConfig.TOPSCORE_DELIMITER);
        }
        //System.out.println(sb.toString()); 
        stm.close();
        return sb.toString();
    }

    /**
     * handle login events
     */
    private void login(GameEvent e, Player player) {
        if (e instanceof TetrisEvent) {
            TetrisEvent event = (TetrisEvent) e;
            String username = event.getUsername();
            String pass = event.getMessage();
            try {
                // check then send ACK to player
                int score = checkLogin(username, pass);
                if (score >= 0) {
                    if (player.loggedIn()) {
                        log.warn("got login event for already logged in player: " + player.getPlayerId());
                    }
                    player.setName(username + " (@" + player.getChannel().socket().getInetAddress().getHostAddress() + ")");
                    player.setLoggedIn(true);
                    ((TetrisPlayer) player).score = score;
                    log.info(player.getName() + " login ok : " + score);
                    e.setType(GameEventDefault.S_LOGIN_ACK_OK);
                } else {
                    log.info(player.getName() + " login fail");
                    e.setType(GameEventDefault.S_LOGIN_ACK_FAIL);
                    e.setMessage("wrong username or password");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                e.setType(GameEventDefault.S_LOGIN_ACK_FAIL);
                e.setMessage("server updating");
            }
            sendEvent(e, player.getPlayerId());
        }
    }

    /**
     * handle logout events
     */
    protected void logout(GameEvent e) {
//        String pid = e.getPlayerId();
//        Player p = (Player) players.get(pid);
//
//        // if in game, kill it first
//        if (p.inGame()) {
//            quit(e);
//        }
//
//        // remove the player
//        players.remove(pid);
//
//        // send them a disconnect
//        GameEventDefault dis = new GameEventDefault(GameEventDefault.S_DISCONNECT, "logged out");
//        sendEvent(dis, p);
//
//        // tell everyone else
//        GameEventDefault sbl = new GameEventDefault(GameEventDefault.SB_LOGOUT, p.getPlayerId());
//        sendBroadcastEvent(sbl, players.values());
//
//        log.info("logout, player: " + pid + ", players online: " + players.size());
    }

    private void createGame(GameEvent e, Player player) {
        TetrisGame game = new TetrisGame(player, this);
        String message = e.getMessage();
        if (message != null) {
            game.setGameName(player.getName() + " ( " + message + " )");
        }
        e.setType(TetrisEvent.S_CREATE_GAME_ACK);
        sendEvent(e, player.getPlayerId());
        games.put("" + game.getGameId(), game);
        log.info("Player :" + player.getName() + " created a new game : " + game.getGameId());
    }

    /**
     * explain some thing
     * @param e
     * @param player
     */
    private void join(GameEvent e, Player player) {
        TetrisGame game = games.get(e.getMessage());
        if (game == null || (!game.addPlayer(player))) {
            e.setType(TetrisEvent.S_JOIN_GAME_ACK_FAIL);
            sendEvent(e, player.getPlayerId());
        }
    }

    /**
     * handle quit events
     */
    private void quit(GameEvent e, Player player, TetrisGame game) {
        log.info("client quit game");
        if (player == game.getOwner() && game.askDestroy()); else {
            game.removePlayer(player);
        }
    }

    /**
     * remove the created game
     * @param gameID id of the game
     */
    public void removeGame(String gameID) {
        games.remove(gameID);
    }

    /**
     * handle chat events
     */
    private void chat(GameEvent e, TetrisGame game) {
        e.setType(GameEventDefault.SB_CHAT_MSG);
        sendBroadcastEvent(e, game.getAlivePlayers());
        log.info("chat, player " + e.getPlayerId() + " says " + e.getMessage());
    }

    private void disconnect(GameEvent e) {
        Player player = getPlayerById(e.getPlayerId());
        if (player.inGame()) {
            TetrisGame game = games.get("" + player.getGameId());
            if (game != null) {
                quit(e, player, game);
//                e.setType(GameEventDefault.SB_CLIENT_ERROR);
//                sendBroadcastEvent(e, game.getAlivePlayers());
            }
        }
        removePlayerById(e.getPlayerId());
        log.info("disconnect from player " + e.getPlayerId());
    }

    /**
     * 
     * @param fromPlayer
     * @param type
     * @param reason
     * @return
     */
    ;

    public GameEvent createDisconnectEvent(Player fromPlayer, int type, String reason) {
        if (type == GameServer.CLIENT_END_OF_STREAM) {
            return new GameEventDefault(GameEventDefault.C_DISCONNECT, "bye bye");
        }
        return new GameEventDefault(GameEventDefault.C_DISCONNECT, "Thang " + fromPlayer.getPlayerId()
                + " bi dismang rui");
    }

    @Override
    public void shutdown() {
        try {
            super.shutdown();
            log.info("Close connection to DBMS");
            conn.close();
        } catch (Exception ex) {
            log.error("Error closing connection to DBMS", ex);
        }
    }
}
