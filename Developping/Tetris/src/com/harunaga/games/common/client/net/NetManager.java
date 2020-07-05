package com.harunaga.games.common.client.net;

import com.harunaga.games.common.*;
import com.harunaga.games.tetris.TetrisEvent;
import java.nio.channels.*;
import java.net.*;
import java.io.*;

import org.apache.log4j.*;

/**
 *It's work as an event transporter.
 * It's job is to give the event need to process to the listener and put the event need to send
 * to the event writer
 *
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public abstract class NetManager implements Runnable {

    GameEventListener gameEventListener;
    /** log4j logger */
    protected static Logger log = Logger.getLogger("NetManager");
    /** address of server */
    protected String serverAddress;
    /** connection to server */
    protected SocketChannel channel;
    /** queue for incoming events */
    protected EventQueue inQueue;
    /** queue for outoging events */
    protected EventQueue outQueue;
    /** reference to NIOEventReader that reads events from the server */
    protected NIOEventReader netReader;
    protected NIOEventWriter netWriter;
    /** id of our player */
    public String playerId;
    /** id of our current opponent */
    protected String opponentId;
    /** are we login or not*/
    protected boolean logedIn = false;
    /** still running? */
    protected boolean running = true;
    private boolean inMultiplayerGame;

    public void setInMultiplayerGame(boolean inGame) {
        this.inMultiplayerGame = inGame;
    }

    public boolean isInMultiplayerGame() {
        return inMultiplayerGame;
    }
    Thread thread;
//    private final Object lock = new Object();

    public NetManager() {
    }

    public boolean isLogedin() {
        return logedIn;
    }

    public void setListener(GameEventListener listener) {
        gameEventListener = listener;
    }

    /** 
     * do some initialization
     */
    public boolean init(String serverAddress) {
        logedIn = false;
        inMultiplayerGame = false;
        this.serverAddress = serverAddress;
        inQueue = new EventQueue("NetManager-in");
        outQueue = new EventQueue("NetManager-out");
        // connect to the server
        if (!connect()) {
            return false;
        }
        // start our net reader
        netReader = new NIOEventReader(this, channel, inQueue);
        netReader.start();
        netWriter = new NIOEventWriter(this, channel, outQueue);
        netWriter.start();
        return true;
    }

    public boolean isConnect() {
        if (channel == null) {
            return false;
        }
        return channel.isConnected();
    }

    @Override
    public void run() {
        running = true;
        // main loop
        try {
            while (running) {
                GameEvent inEven = inQueue.deQueue();
                if (inEven != null) {
                    GameEventListener listener = gameEventListener;
                    if (listener != null) {
                        log.info("Process Event: " + inEven.getType());
                        listener.processEvent(inEven);
                    }
                }
            }
        } catch (InterruptedException ex) {
            log.info("Net manager was interrpted");
        }
        log.info("Net manager Stop");
    }

    public void send(GameEvent e) {
        outQueue.enQueue(e);
    }

    /**
     * connect to the server
     */
    protected boolean connect() {
        log.info("connect()");
        try {
            // open the socket channel
            channel = SocketChannel.open(new InetSocketAddress(InetAddress.getByName(serverAddress), Globals.PORT));
            channel.configureBlocking(false);
            // we don't like Nagle's algorithm
            channel.socket().setTcpNoDelay(true);
            return true;
        } catch (UnknownHostException unhe) {
            log.error("unknown host: " + serverAddress);
            return false;
        } catch (ConnectException ce) {
            log.error("Connect Exception: " + ce.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Exception while connecting", e);
            return false;
        }
    }

    public void setLogin(boolean aFlag) {
        logedIn = aFlag;
    }

    /**
     * send the login event
     */
    public void login() {
        GameEvent e = createLoginEvent();
        send(e);
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * shutdown the client
     * stop our readers and close the channel
     */
    public void shutdown() {
        running = false;
        if (netWriter != null) {
            netWriter.shutdown();
//            try {
//                synchronized (this) {
//                    wait(1000);
//                }
//            } catch (InterruptedException ex) {
//            }
        }
        if (netReader != null) {
            netReader.shutdown();
        }

        if (thread != null) {
            thread.interrupt();
            //log.info("thread is interrupted : " + thread.interrupted());
        }
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException ioe) {
                log.error("exception while closing channel", ioe);
            }
        }
    }

    /** 
     * utility method to call Thread.sleep()
     */
    private void threadSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("hi hi. bi ngat rui");
        }
    }

    /**
     * subclasses must implement to provide their GameName
     */
    public abstract String getGameName();

    /**
     * subclasses must implement this factory method
     */
    public abstract GameEvent createGameEvent();

    /**
     * and this one to create a game specific login event
     */
    public abstract GameEvent createLoginEvent();

    /**
     * and this one to create a game specific disconnect event
     */
    public abstract GameEvent createDisconnectEvent(String reason);
}
