package com.harunaga.games.common;

import java.nio.*;
import java.nio.channels.*;
import org.apache.log4j.Logger;

/**
 * NIOUtils.java
 *
 * Misc utility functions to simplify dealing w/NIO channels and buffers
 *
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public class NIOUtils {

    static Logger log = Logger.getLogger("NIOUtils");

    /**
     * For debug
     * @param e
     */
    public static void printEvent(GameEvent e) {
        log.info("\n\n\n-----------------Print Event-------------\n");
        log.info("Game name: " + e.getGameName());
        System.out.println("Type: " + e.getType());
        String[] rec = e.getRecipients();
        if (rec != null) {
            log.info("Number recipients: " + rec.length);
            for (int i = 0; i < rec.length; i++) {
                log.info("Number " + i + ": " + rec[i]);
            }
        }
        log.info("Message: " + e.getMessage());
        log.info("\n-----------------End----------------\n\n");
    }

    /**
     * first, writes the header, then the 
     * event into the given ByteBuffer
     * in preparation for the channel write
     */
    public static void prepBuffer(GameEvent event, ByteBuffer writeBuffer) {
        // write header
        writeBuffer.clear();
        writeBuffer.putInt(0); // todo: clientId
        if (event.getGameName() != null) {
            writeBuffer.putInt(event.getGameName().hashCode());
        } else {
            writeBuffer.putInt(0);
        }
        int sizePos = writeBuffer.position();
        writeBuffer.putInt(0);// placeholder for payload size
        // write event
        int payloadSize = event.write(writeBuffer);

        // insert the payload size in the placeholder spot
        writeBuffer.putInt(sizePos, payloadSize);

        // prepare for a channel.write
        writeBuffer.flip();
    }

    /** 
     * write the contents of a ByteBuffer to the given SocketChannel
     * @param channel 
     */
    public static void channelWrite(final SocketChannel channel, ByteBuffer writeBuffer) {
        long nbytes = 0;
        long toWrite = writeBuffer.remaining();

        // loop on the channel.write() call and synchrosized block since it will not necessarily
        // write all bytes in one shot
        try {
            //make sure that differ thread will not write to this channel while the channel is writing
            synchronized (channel) {
                while (nbytes != toWrite) {
                    nbytes += channel.write(writeBuffer);
                    try {
                        Thread.sleep(Globals.CHANNEL_WRITE_SLEEP);
                    } catch (InterruptedException e) {
                    }
                }
            }
        } catch (ClosedChannelException cce) {
            log.error(channel.socket().getInetAddress() + "is closed", cce);
        } catch (Exception e) {
        }

        // get ready for another write if needed
        writeBuffer.rewind();
    }

    /**
     * write a String to a ByteBuffer, 
     * prepended with a short integer representing the length of the String
     */
    public static void putStr(ByteBuffer buff, String str) {
        if (str == null) {
            buff.putShort((short) 0);
        } else {
            byte[] data = str.getBytes();
            buff.putShort((short) data.length);
            buff.put(data);
        }
    }

    /**
     * read a String from a ByteBuffer 
     * that was written w/the putStr method
     */
    public static String getStr(ByteBuffer buff) {

        short len = buff.getShort();
        if (len == 0) {
            return null;
        } else {
            byte[] b = new byte[len];
            buff.get(b);
            return new String(b);
        }
    }
}
