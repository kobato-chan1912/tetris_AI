/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.states;

import com.harunaga.games.common.GameEvent;
import com.harunaga.games.common.client.font.Font;
import com.harunaga.games.common.client.input.InputManager;
import com.harunaga.games.common.client.effect.OpenDoorTransition;
import com.harunaga.games.common.client.effect.Transition;
import com.harunaga.games.common.client.net.GameEventListener;
import com.harunaga.games.common.client.net.NetManager;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.common.client.state.GameState;
import com.harunaga.games.tetris.TetrisEvent;
import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.tetris.client.GameManager;
import com.harunaga.games.tetris.client.net.TetrisClient;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFrame;
import org.apache.log4j.Logger;

/**
 *
 * @author Harunaga
 */
public abstract class TetrisGameState implements GameState, GameEventListener, ActionListener {

    AlphaComposite transparency05 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f);
    //state
    public static final String STATE_SPLASH = "_splash";
    public static final String STATE_PLAYING = "_playing";
    public static final String STATE_GAME_MENU = "_menu";
    public static final String STATE_LOGEDIN = "_logedin";
    public static final String STATE_COLLECTING_PLAYERS = "_collecting";
    public static final String STATE_OPTION = "_option";
    public static final String STATE_RESULT = "_result";
    /**
     *
     */
    protected Timer timer = new Timer(this.getName());
    protected JFrame frame = GameManager.frame;
    protected Image bg;
    protected Logger log = Logger.getLogger(this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1));
    protected TetrisClient net;
    ResourceManager resource;
    protected String nextState;
    protected boolean paused;
    //message
    private String notifyMessage;
    protected static final int MESSAGE_CENTER = 0;
    protected static final int MESSAGE_LEFT = 1;
    protected static final int MESSAGE_RIGHT = 2;
    protected static final int TIME_DEFAULT_FOR_DISPLAY_MESSAGE = 100;// = *40 milisecond
    protected int timeToDisplay;
    protected Font defaultFont = Font.getDefaultFont();
    //waiting flag for waiting some event
//    private int _wait;
    public static final int FLAG_WAITING_INPUT = 1;
    public static final int FLAG_WAITING_LOGIN_ACK = 1 << 1;
    public static final int FLAG_WAITING_GET_LIST_GAME_ACK = 1 << 2;
    public static final int FLAG_WAITING_CREATE_GAME_ACK = 1 << 3;
    public static final int FLAG_WAITING_JOIN_GAME_ACK = 1 << 4;
    public static final int FLAG_WAITING_SAVE_SCORE_ACK = 1 << 5;
    public static final int TIME_FOR_WAIT_LOGIN_ACK = 5000;
    public static final int TIME_FOR_WAIT_JOINGAME_ACK = 6000;
    public static final int TIME_DEFAULT_FOR_WAIT_SERVER_RESPONSE = 5000;

    public String checkForStateChange() {
        return nextState;
    }

    public void loadResources(ResourceManager resourceManager) {
        resource = resourceManager;
        bg = resourceManager.getImage(TetrisGameConfig.DEFAULT_BG);
    }

    public void start(InputManager inputManager, NetManager netManager) {
        nextState = null;
        net = (TetrisClient) netManager;
        if (frame == null || net == null) {
            throw new IllegalArgumentException("illegal frame or netManager");
        }
        createGUI(resource);
        frame.validate();
        frame.requestFocus();
    }

    public void stop() {
        frame.getContentPane().removeAll();
        timer.cancel();
    }

    abstract void createGUI(ResourceManager resourceManager);

    public void draw(Graphics2D g) {
        if (bg != null) {
            g.drawImage(bg, 0, 0,null);
        }
        g.setComposite(AlphaComposite.SrcOver);
        frame.getLayeredPane().paintComponents(g);
    }

    public Transition getEffect() {

        return new OpenDoorTransition(frame.getWidth(), frame.getHeight(), 1f, true);
    }

//    protected boolean isWaiting() {
//        return _wait != 0;
//    }
//
//    protected boolean isWaiting(int reasonFlag) {
//        return (_wait & reasonFlag) != 0;
//    }
//
//    protected void setWait(boolean aFlag) {
//    }
//
//    protected void setWait(boolean needToWait, int reasonFlag) {
//        if (needToWait) {
//            _wait = _wait | reasonFlag;
//        } else {
//            _wait = _wait & (~reasonFlag);
//        }
//    }
//
//    protected void resetWaitState() {
//        _wait = 0;
//    }
    protected void drawMessage(Graphics2D g, String s, int x, int y, int constrain) {
        g.setColor(Color.RED);
        if (defaultFont == null || s == null) {
            return;
        }
        switch (constrain) {
            case MESSAGE_RIGHT:
                defaultFont.drawString(s, x - defaultFont.stringWidth(s), y, g);
                break;
            case MESSAGE_LEFT:
                defaultFont.drawString(s, x, y, g);
                break;
            default:
                defaultFont.drawString(s, x - (defaultFont.stringWidth(s) >> 1), y, g);
        }
        if (timeToDisplay > 0) {
            timeToDisplay--;
        }
    }

    protected void drawMessage(Graphics2D g, int x, int y, int constrain) {
        drawMessage(g, notifyMessage, x, y, constrain);
    }

    public void displayMessage(String message, int time) {
        notifyMessage = message;
        timeToDisplay = time;
    }

    /**
     *
     * @param e
     */
    public void processEvent(GameEvent e) {
        if (e.getType() == TetrisEvent.S_DISCONNECT) {
            // net.setLogin(false);
            nextState = STATE_GAME_MENU;
            displayMessage("server error", 100);
        }
    }
}

class Timer {

    Logger log = Logger.getLogger(Timer.class);
    String name;
    int count = 0;
    private boolean canceled;
    Collection<Thread> tasks = new ArrayList<Thread>(3);

    Timer(String name) {
        this.name = name;
    }

    public synchronized void schedule(final TimerTask task, final int delay) {
        canceled = false;
        Thread thread = new Thread("task " + count) {

            @Override
            public synchronized void run() {
                try {
                    wait(delay);
                    if (!canceled) {
                        task.run();
                    }
                } catch (InterruptedException ex) {
                }
                log.info(getName() + " finished");
            }
        };
        tasks.add(thread);
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void cancel() {
        canceled = true;
        for (Thread thread : tasks) {
            thread.interrupt();
        }
    }
}

interface TimerTask {

    public void run();
}
