/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.states;

import com.harunaga.games.common.GameEvent;
import com.harunaga.games.common.client.miscellaneous.SizeFixedButton;
import com.harunaga.games.tetris.client.form.ComponentProvider;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.tetris.TetrisEvent;
import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.tetris.TetrisGamePlayer;
import com.harunaga.games.tetris.client.form.ChatCanvas;
import com.harunaga.games.tetris.client.form.ChatInput;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author Harunaga
 */
public class CollectingPlayerState extends TetrisGameState implements ActionListener {

    SizeFixedButton backbt;
    JCheckBox ready;
    SizeFixedButton startbt;
    ChatCanvas chat;
    ChatInput input = ChatInput.instance;

    @Override
    void createGUI(ResourceManager resourceManager) {
        if (backbt == null) {
            ComponentProvider btprovider = new ComponentProvider(resource);
            backbt = btprovider.createOvalButton("back", TetrisGameConfig.SMALL_ON_FOCUS_IMG,
                    "return to menu", this, Cursor.HAND_CURSOR);
            ready = btprovider.getDefaultCheckbox("Ready", this, false);
            startbt = btprovider.getDefaultButton("Start", this);
        } else {
            ready.setEnabled(true);
            startbt.setEnabled(true);
        }
        ready.setSelected(false);
        //if input.setvisible(false) ??????
//        ready.setFocusable(false);
//        startbt.setFocusable(false);
        //------------------------botton pane
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panel1.add(ready);
        panel1.add(startbt);
        panel1.setOpaque(false);
        panel1.setAlignmentY(JPanel.TOP_ALIGNMENT);
        bottomPanel.add(Box.createHorizontalStrut(10));
        bottomPanel.add(backbt);
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(panel1);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout(10, 20));
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        //------------------------------Chat pane
        Container chatPane = new Container();
        chatPane.setLayout(new BoxLayout(chatPane, BoxLayout.Y_AXIS));
        chat = new ChatCanvas();
        chatPane.add(chat);
        chat.setAlignmentX(ChatCanvas.RIGHT_ALIGNMENT);
        chatPane.add(Box.createVerticalStrut(10));
//        chatPane.add(input);
//        input.setAlignmentX(ChatCanvas.RIGHT_ALIGNMENT);
        contentPane.add(chatPane, BorderLayout.LINE_END);
//        input.setListener(this);

        frame.getLayeredPane().add(input, JLayeredPane.POPUP_LAYER);
        input.setListener(this);
        input.setVisible(true);
        // input.setLocation((frame.getWidth() - input.getWidth()) >> 1, frame.getHeight() >> 1);        
        input.setLocation(10, 3 * frame.getHeight() >> 2);
        startbt.setVisible(net.isGameOwner());
        //frame.revalidate();
    }

//    @Override
//    public void start(InputManager inputManager, NetManager netManager) {
//        super.start(inputManager, netManager);
//    }
    public String getName() {
        return STATE_COLLECTING_PLAYERS;
    }

    public void update(long elapsedTime) {
        // System.out.println(input.isVisible()+ " x" + input.getX()+ " y" + input.getY());
//        if (TetrisGameConfig.CHAT.isPressed()) {
//            input.setVisible(true);
//        }
    }

    @Override
    public void stop() {
        frame.getLayeredPane().remove(input);
        chat = null;
        super.stop();
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        drawPlayer(g);
        frame.getLayeredPane().paintComponents(g);
    }

    private void drawPlayer(Graphics2D g) {
        Collection<TetrisGamePlayer> players = net.getAllPlayers();
        int y = 30;
        for (TetrisGamePlayer player : players) {
            g.setColor(TetrisGameConfig.PLAYER_COLORS[player.color]);
            StringBuffer sb = new StringBuffer(100).append(player.name).append("  ").append((player.ready ? "ready" : "not ready"));
            defaultFont.drawString(sb.toString(), 5, y, g);
            y += defaultFont.getHeight();
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void processEvent(GameEvent e) {
        super.processEvent(e);
        int type = e.getType();
        if (type == TetrisEvent.SB_CHAT_MSG) {
            String pid = e.getPlayerId();
            TetrisGamePlayer player = net.getPlayer(pid);
            if (player != null) {
                chat.putMessage(player.name + " say : " + e.getMessage());
            } else {
                log.warn("Null player");
            }
        } else if (type == TetrisEvent.SB_PLAYER_JOINED) {
            net.addPlayer(e.getMessage());
        } else if (type == TetrisEvent.SB_PLAYER_QUIT) {
            net.removePlayer(e.getMessage());
        } else if (type == TetrisEvent.SB_GAME_DESTROYED) {
            nextState = STATE_LOGEDIN;
        } else if (type == TetrisEvent.S_START_GAME_FAIL) {
            startbt.setEnabled(true);
            displayMessage("Can not start the game ", TIME_DEFAULT_FOR_DISPLAY_MESSAGE);
        } else if (type == TetrisEvent.SB_GAME_READY) {
            net.setInMultiplayerGame(true);
            nextState = STATE_SPLASH;
        } else if (type == TetrisEvent.SB_PLAYER_READY) {
            processReadyEvent(e);
        }
    }

    private void processReadyEvent(GameEvent event) {
        TetrisGamePlayer player = net.getPlayer(event.getPlayerId());
        if (player != null) {
            player.ready = Boolean.parseBoolean(event.getMessage());
        } else {
            log.error("Null player send ready event");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (nextState != null) {
            return;
        }
        Object src = e.getSource();
        if (src == input) {
            //input.setVisible(false);
            String text = e.getActionCommand();
            if (text != null && !text.isEmpty()) {
                chat.putMessage("me: " + text);
                input.clear();
                net.send(new TetrisEvent(TetrisEvent.C_CHAT_MSG, text));
            }
            // log.info("send chat-event: " + text);
        } else if (src == ready) {
            net.send(new TetrisEvent(TetrisEvent.C_READY, ready.isSelected() + ""));
            net.getPlayer(net.playerId).ready = ready.isSelected();
        } else if (src == startbt) {
            startbt.setEnabled(false);
            net.send(new TetrisEvent(TetrisEvent.C_START_GAME));
        } else if (src == backbt) {
            log.info("go back");
            net.send(new TetrisEvent(TetrisEvent.C_QUIT_GAME));
            nextState = STATE_LOGEDIN;
        }
    }

    public void actionPerformed(Object provider) {
    }
}
