/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris;

import com.harunaga.games.common.GameEventDefault;
import com.harunaga.games.common.NIOUtils;
import java.nio.ByteBuffer;

/**
 *
 * @author Harunaga
 */
public class TetrisEvent extends GameEventDefault {
    //-----------------------TetrisEventType constants---------------------------
    // C_* is for Client initiated events ( start >=5  )
    // S_* is for Server initiated events  ( start < 5 )
    // SB_* is for Server broadcast events

    //ok=1   fail=0   broadcast=3  error=6     get=8
    //TetrisEvent start by 11 and 51
    //------------------One Player Event-----------------------------------
    //----------------110....and....510....---------------------------
    /** request some highest score */
    public static final int C_REQUEST_SCORE = 11008;
    /** response the score of some player */
    public static final int S_RESPONSE_SCORE = 51008;
    /** ask to save score*/
    public static final int C_SAVE_SCORE = 11011;
    /** saved successfully*/
    public static final int S_SAVE_SCORE_ACK_OK = 51011;
    /** save fail */
    public static final int S_SAVE_SCORE_ACK_FAIL = 51010;
    //-------------------Multiple Player Event------------------------------
    //-----------------------------Playing----------------------
    //------------------111.....and........511...........-------------
    /** client send score */
    public static final int C_SEND_SCORE = 11101;
    /** broadcast the score of one player */
    public static final int SB_CLIENT_SCORE = 51103;
    /** cause the client game be sprouted up*/
    public static final int SB_SPROUT_UP = 51113;
    /** notify the time */
    public static final int SB_TIME = 51123;
    /** player die */
    public static final int C_GAME_OVER = 11131;
    //--------------------------Prepare------------------------------
    //--------------112......and............512.......--------------------
    /** notify that i am ready */
    public static final int C_READY = 11201;
    /** notify to the rest that this player are ready*/
    public static final int SB_PLAYER_READY = 51203;
    /** ask for start the game*/
    public static final int C_START_GAME = 11211;
    /** start fail */
    public static final int S_START_GAME_FAIL = 51210;
    /** notify all player to ready */
    public static final int SB_GAME_READY = 51223;
    /** notify start the game*/
    public static final int SB_GAME_START = 51233;
    //-------------------------Finish-------------------------
    //----------------113.....and........513....------------------
    /** notify the time is up */
    public static final int SB_TIME_UP = 51303;
    /** broadcast the result of game*/
    public static final int SB_RESULT = 51313;
    String username;

    public TetrisEvent() {
    }

    public TetrisEvent(int type) {
        super(type);
    }

    public TetrisEvent(int type, String message) {
        super(type, message);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void read(ByteBuffer buff) {
        super.read(buff);
        username = NIOUtils.getStr(buff);
    }

    @Override
    public int write(ByteBuffer buff) {
        int pos = buff.position();
        super.write(buff);
        NIOUtils.putStr(buff, username);

        // return the length of the event, this will get inserted at the beginning of the buffer
        // in the EventWriter so the Reader knows how many bytes to read for the payload
        return buff.position() - pos;
    }
}
