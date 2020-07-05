/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.states;

import com.harunaga.games.common.client.input.GameAction;
import com.harunaga.games.common.client.input.HTextField;
import com.harunaga.games.common.client.input.InputManager;
import com.harunaga.games.common.client.miscellaneous.SizeFixedButton;
import com.harunaga.games.common.client.net.NetManager;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.tetris.client.form.ComponentProvider;
import com.harunaga.games.tetris.client.form.HSelection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author Harunaga
 */
public class OptionState extends TetrisGameState implements ActionListener {

    private boolean keySaved = true;
    private SizeFixedButton okBt;
    private SizeFixedButton resetBt;
    private SizeFixedButton saveBt;
    private HSelection language;
    private List inputs;
    private InputManager inputManager;
    private String MUSIC = "Music";
    private String SOUND = "Sound";

    public OptionState() {
        inputs = new ArrayList();
    }

    @Override
    void createGUI(ResourceManager resourceManager) {
        ComponentProvider provider = new ComponentProvider(resource);
        // create the list of GameActions and mapped keys
        JPanel configPanel = new JPanel(new GridLayout(9, 2, 2, 2));
        addActionConfig(configPanel, TetrisGameConfig.MOVE_LEFT);
        addActionConfig(configPanel, TetrisGameConfig.MOVE_RIGHT);
        addActionConfig(configPanel, TetrisGameConfig.MOVE_DOWN);
        addActionConfig(configPanel, TetrisGameConfig.ROTATE);
        addActionConfig(configPanel, TetrisGameConfig.SHIFT);
        addActionConfig(configPanel, TetrisGameConfig.CHAT);
        addActionConfig(configPanel, TetrisGameConfig.PAUSE);
        addActionConfig(configPanel, TetrisGameConfig.EXIT);
//        addCheckBox(configPanel, SOUND);
//        addCheckBox(configPanel, MUSIC);
        //sound and music
        JPanel bottomPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JCheckBox check = provider.getDefaultCheckbox(MUSIC, this, TetrisGameConfig.musicPrederred);
        check.setHorizontalAlignment(JCheckBox.CENTER);
        bottomPane.add(check);
        check = provider.getDefaultCheckbox(SOUND, this, TetrisGameConfig.soundPreferred);
        check.setHorizontalAlignment(JCheckBox.CENTER);
        bottomPane.add(check);
        //-------------------------

        okBt = provider.getDefaultButton("Ok", this);
        saveBt = provider.getDefaultButton("Save", this);
        resetBt = provider.getDefaultButton("Reset", this);
        // create the panel containing the button
        JPanel controlPanel = new JPanel(new FlowLayout());
        //create buttons and add to panel
        controlPanel.add(okBt);
        controlPanel.add(saveBt);
        controlPanel.add(resetBt);
        controlPanel.setSize(controlPanel.getPreferredSize());
        controlPanel.setLocation((frame.getWidth() - controlPanel.getWidth()) / 2,
                (frame.getHeight() - controlPanel.getHeight()) - 100);
        controlPanel.setOpaque(false);
        // create the panel containing the instructions.
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton inc = ComponentProvider.createButton(new ImageIcon(resourceManager.getImage("buttons/increase.png")),
                new ImageIcon(resourceManager.getImage("buttons/increase_rollover.png")));
        JButton dec = ComponentProvider.createButton(new ImageIcon(resourceManager.getImage("buttons/decrease.png")),
                new ImageIcon(resourceManager.getImage("buttons/decrease_rollover.png")));
        language = new HSelection(new ImageIcon(resourceManager.getImage("selectionpanel.png")),
                dec, inc);
        language.setForeground(Color.RED);
        inc.setFocusable(false);
        dec.setFocusable(false);
        language.setTitle("Language");
        language.addOption("Vietnamese", "1");
        language.addOption("English", "2");
        topPanel.add(language);

        // create the dialog border
        Border border =
                BorderFactory.createLineBorder(Color.GRAY);

        // create the config dialog.
        JPanel dialog = new JPanel(new BorderLayout());
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(configPanel, BorderLayout.CENTER);
        dialog.add(bottomPane, BorderLayout.SOUTH);
        dialog.setBorder(border);
        dialog.setSize(dialog.getPreferredSize());
        dialog.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        dialog.setSize(400, 300);
        dialog.setLocation(
                (frame.getWidth() - dialog.getWidth()) / 2,
                (frame.getHeight() - dialog.getHeight()) / 2);
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(null);
        contentPane.add(dialog);
        contentPane.add(controlPanel);
    }

    public String getName() {
        return STATE_OPTION;
    }

    public void update(long elapsedTime) {
    }
//    private static final String INSTRUCTIONS =
//            "<html>Click an action's input box to change it's keys."
//            + "<br>An action can have at most three keys associated "
//            + "with it.<br>Press Backspace to clear an action's keys.";

    @Override
    public void start(InputManager inputManager, NetManager netManager) {
        super.start(inputManager, netManager);
        this.inputManager = inputManager;
        inputManager.setCursor(Cursor.getDefaultCursor());
        keySaved = true;
        resetInputs();
    }

    @Override
    public void stop() {
        super.stop();
        okBt = null;
        saveBt = null;
        resetBt = null;
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == okBt) {
            if (!keySaved && JOptionPane.showConfirmDialog(frame, "Do you want to save all", "SaveKey",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (TetrisGameConfig.saveConfigButton(inputManager)) {
                    JOptionPane.showMessageDialog(frame,
                            "Your option now saved", "Config Infor", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Cannot save your option. please send error to truong1990vn@yahoo.com.vn", "Config Infor", JOptionPane.ERROR_MESSAGE);
                }
            }
            nextState = STATE_GAME_MENU;
        } else if (src == saveBt) {
            if (TetrisGameConfig.saveConfigButton(inputManager)) {
                JOptionPane.showMessageDialog(frame,
                        "Your option now saved", "Config Infor", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Cannot save your option. please send error to truong1990vn@yahoo.com.vn", "Config Infor", JOptionPane.ERROR_MESSAGE);
            }
            keySaved = true;
        } else if (src == resetBt) {
            TetrisGameConfig.mapKeyDefault(inputManager);
            resetInputs();
            keySaved = false;
        } else if (src instanceof JCheckBox) {
            JCheckBox check = (JCheckBox) src;
            if (MUSIC.equals(check.getText())) {
                TetrisGameConfig.musicPrederred = check.isSelected();
            } else {
                TetrisGameConfig.soundPreferred = check.isSelected();
            }
            keySaved = false;
        }
    }

    /**
    Adds a label containing the name of the GameAction and an
    InputComponent used for changing the mapped keys.
     */
    private void addActionConfig(JPanel configPanel,
            GameAction action) {
        JLabel label = new JLabel(action.getName(), JLabel.RIGHT);
        InputComponent input = new InputComponent(action);
        configPanel.add(label);
        configPanel.add(input);
        inputs.add(input);
    }

    /**
    Resets the text displayed in each InputComponent, which
    is the names of the mapped keys.
     */
    private void resetInputs() {
        for (int i = 0; i < inputs.size(); i++) {
            ((InputComponent) inputs.get(i)).setText();
        }
    }

    /**
    The InputComponent class displays the keys mapped to a
    particular action and allows the user to change the mapped
    keys. The user selects an InputComponent by clicking it,
    then can press any key or mouse button (including the
    mouse wheel) to change the mapped value.
     */
    class InputComponent extends HTextField {

        private GameAction action;

        /**
        Creates a new InputComponent for the specified
        GameAction.
         */
        InputComponent(GameAction action) {
            this.action = action;
            enableEvents(KeyEvent.KEY_EVENT_MASK
                    | MouseEvent.MOUSE_EVENT_MASK
                    | MouseEvent.MOUSE_MOTION_EVENT_MASK
                    | MouseEvent.MOUSE_WHEEL_EVENT_MASK);
            setIgnoreRepaint(true);
        }

        /**
        Sets the displayed text of this InputComponent to the
        names of the mapped keys.
         */
        private void setText() {
            String text = "";
            List list = inputManager.getMaps(action);
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    text += (String) list.get(i) + ", ";
                }
                // remove the last comma
                text = text.substring(0, text.length() - 2);
            }
            setText(text);
        }

        /**
        Maps the GameAction for this InputComponent to the
        specified key or mouse action.
         */
        private void mapGameAction(int code, boolean isMouseMap) {
            if (inputManager.getMaps(action).size() >= 3) {
                inputManager.clearMap(action);
            }
            if (isMouseMap) {
                inputManager.mapToMouse(action, code);
            } else {
                inputManager.mapToKey(action, code);
            }
            resetInputs();
            frame.requestFocus();
        }

        // alternative way to intercept key events
        @Override
        protected void processKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                keySaved = false;
                // if backspace is pressed, clear the map
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_BACK_SPACE
                        && inputManager.getMaps(action).size() > 0) {
                    inputManager.clearMap(action);
                    setText();
                    frame.requestFocus();
                } else {
                    mapGameAction(code, false);
                    transferFocus();
                    if (!(FocusManager.getCurrentManager().getFocusOwner() instanceof InputComponent)) {
                        okBt.requestFocus();
                    }
                }

            }
            e.consume(); // ensure this key would not be process by any component
        }

        // alternative way to intercept mouse events
        @Override
        protected void processMouseEvent(MouseEvent e) {
            if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                if (hasFocus()) {
                    keySaved = false;
                    int code = InputManager.getMouseButtonCode(e);
                    mapGameAction(code, true);
                } else {
                    requestFocus();
                }
            }
            e.consume();// ensure this key would not be process by any component
        }

        // alternative way to intercept mouse events
        @Override
        protected void processMouseWheelEvent(MouseWheelEvent e) {
            if (hasFocus()) {
                keySaved = false;
                int code = InputManager.MOUSE_WHEEL_DOWN;
                if (e.getWheelRotation() < 0) {
                    code = InputManager.MOUSE_WHEEL_UP;
                }
                mapGameAction(code, true);
            }
            e.consume();// ensure this key would not be process by any component
        }
    }
//    private void addCheckBox(JPanel configPanel,
//            String text) {
////        JLabel label = new JLabel(text);
//
//        JCheckBox check = new JCheckBox(text);
////        JPanel bottomPane = new JPanel();
////        bottomPane.add(label);
////        bottomPane.add(check);
//        check.addActionListener(this);
////        configPanel.add(label);
////        configPanel.add(check);
//        //configPanel.add(bottomPane);
//        configPanel.add(check);
//    }
}
