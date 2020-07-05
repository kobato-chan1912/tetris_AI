/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.states;

import com.harunaga.games.common.GameEvent;
import com.harunaga.games.common.client.input.InputManager;
import com.harunaga.games.common.client.effect.FallingEffect;
import com.harunaga.games.common.client.net.NetManager;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.common.client.state.GameStateManager;
import com.harunaga.games.tetris.TetrisEvent;
import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.common.client.miscellaneous.GlassPanel;
import com.harunaga.games.tetris.client.form.LoginBox;
import com.harunaga.games.tetris.client.form.Menu;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Harunaga
 */
public class MenuGameState extends TetrisGameState implements ActionListener {

    public static final String HELP = "_help";
    private Menu menu;
//    private int startY;
    private FallingEffect effect;
    private boolean drawLogo;
    Image tetrisLogo;
    TetrisGameConfig config;
    Container glass;

    public MenuGameState(TetrisGameConfig config) {
        this.config = config;
    }

    @Override
    public void start(InputManager inputManager, NetManager netManager) {
        super.start(inputManager, netManager);
        net.setInMultiplayerGame(false);
        drawLogo = true;
        effect = new FallingEffect(0, menu.getHeight() - tetrisLogo.getHeight(null), 60 + frame.getWidth() >> 1);
        config.startSoundAndMusic();
        config.music(TetrisGameConfig.MUSIC_MENU, false);
//        System.out.println("Menu size: " + menu.getHeight());
    }

    public String getName() {
        return STATE_GAME_MENU;
    }

    @Override
    public void loadResources(ResourceManager resourceManager) {
        super.loadResources(resourceManager);
        tetrisLogo = resourceManager.getImage("logo.png");
        glass = (Container) frame.getGlassPane();
        // glass.add(config.getLoginBox());
    }

    void createGUI(ResourceManager resourceManager) {
        menu = new Menu(new ImageIcon(resourceManager.getImage("menu/start_menu.png")),
                new ImageIcon(resourceManager.getImage("menu/start_menu.png")), this);
        menu.setHorizontalSlice(80);
        menu.setFont(TetrisGameConfig.FONT_MENU_48);
        menu.addItem("NEWGAME", STATE_SPLASH);
        menu.addItem("MULTIPLAYER", STATE_LOGEDIN);
        menu.addItem("OPTION", STATE_OPTION);
        menu.addItem("HELP", HELP);
        menu.addItem("EXIT", GameStateManager.EXIT_GAME);
        menu.setSize(menu.getPreferredSize());
        menu.setLocation(10, 10);
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(null);
        contentPane.add(menu);
        //frame.validate();
    }

    @Override
    public void stop() {
        super.stop();
        config.stopSoundAndMusic();
        menu = null;
        effect = null;
    }

    public void update(long elapsedTime) {
        if (drawLogo) {
            drawLogo = effect.isProcessing(elapsedTime);
        }
        if (TetrisGameConfig.EXIT.isPressed()) {
            if (JOptionPane.showConfirmDialog(frame, "Are you sure ?", "Quit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                nextState = GameStateManager.EXIT_GAME;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        effect.draw(tetrisLogo, g);
        if (timeToDisplay > 0) {
            drawMessage(g, frame.getWidth() - 50, 50, MESSAGE_RIGHT);
        }
        if (glass.isVisible()) {
            glass.paint(g);
        }
//        g.setColor(Color.red);
//        Font.getFont(TetrisGameConfig.FONT_MENU_60).drawString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", 10, 10, g);
        //g.rotate(Math.PI * 45 / 180, frame.getWidth() >> 1, frame.getHeight() >> 1);
    }

    private void login() {
        glass.add(config.getLoginBox());
        config.getLoginBox().setListener(this);
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                frame.validate();
                glass.setVisible(true);
            }
        });
    }

    /**
     *
     * @param e
     */
    @Override
    public void processEvent(GameEvent e) {
        switch (e.getType()) {
            case TetrisEvent.S_LOGIN_ACK_OK:
                timer.cancel();
                glass.setVisible(false);
                net.setLogin(true);
                nextState = STATE_LOGEDIN;
                log.info("get log in ack ok. next state : " + nextState);
                return;
            case TetrisEvent.S_LOGIN_ACK_FAIL:
                timer.cancel();
                glass.setVisible(false);
                JOptionPane.showMessageDialog(frame, "Log in fail. reason:" + e.getMessage(),
                        "Log in respon", JOptionPane.INFORMATION_MESSAGE);
                return;
            case TetrisEvent.S_DISCONNECT:
                net.setLogin(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof LoginBox) {
            final LoginBox login = (LoginBox) src;
//            login.setVisible(false);
            if (LoginBox.LOG_IN.equals(e.getActionCommand())) {
                new Thread() {

                    @Override
                    public void run() {
                        if (net == null) {
                            glass.setVisible(false);
                            log.error("Null net manager");
                        } else {
                            net.shutdown();
                            String ip = login.getIP();
                            if (net.init(ip)) {
                                net.setPassword(login.getPassword());
                                net.setUsername(login.getUsername());
                                net.start();
                                displayMessage("logging in. please be patient", -1);
                                timer.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        glass.setVisible(false);
                                        JOptionPane.showMessageDialog(frame,
                                                "Log in fail. reason: no respone from server",
                                                "Log in respone", JOptionPane.ERROR_MESSAGE);

                                    }
                                }, TIME_FOR_WAIT_LOGIN_ACK);
                                net.login();
                            } else {
                                glass.setVisible(false);
                                JOptionPane.showMessageDialog(frame,
                                        "Can not connect to server: " + ip,
                                        "Log in respone", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }.start();
            } else {
                glass.setVisible(false);
            }
            glass.remove(config.getLoginBox());
            frame.requestFocus();
        } else if (src instanceof Menu) {
            String key = e.getActionCommand();
            if (STATE_SPLASH.equals(key)) {
                net.setInMultiplayerGame(false);
                nextState = STATE_SPLASH;
            } else if (STATE_LOGEDIN.equals(key)) {
                if (net.isLogedin()) {
                    nextState = STATE_LOGEDIN;
                } else {
                    login();
                }
            } else if (GameStateManager.EXIT_GAME.equals(key)) {
                TetrisGameConfig.EXIT.tap();
            } else if (HELP.equals(key)) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        try {
                            File file = new File(".");
                            String path = file.getAbsolutePath();
                            path = path.substring(0, path.length() - 1);
                            System.out.println(path);
                            path = path + "HuongDan.chm";
                            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path);
//                            Runtime.getRuntime().exec("HuongDan.chm");
                        } catch (IOException ex) {
                            log.error("Can not open file", ex);
                        }
                    }
                });
            } else if (STATE_OPTION.equals(key)) {
                nextState = STATE_OPTION;
            }
        } else {
            log.warn("wrong provider for the waiting input");
        }
    }
}
