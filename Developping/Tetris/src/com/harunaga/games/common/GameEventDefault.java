package com.harunaga.games.common;

import java.nio.ByteBuffer;

/**
 * GameEventDefault.java
 *
 * A basic GameEvent class, this can be extended for other Games
 * or a completely different class may be used as required by a specific game.
 * 
 * @author <a href="mailto:bret@hypefiend.com">bret barker</a>
 * @version 1.0
 */
public class GameEventDefault implements GameEvent {

    public static final int GROUP_SHIFT = 20; //eventType=(GROUP << GROUP_SHIFT) | subEventType
    //------------------------groupType constants-------------------------------------
    public static final int GROUP_LOGIN = 1;
    public static final int GROUP_COMPETTING = 2;
    public static final int GROUP_FINDING_GAME = 3;
    //-----------------------eventType constants---------------------------
    // C_* is for Client initiated events ( start >=5  )
    // S_* is for Server initiated events  ( start < 5 )
    // SB_* is for Server broadcast events
    
    //ok=1   fail=0   broadcast=3  error=6     get=8
    //GameEventDefault start by 10 and 50
    //--------------------Server-Client Event------------------
    //----------------------100....and.........500....--------------
    /** request to login */
    public static final int C_LOGIN = 10001;
    /** login ok */
    public static final int S_LOGIN_ACK_OK = 50001;
    /** login failed */
    public static final int S_LOGIN_ACK_FAIL = 50000;
    /** broadcast notice of a player login */
    public static final int SB_LOGIN = 50003;
    /** logout request */
    public static final int C_LOGOUT = 10010;
    /** broadcast notice of a player logout */
    public static final int SB_LOGOUT = 50013;
    /** notice of disconnect */
    public static final int S_DISCONNECT = 50020;
    /** used internally in server */
    public static final int C_DISCONNECT = 10020;
    /** broadcast a client disconnected accidently*/
    public static final int SB_CLIENT_ERROR = 50026;
    //--------------------Establish Game Event------------------
    //-----------101.....and......501.....-----------------
    /** request to create a new game */
    public static final int C_CREATE_GAME = 10101;
    /** request is received */
    public static final int S_CREATE_GAME_ACK = 50101;
    /** broadcast destroy game */
    public static final int SB_GAME_DESTROYED = 50106;
    /** request to join a game */
    public static final int C_JOIN_GAME = 10111;
    /** join success */
    public static final int S_JOIN_GAME_ACK_OK = 50111;
    /** join failure */
    public static final int S_JOIN_GAME_ACK_FAIL = 50110;
    /** broadcast notice that player has joined */
    public static final int SB_PLAYER_JOINED = 50113;
    /** request to quit game */
    public static final int C_QUIT_GAME = 10120;
    /** broadcast notice that player has quit */
    public static final int SB_PLAYER_QUIT = 50123;
    /** request a list of game already created */
    public static final int C_GET_LIST_CREATED_GAME = 10138;
    /** response from server for list of created game */
    public static final int S_LIST_CREATED_GAME = 50138;
    /** request to kick a player */
    public static final int C_KICK_PLAYER = 10141;
    /** notify be kicked*/
    public static final int S_GET_KICKED = 50141;
    //-------------------While Playing GameEvent---------------
    public static final int C_REQUEST_PAUSE = 10201;
    public static final int SB_PAUSED = 50203;
    //--------------------Chat Event------------------
    /** client chat mesg */
    public static final int C_CHAT_MSG = 10301;
    /** server broadcast chat mesg */
    public static final int SB_CHAT_MSG = 50303;
    //-----------------------------------------------------
    /** group */
    protected int groupType;
    /** event type */
    protected int eventType;
    /** playerID that sent the message (for client messages) it's will be change in server*/
    protected String playerId;
    /** player's session id */
    protected String sessionId;
    /** gameID that the event belongs to, if any */
    protected int gameId = -1;
    /** gameName that the event belongs to */
    protected String gameName;
    /** # of recipients */
    protected int numRecipients;
    /** array of event recipient playerIDs */
    protected String[] recipients;
    /** chat message or other command specific string */
    protected String message;

    /** 
     * default contructor
     */
    public GameEventDefault() {
    }

    /** 
     * constructor that takes eventType
     */
    public GameEventDefault(int type) {
        this.eventType = type;
    }

    /**
     * constructor that takes eventType and message
     */
    public GameEventDefault(int type, String message) {
        this.eventType = type;
        this.message = message;
    }

    public int getTypeGroup() {
        // return groupType;
        return eventType >> GROUP_SHIFT;
    }

    public void setType(int type) {
        eventType = type;
    }

    public int getType() {
        return eventType;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String id) {
        playerId = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String id) {
        sessionId = id;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public void setRecipients(String[] recipients) {
        this.recipients = recipients;
        if (recipients == null) {
            numRecipients = 0;
        } else {
            numRecipients = recipients.length;
        }
    }

    /** 
     * write the event to the given ByteBuffer
     * 
     * note we are using 1.4 ByteBuffers for both client and server
     * depending on the deployment you may need to support older java
     * versions on the client and use old-style socket input/output streams
     */
    public int write(ByteBuffer buff) {
        int pos = buff.position();

        buff.putInt(eventType);
        //eventType contain groupType, so don't need to put groupType
        NIOUtils.putStr(buff, playerId);
        // NIOUtils.putStr(buff, sessionId);
        buff.putInt(gameId);
        NIOUtils.putStr(buff, gameName);
//        buff.putInt(numRecipients);
//        for (int i = 0; i < numRecipients; i++) {
//            NIOUtils.putStr(buff, recipients[i]);
//        }
        NIOUtils.putStr(buff, message);

        // return the length of the event, this will get inserted at the beginning of the buffer
        // in the EventWriter so the Reader knows how many bytes to read for the payload
        return buff.position() - pos;
    }

    /**
     * read the event from the given ByteBuffer
     */
    public void read(ByteBuffer buff) {
        eventType = buff.getInt();
        //groupType = eventType >> GROUP_SHIFT;
        playerId = NIOUtils.getStr(buff);
        // sessionId = NIOUtils.getStr(buff);
        gameId = buff.getInt();
        gameName = NIOUtils.getStr(buff);
//        numRecipients = buff.getInt();
//        if (numRecipients > 0) {
//            recipients = new String[numRecipients];
//            for (int i = 0; i < numRecipients; i++) {
//                recipients[i] = NIOUtils.getStr(buff);
//            }
//        } else {
//            recipients = null;
//        }
        message = NIOUtils.getStr(buff);
    }
}// GameEvent

