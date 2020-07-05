package com.harunaga.games.common.server;

import com.harunaga.games.common.*;
import java.nio.*;
import java.nio.channels.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * SelectAndRead.java
 *
 * handles reading from all clients using a Selector
 * and hands off events to the appropriatae GameControllers
 *
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public class SelectAndRead extends Thread {

    /** log4j logger */
    private static Logger log = Logger.getLogger("SelectAndRead");
    /** pending connections */
    private LinkedList newClients;
    //    /** timeout for the selector's select() call */
    //    private static final long SELECT_TIMEOUT = 250;
    /** the selector, multiplexes access to client channels */
    private Selector selector;
    /** reference to the GameServer */
    private GameServer gameServer;

    /**
     * Constructor.
     */
    public SelectAndRead(GameServer gameServer) {
        this.gameServer = gameServer;
        newClients = new LinkedList();
        setDaemon(true);
    }

    /**
     * adds to the list of pending clients
     */
    public void addNewClient(SocketChannel clientChannel) {
        synchronized (newClients) {
            newClients.addLast(clientChannel);
        }
        // force selector to return
        // so our new client can get in the loop right away
        selector.wakeup();
    }

    /**
     * loop forever, first doing our select()
     * then check for new connections
     */
    @Override
    public void run() {
        try {
            selector = Selector.open();

            while (true) {
                select();
                checkNewConnections();

                // sleep just a bit
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                }
            }

        } catch (IOException e) {
            log.fatal("exception while opening Selector", e);
        }
        log.info("SelectAndRead stop");
    }

    /**
     * check for new connections
     * and register them with the selector
     */
    private void checkNewConnections() {
        synchronized (newClients) {
            while (newClients.size() > 0) {
                try {
                    SocketChannel clientChannel = (SocketChannel) newClients.removeFirst();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ, new Attachment());
                } catch (ClosedChannelException cce) {
                    log.error("channel closed", cce);
                } catch (IOException ioe) {
                    log.error("ioexception on clientChannel", ioe);
                }
            }
        }
    }

    /**
     * do our select, read from the channels
     * and hand off events to GameControllers
     */
    private void select() {
        try {
            // this is a blocking select call but will
            // be interrupted when new clients come in
            selector.select();
            Set readyKeys = selector.selectedKeys();

            Iterator<SelectionKey> i = readyKeys.iterator();
            while (i.hasNext()) {
                SelectionKey key = i.next();
                i.remove();
                SocketChannel channel = (SocketChannel) key.channel();
                Attachment attachment = (Attachment) key.attachment();

                try {
                    // read from the channel
                    long nbytes = channel.read(attachment.readBuff);
                    // check for end-of-stream condition
                    if (nbytes == -1) {
                        log.info("disconnect: " + channel.socket().getInetAddress()
                                + ", end-of-stream");
                        delegateDisconnectEvent(attachment, GameServer.CLIENT_END_OF_STREAM, "end-of-stream");
                        channel.close();
                    }

                    // check for a complete event
                    try {
                        if (attachment.readBuff.position() >= attachment.HEADER_SIZE) {
                            attachment.readBuff.flip();

                            // read as many events as are available in the buffer
                            while (attachment.eventReady()) {
                                // GameEvent event = getEvent(attachment);
                                delegateEvent(attachment, channel);
                                attachment.reset();
                            }
                            // prepare for more channel reading
                            attachment.readBuff.compact();
                        }
                    } catch (IllegalArgumentException e) {
                        log.error("illegal argument exception", e);
                    }
                } catch (IOException ioe) {
                    log.warn("IOException during read(), closing channel:" + channel.socket().getInetAddress());
                    delegateDisconnectEvent(attachment, GameServer.CLIENT_IOEXCEPTION, "IOException during read()");
                    channel.close();
                }
            }
        } catch (IOException ioe2) {
            log.warn("IOException during select(): " + ioe2.getMessage());
        } catch (Exception e) {
            log.error("exception during select()", e);
        }
    }

    /**
     * read an event from the attachment's payload
     */
    private void delegateEvent(Attachment attachment, SocketChannel channel) {
        GameEvent event = null;
        ByteBuffer bb = ByteBuffer.wrap(attachment.payload);

        // get the controller and tell it to instantiate an event for us
        GameController gc = gameServer.getGameControllerByHash(attachment.gameNameHash);
        if (gc == null) {
            log.warn("No gameCotroller with gameNamehash :" + attachment.gameNameHash);
            return;
        }
        Player player = attachment.player;
        boolean needToCreatePlayer = false;
        if (player == null) {
            needToCreatePlayer = true;
        } else if (player.getGameNameHash() != attachment.gameNameHash) {
            needToCreatePlayer = true;
            GameController oldGC = gameServer.getGameControllerByHash(player.getGameNameHash());
            oldGC.removePlayer(player);
            log.info("Player have changed the game to " + gc.getGameName());
        }
        if (needToCreatePlayer) {
            player = gc.createNextPlayer();
            player.setChannel(channel);
            attachment.player = player;
            log.debug("delegate event, new player created and channel set, player:"
                    + player.getPlayerId() + ", channel: " + channel);
        }
        event = gc.createGameEvent();
        // read the event from the payload
        event.read(bb);
        event.setPlayerId(player.getPlayerId());
        gc.handleEvent(event);
        // return event;
    }

    private void delegateDisconnectEvent(Attachment attachment, int type, String reason) {
        Player player = attachment.player;
        if (player == null) {
            log.info("this client hasn't sent game event yet");
            return;
        }
        GameController gc = gameServer.getGameControllerByHash(player.getGameNameHash());
        if (gc == null) {
            log.error("No gameCotroller with gameNamehash :" + player.getGameNameHash()
                    + " which created player " + player.getPlayerId());
            return;
        }
        GameEvent event = gc.createDisconnectEvent(player, type, reason);
        event.setPlayerId(player.getPlayerId());
        gc.handleEvent(event);
    }
    /**
     * pass off an event to the appropriate GameController
     * based on the GameName of the event
     */
//    private void delegateEvent(GameEvent event, SocketChannel channel) {
//        if (event == null) {
//            log.error("event null");
//            return;
//        } else if (event.getGameName() == null) {
//            log.error("GameServer.handleEvent() : gameName is null");
//            return;
//        }
//
//        GameController gc = gameServer.getGameController(event.getGameName());
//        if (gc == null) {
//            log.error("No GameController for gameName: " + event.getGameName());
//            return;
//        }
////        Player p = gameServer.getPlayerById(event.getPlayerId());
////        if (p != null) {
////            if (p.getChannel() != channel) {
////                log.warn("player is on a new channel, must be reconnect.");
////                p.setChannel(channel);
////            }
////        } else {
////            // first time we see a playerId, create the Player object
////            // and populate the channel, and also add to our lists
////            p = gc.createPlayer();
////            p.setPlayerId(event.getPlayerId());
////            p.setChannel(channel);
////            gameServer.addPlayer(p);
////            log.debug("delegate event, new player created and channel set, player:"
////                    + p.getPlayerId() + ", channel: " + channel);
////        }
//
//    }
}// SelectAndRead

