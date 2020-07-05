/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.net;

import com.harunaga.games.common.EventQueue;
import com.harunaga.games.common.GameEvent;
import com.harunaga.games.common.GameEventDefault;
import com.harunaga.games.common.Globals;
import com.harunaga.games.common.NIOUtils;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.apache.log4j.Logger;

/**
 *
 * @author Harunaga
 */
public class NIOEventWriter extends Thread {

    boolean running;
    NetManager clientNetManager;
    /** connection to server */
    protected SocketChannel channel;
    /** buffer for outgoing events */
    private ByteBuffer writeBuffer = ByteBuffer.allocate(Globals.MAX_EVENT_SIZE);
    /** queue for outoging events */
    private EventQueue queue;
    private Logger log = Logger.getLogger("NIOEventWriter");

    public NIOEventWriter(NetManager et, SocketChannel channel, EventQueue queue) {
        super("NIOEventWriter");
        this.clientNetManager = et;
        this.queue = queue;
        this.channel = channel;
    }

    @Override
    public void run() {
        int count = 0;
        running = true;
        while (running) {
            try {
                GameEvent outEvent = queue.deQueue();
                writeEvent(outEvent);
                log.info("Event number : " + (++count));
            } catch (InterruptedException ex) {
            }
        }
        log.info("EventWriter stop");
    }

    private void writeEvent(GameEvent ge) {
        if (ge == null) {
            return;
        }
        log.info("writeEvent: type=" + ge.getType() + ", msg=" + ge.getMessage());
        // set the gamename and player id
        ge.setGameName(clientNetManager.getGameName());
        // ge.setPlayerId(clientNetManager.playerId);
        NIOUtils.prepBuffer(ge, writeBuffer);
        NIOUtils.channelWrite(channel, writeBuffer);
    }

    public void shutdown() {
        running = false;
        interrupt();
        //queue.notifyAll();
//        //send event to server and wake this thread up
//        queue.enQueue(new GameEventDefault(GameEventDefault.C_DISCONNECT));
    }
}
