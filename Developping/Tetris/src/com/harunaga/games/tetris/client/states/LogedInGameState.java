/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.states;

import com.harunaga.games.common.GameEvent;
import com.harunaga.games.common.client.input.InputManager;
import com.harunaga.games.common.client.net.NetManager;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.tetris.TetrisEvent;
import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.tetris.TetrisGamePlayer;
import com.harunaga.games.tetris.client.form.HListBox;
import com.harunaga.games.tetris.client.form.Menu;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Harunaga
 */
public class LogedInGameState extends TetrisGameState {

    Menu menu;
    private HListBox list;
    public static final String CREATE_GAME = "_create";
    public static final String JOINT_GAME = "_joint";
    public static final String BACK = "_back";

    @Override
    void createGUI(ResourceManager resourceManager) {
        //create menu
        menu = new Menu(new ImageIcon(resourceManager.getImage("menu/logedin_menu.png")),
                new ImageIcon(resourceManager.getImage("menu/logedin_menu_rollover.png")), this);
        menu.setFont(TetrisGameConfig.FONT_MENU_60);
        menu.setItemAlignment(Menu.CENTER_ALIGNMENT);
        menu.setForeground(Color.YELLOW);
        menu.addItem("Create a multiplayer game", CREATE_GAME);
        menu.add(Box.createVerticalStrut(10));
        menu.addItem("Join a game", JOINT_GAME);
        menu.add(Box.createVerticalStrut(10));
        menu.addItem("Go back", BACK);
        menu.setSize(menu.getPreferredSize());
        menu.setLocation((frame.getWidth() - menu.getWidth()) >> 1, frame.getHeight() - menu.getHeight() - 20);
        //create list
        list = new HListBox(new ImageIcon(resourceManager.getImage("text_rollover.png")), this);
//        list.addItem("1", "2");
//        list.addItem("1", "3");
//        list.setSize(list.getPreferredSize());
        list.setForeground(Color.RED);
        list.setLocation(5, 2);
        //------------------Add to frame----------------------
        Container container = frame.getContentPane();
        container.setLayout(null);
        container.add(list);
        container.add(menu);
//        System.out.println("MenuH=" + menu.getHeight());
//        System.out.println("MenuY=" + menu.getY());
//        System.out.println("FrameH=" + frame.getHeight());

//        displayMessage("me kiep", 100);
//        Component c = list.getParent();
//        System.out.println(c.getName());
    }

    public String getName() {
        return STATE_LOGEDIN;
    }

    @Override
    public void stop() {
        super.stop();
        menu = null;
        list = null;
    }

    @Override
    public void start(InputManager inputManager, NetManager netManager) {
        super.start(inputManager, netManager);
        list.clear();
        list.setVisible(false);
    }

    public void update(long elapsedTime) {
    }

    @Override
    public void processEvent(GameEvent e) {
        super.processEvent(e);
        int type = e.getType();
        if (type == TetrisEvent.S_CREATE_GAME_ACK) {
            changeState(e, true);
        } else if (type == TetrisEvent.S_LIST_CREATED_GAME) {
            timer.cancel();
            processListGame(e.getMessage());
            return;
        } else if (type == TetrisEvent.S_JOIN_GAME_ACK_FAIL) {
            timer.cancel();
            displayMessage("Cannot connect to server. reason:" + e.getMessage(),
                    TIME_DEFAULT_FOR_DISPLAY_MESSAGE);
        } else if (type == TetrisEvent.S_JOIN_GAME_ACK_OK) {
            changeState(e, false);
        }

    }

    private void changeState(GameEvent event, boolean isGameOwner) {
        timer.cancel();
        net.removeAllPlayers();
        net.playerId = event.getPlayerId();
        net.setGameOwner(isGameOwner);
        if (isGameOwner) {
            net.addPlayer(new TetrisGamePlayer(net.getUsername(), event.getPlayerId()));
        } else {
            net.addPlayers(event.getMessage());
        }
        nextState = STATE_COLLECTING_PLAYERS;
    }

    private void processListGame(String listGame) {
        log.info("process list game");
        boolean gameExist = false;
        if (listGame != null) {
            StringTokenizer st = new StringTokenizer(listGame,
                    "" + TetrisGameConfig.GAME_DELIMITER);
            list.clear();
            while (st.hasMoreTokens()) {
                StringTokenizer item = new StringTokenizer(st.nextToken(),
                        "" + TetrisGameConfig.GAME_ATTRIBUTE_DELIMITER);
                if (item.countTokens() >= 2) {
                    gameExist = true;
                    list.addItem(item.nextToken(), item.nextToken());
                }
            }
        }
        if (gameExist) {
            list.setSize(list.getPreferredSize());
            list.setVisible(true);
        } else {
            displayMessage("No game exist.", TIME_DEFAULT_FOR_DISPLAY_MESSAGE);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        if (timeToDisplay > 0) {
            drawMessage(g, frame.getWidth(), 5, MESSAGE_RIGHT);
        }
        // System.out.println("X=" + list.getX() + "  y=" + list.getY() + "  W=" + list.getWidth() + " h=" + list.getHeight());
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        String key = e.getActionCommand();
        if (src instanceof HListBox) {
            if (key != null) {
                log.info("join in game of " + key);
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        displayMessage("You cannot join that game. reason: out of ack time",
                                TIME_DEFAULT_FOR_DISPLAY_MESSAGE);
                    }
                }, TIME_FOR_WAIT_JOINGAME_ACK);
                net.send(new TetrisEvent(TetrisEvent.C_JOIN_GAME, key));
            }
        } else if (src instanceof Menu) {
            if (CREATE_GAME.equals(key)) {
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(frame, "Cannot connect to server. reason: out of ack time",
                                "Join game response", JOptionPane.INFORMATION_MESSAGE);
                    }
                }, TIME_DEFAULT_FOR_WAIT_SERVER_RESPONSE);
                net.send(new TetrisEvent(TetrisEvent.C_CREATE_GAME));
            } else if (JOINT_GAME.equals(key)) {
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(frame, "Cannot connect to server. reason: out of ack time",
                                "Join game response", JOptionPane.INFORMATION_MESSAGE);
                    }
                }, TIME_DEFAULT_FOR_WAIT_SERVER_RESPONSE);
                net.send(new TetrisEvent(TetrisEvent.C_GET_LIST_CREATED_GAME));
            } else if (BACK.equals(key)) {
//                list.addItem("1", "2");
//                list.addItem("1", "3");
//                list.addItem("1", "4");
//                list.setSize(list.getPreferredSize());
//                list.setVisible(true);
                timer.cancel();
                nextState = STATE_GAME_MENU;
            }
        } else {
            log.warn("wrong provider for the waiting input");
        }
    }
}
