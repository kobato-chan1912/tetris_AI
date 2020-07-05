package com.harunaga.games.common.server;

import com.harunaga.games.common.*;
import com.harunaga.games.common.sql.MySQL;
import com.harunaga.games.tetris.server.TetrisController;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.nio.channels.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import org.apache.log4j.*;

/**
 * GameServer.java
 *
 * The heart of the framework, GameServer accepts
 * incoming client connections and hands them off to 
 * the SelectAndRead class.
 * GameServer also keeps track of the connected players
 * and the GameControllers.
 *
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public final class GameServer extends Thread {

    public static final int CLIENT_END_OF_STREAM = 200;
    public static final int CLIENT_IOEXCEPTION = 400;
    /** log4j Logger */
    private Logger log = Logger.getLogger("GameServer");
    /** ServerSocketChannel for accepting client connections */
    private ServerSocketChannel sSockChan;
    /** selector for multiplexing ServerSocketChannels */
    private Selector selector;
    /** GameControllers keyed by GameName */
    private Hashtable<String, GameController> gameControllers;
//    /** classname prefix used for dynamically loading GameControllers */
//    private static final String CONTROLLER_CLASS_PREFIX =
//            "com.harunaga.games.common.server.controller.";
//    private static final String BASE_CLASS =
//            "com/harunaga/games/common/server/controller/GameController.class";
//    /** players keyed by playerId */
//    private static Hashtable playersByPlayerId;
//    /** players keyed by sessionId */
//    private static Hashtable playersBySessionId;
    private boolean running;
    private SelectAndRead selectAndRead;
    private EventWriter eventWriter;
    private static long nextSessionId = 0;

    /**
     * main.
     * setup log4j and fireup the GameServer
     */
    public static void main(String args[]) {
        BasicConfigurator.configure();
        final GameServer gs = new GameServer();
        JFrame frame = new JFrame("Harunaga-Server");
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        final JTextField user = new JTextField(MySQL.USER);
        final JTextField password = new JTextField(MySQL.PASSWORD);
        pane.add(user);
        pane.add(password);
        pane.add(new JButton("Start") {

            @Override
            protected void fireActionPerformed(ActionEvent event) {
                super.fireActionPerformed(event);
                MySQL.USER = user.getText();
                MySQL.PASSWORD = password.getText();
                gs.start();
                this.setEnabled(false);
            }
        });
        pane.add(new JButton("Exit") {

            @Override
            protected void fireActionPerformed(ActionEvent event) {
                super.fireActionPerformed(event);
                if (gs.isAlive()) {
                    gs.shutdown();
                }
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * constructor, just initialize our hashtables
     */
    public GameServer() {
        gameControllers = new Hashtable<String, GameController>();
//        playersByPlayerId = new Hashtable();
//        playersBySessionId = new Hashtable();
    }

    /**
     * init the GameServer, startup our workers, etc.
     */
    public void init() {
        log.info("GameServer initializing");
        initAllGameController();
        initServerSocket();
        selectAndRead = new SelectAndRead(this);
        selectAndRead.start();
        eventWriter = new EventWriter(this, Globals.EVENT_WRITER_WORKERS);
    }

    private void initAllGameController() {
        log.info("loading all GameControllers");
        initGameController(new TetrisController());
    }

    /**
     * GameServer specific initialization, bind to the server port,
     * setup the Selector, etc.
     */
    private void initServerSocket() {
        try {
            // open a non-blocking server socket channel
            sSockChan = ServerSocketChannel.open();
            sSockChan.configureBlocking(false);

            // bind to localhost on designated port
            InetAddress addr = InetAddress.getLocalHost();
            log.info("binding to address: " + addr.getHostAddress());
            sSockChan.socket().bind(new InetSocketAddress(addr, Globals.PORT));

            // get a selector
            selector = Selector.open();

            // register the channel with the selector to handle accepts
            SelectionKey acceptKey = sSockChan.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            log.error("error initializing ServerSocket", e);
            System.exit(1);
        }
    }

    /**
     * Here's the meat, loop over the select() call to
     * accept socket connections and hand them off to SelectAndRead
     */
    @Override
    public void run() {
        init();
        log.info("******** GameServer running ********");
        running = true;
        int numReady = 0;

        while (running) {
            // note, since we only have one ServerSocket to listen to,
            // we don't need a Selector here, but we set it up for
            // later additions such as listening on another port
            // for administrative uses.
            try {
                // blocking select, will return when we get a new connection
                selector.select();

                // fetch the keys
                Set readyKeys = selector.selectedKeys();

                // run through the keys and process
                Iterator i = readyKeys.iterator();
                while (i.hasNext()) {
                    SelectionKey key = (SelectionKey) i.next();
                    i.remove();

                    ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = ssChannel.accept();

                    // add to the list in SelectAndRead for processing
                    selectAndRead.addNewClient(clientChannel);
                    log.info("got connection from: " + clientChannel.socket().getInetAddress());
                }
            } catch (IOException ioe) {
                log.warn("error during serverSocket select(): " + ioe.getMessage());
            } catch (Exception e) {
                log.error("exception in run()", e);
            }
        }
    }

    /**
     * shutdown the GameServer
     */
    public void shutdown() {
        running = false;
        selector.wakeup();
        eventWriter.shutdown();
        for (GameController gameController : gameControllers.values()) {
            gameController.shutdown();
        }
    }

    /**
     * Return the next available sessionId
     */
    public static synchronized String nextSessionId() {
        return "" + nextSessionId++;
    }

    /**
     * finds the GameController for a given GameName
     */
    public GameController getGameController(String gameName) {
        return getGameControllerByHash(gameName.hashCode());
    }

    /**
     * finds the GameController for a given GameName hash code
     */
    public GameController getGameControllerByHash(int gameNameHash) {
        return gameControllers.get("" + gameNameHash);
    }

    /**
     *  Dynamically loads GameControllers
     */
    private void initGameController(GameController gc) {
        String gameName = gc.getGameName();
        if (gc.init(this, getGameConfig(gameName))) {
            // add to our controllers hash
            GameController old = gameControllers.put("" + gameName.hashCode(), gc);
            if (old != null) {
                log.warn("There are two game have the same hashcode of name : \"" + old.getGameName()
                        + "\" and \"" + gc.getGameName() + "\"");
            }
        }
    }

    /**
     * pass the event on to the EventWriter
     */
    public void sendEvent(GameEvent e) {
        eventWriter.handleEvent(e);
    }

    /**
     * returns the GameConfig object for the given gameName
     */
    public GameConfig getGameConfig(String gameName) {
        // todo: implement getGameConfig()
        return null;
    }
//    /**
//     * fetches the Player for a given playerId
//     */
//    public static Player getPlayerById(String id) {
//        return (Player) playersByPlayerId.get(id);
//    }
//
//    /**
//     * fetches the Player for a given sessionId
//     */
//    public static Player getPlayerBySessionId(String id) {
//        return (Player) playersBySessionId.get(id);
//    }
//
//    /**
//     * add a player to our lists
//     */
//    public static void addPlayer(Player p) {
//        playersByPlayerId.put(p.getPlayerId(), p);
//        playersBySessionId.put(p.getSessionId(), p);
//    }
//
//    /**
//     * remove a player from our lists
//     */
//    public static void removePlayer(Player p) {
//        playersByPlayerId.remove(p.getPlayerId());
//        playersBySessionId.remove(p.getPlayerId());
//    }
}// GameServer

