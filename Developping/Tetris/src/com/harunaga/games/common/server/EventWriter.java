package com.harunaga.games.common.server;

import com.harunaga.games.common.*;
import java.nio.*;
import java.nio.channels.*;
import org.apache.log4j.Logger;

/**
 * EventWriter.java
 *
 * 
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public class EventWriter extends Wrap {

    /** reference to the GameServer */
    private static GameServer gameServer;

    /**
     * contructor.
     */
    public EventWriter(GameServer gameServer, int numWorkers) {
        this.gameServer = gameServer;
        initWrap(numWorkers);
    }

    /**
     * note we override the Wrap's run method here
     * doing essentially the same thing, but
     * first we allocate a ByteBuffer for this
     * thread to use
     */
    @Override
    public void run() {
        ByteBuffer writeBuffer = ByteBuffer.allocateDirect(Globals.MAX_EVENT_SIZE);
        GameEvent event;
        running = true;
        int count = 0;
        while (running) {
            try {
                if ((event = eventQueue.deQueue()) != null) {
//                    log.info("writing event " + (++count));
                    processEvent(event, writeBuffer);
                }
            } catch (InterruptedException e) {
            }
        }
    }

    /** unused */
    protected void processEvent(GameEvent event) {
    }

    /**
     * our own version of processEvent that takes
     * the additional parameter of the writeBuffer
     * @param event
     * @param writeBuffer  
     */
    protected void processEvent(GameEvent event, ByteBuffer writeBuffer) {
        NIOUtils.prepBuffer(event, writeBuffer);
        GameController gc = gameServer.getGameController(event.getGameName());
        String[] recipients = event.getRecipients();
//        if (recipients != null) {
//            log.info("sendEvent: type=" + event.getType() + ", id="
//                    + event.getPlayerId() + ", msg=" + event.getMessage());
//            String playerId = event.getPlayerId();
//            write(gc.getPlayerById(playerId), writeBuffer);
//        } else {

        /**
         * the step to check if recipients equal null will be omit because it was already checked in
         * the call to send event of GameController class
         */
        int n = recipients.length;
        for (int i = 0; i < n; i++) {
            if (recipients[i] != null) {
                log.info("writeEvent: type=" + event.getType() + ", id="
                        + recipients[i] + ", msg=" + event.getMessage());
                write(gc.getPlayerById(recipients[i]), writeBuffer);
            }
        }
        // }
    }

    /**
     * write the event to the given playerId's channel
     */
    private void write(Player player, ByteBuffer writeBuffer) {
        if (player == null) {
            log.warn("Null player. Perhap he was removed. so the message won't be sent");
            return;
        }
//        log.info("writing event to " + player.getName());
        SocketChannel channel = player.getChannel();
        NIOUtils.channelWrite(channel, writeBuffer);
        log.info("write event finish ----------------------------------------");
    }
}// EventWriter

