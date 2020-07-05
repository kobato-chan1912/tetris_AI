/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris;

import com.harunaga.games.common.client.font.Font;
import com.harunaga.games.common.client.input.GameAction;
import com.harunaga.games.common.client.input.InputManager;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.common.client.sound.MidiPlayer;
import com.harunaga.games.common.client.sound.Sound;
import com.harunaga.games.common.client.sound.SoundManager;
import com.harunaga.games.tetris.client.form.LoginBox;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.sound.midi.Sequence;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Harunaga
 */
public class TetrisGameConfig {

    static Logger log = Logger.getLogger(TetrisGameConfig.class);
//    public static TetrisGameConfig instance = new TetrisGameConfig();
    public static final Color[] PLAYER_COLORS = {Color.RED, Color.BLUE, Color.YELLOW,
        Color.ORANGE, Color.GRAY, Color.WHITE};
    public static final char TOPSCORE_DELIMITER = '^';
    public static final char TOPSCORE_ATTRIBUTE_DELIMITER = '&';
    public static final char GAME_DELIMITER = '^';
    public static final char GAME_ATTRIBUTE_DELIMITER = '&';
    public static final char PLAYER_DELIMITER = '^';
    public static final char PLAYER_ATTRIBUTE_DELIMITER = '$';
    public static final String GAME_NAME = "Tetris";
    public static final int resultPaneX = 30;
    public static final int resultPaneY = 30;
    public static final String SMALL_ON_FOCUS_IMG = "Smallfocus.png";
    public static final String BIG_ON_FOCUS_IMG = "Bigfocus.png";
    public static final String DEFAULT_BG = "splash.jpg";
    public static final String GAME_PLAYING_BG = "playing_bg.jpg";
    public static final int PLAYER_LIMIT = PLAYER_COLORS.length;
    public static final int BACKGROUND_WIDTH = 1000;
    public static final int BACKGROUND_HEIGHT = 700;
    public static final int PIECE_SIZE = 22;
    public static final int BOARD_ROWS = 25;
    public static final int BOARD_COLUMNS = 15;
    public static final int[] SCORE_BREAKPOINTS = new int[]{1000, 3000, 8000, 25000, 60000};
    public static final int SPROUT_UP_AMOUNT = 1;
    //-------------------------------Font-----------------------------
    public static final String FONT_NUMBER_20 = "numbers_plain_20";
    public static final String FONT_MENU_48 = "menu_plain_48";
    public static final String FONT_MENU_60 = "menu_plain_60";
    public static final String FONT_BUTTON_24 = "button_plain_24";
    //-------------------------------Key---------------------------------
    public static final GameAction MOVE_LEFT = new GameAction("moveLeft");
    public static final GameAction MOVE_RIGHT = new GameAction("moveRight");
    public static final GameAction MOVE_DOWN = new GameAction("moveDown");
    public static final GameAction ROTATE = new GameAction("rotate");
    public static final GameAction CHAT = new GameAction("sendmessage", GameAction.DETECT_INITAL_PRESS_ONLY);
    public static final GameAction SHIFT = new GameAction("shift", GameAction.DETECT_INITAL_PRESS_ONLY);
    public static final GameAction PAUSE = new GameAction("pause", GameAction.DETECT_INITAL_PRESS_ONLY);
    public static final GameAction EXIT = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
    //---------------------------------Sound and music-------------------
    public static int SOUND_MOVE = 0;
    public static int SOUND_ROTATE = 1;
    public static int SOUND_FALL = 2;
    public static int SOUND_GETSCORE = 3;
    public static int SOUND_GAME_OVER = 4;
    public static int SOUND_TIME_UP = 5;
    public static int MUSIC_MENU = 0;
    public static int MUSIC_PLAYING = 1;
    Sound sound[] = new Sound[100];
    private Sequence[] music = new Sequence[100];
    public static boolean soundPreferred = false;
    public static boolean musicPrederred = true;
    private SoundManager soundManager;
    private MidiPlayer midiPlayer;
    private InputManager input;
    private LoginBox login;

    public TetrisGameConfig(SoundManager soundManager, MidiPlayer midiPlayer, InputManager input) {
        this.soundManager = soundManager;
        this.midiPlayer = midiPlayer;
        this.input = input;
    }

    private void getAllSound(ResourceManager resource) {
        sound[SOUND_FALL] = resource.getSound("sounds/fall.wav");
        sound[SOUND_MOVE] = resource.getSound("sounds/move.wav");
        sound[SOUND_ROTATE] = resource.getSound("sounds/rotate.wav");
        sound[SOUND_GETSCORE] = resource.getSound("sounds/bump.wav");
        sound[SOUND_TIME_UP] = resource.getSound("sounds/timeup.wav");
        sound[SOUND_GAME_OVER] = resource.getSound("sounds/gameover.wav");
    }

    private void getAllSequence(ResourceManager resource) {
        music[MUSIC_MENU] = resource.getSequence("musics/menu.mid");
        music[MUSIC_PLAYING] = resource.getSequence("musics/playing.mid");
    }

    public void getConfig(ResourceManager resource) {
        getAllSound(resource);
        getAllSequence(resource);
        configButton(input);
        loadAllFont();
        login = new LoginBox(null, new ImageIcon(resource.getImage("buttons/button.png")),
                new ImageIcon(resource.getImage("buttons/button_rollover.png")));
        login.setLocation(300, 300);
    }

    public LoginBox getLoginBox() {
        return login;
    }

//    public static void soundPreferred(boolean flag) {
//        soundPreferred = flag;
//    }
//
//    public static boolean isMusicPrederred() {
//        return musicPrederred;
//    }
//
//    public static boolean isSoundPreferred() {
//        return soundPreferred;
//    }
//
//    public static void musicPreferred(boolean flag) {
//        musicPrederred = flag;
//    }
    public void stopSoundAndMusic() {
        soundManager.setPaused(true);
        midiPlayer.setPaused(true);
    }

    public void startSoundAndMusic() {
        soundManager.setPaused(!soundPreferred);
        midiPlayer.setPaused(!musicPrederred);
    }

    public void music(int flag, boolean loop) {
        if (musicPrederred) {
            midiPlayer.play(music[flag], loop);
        }
    }

    public void sound(int flag) {
        if (soundPreferred) {
            soundManager.play(sound[flag]);
        }
    }

    void loadAllFont() {
        Font.loadFont(TetrisGameConfig.FONT_BUTTON_24);
        Font.loadFont(TetrisGameConfig.FONT_NUMBER_20);
        Font.loadFont(TetrisGameConfig.FONT_MENU_48);
        Font.loadFont(TetrisGameConfig.FONT_MENU_60);
    }

    public static void mapKeyDefault(InputManager input) {
        input.clearAllMaps();
        input.mapToKey(MOVE_LEFT, KeyEvent.VK_LEFT);
        input.mapToKey(MOVE_RIGHT, KeyEvent.VK_RIGHT);
        input.mapToKey(MOVE_DOWN, KeyEvent.VK_DOWN);
        input.mapToKey(ROTATE, KeyEvent.VK_UP);
        input.mapToKey(SHIFT, KeyEvent.VK_SPACE);
        input.mapToKey(EXIT, KeyEvent.VK_ESCAPE);
        input.mapToKey(CHAT, KeyEvent.VK_ENTER);
        input.mapToKey(PAUSE, KeyEvent.VK_1);
    }
    static private final String DEFAULT_CONFIG_FILE = "config/buttonConfig.txt";

    public static void configButton(InputManager inputmanager) {
        GameAction[] action = new GameAction[]{TetrisGameConfig.MOVE_LEFT, TetrisGameConfig.MOVE_DOWN,
            TetrisGameConfig.MOVE_RIGHT, TetrisGameConfig.ROTATE, TetrisGameConfig.SHIFT,
            TetrisGameConfig.PAUSE, TetrisGameConfig.EXIT, TetrisGameConfig.CHAT
        };
        BufferedReader reader = null;
        try {
            File file = new File(DEFAULT_CONFIG_FILE);
            if (!file.exists()) {
                log.info("cound not found the file : " + DEFAULT_CONFIG_FILE);
                TetrisGameConfig.mapKeyDefault(inputmanager);
                return;
            }
            reader = new BufferedReader(new FileReader(file));
            musicPrederred = Boolean.parseBoolean(reader.readLine());
            soundPreferred = Boolean.parseBoolean(reader.readLine());
//            System.out.println("Music: " + musicPrederred);
//            System.out.println("Sound: " + soundPreferred);
            for (int i = 0; i < action.length; i++) {
                Scanner scanner = new Scanner(reader.readLine());
                while (scanner.hasNextInt()) {
                    int key = scanner.nextInt();
                    int isKeyMap = scanner.nextInt();
                    if (isKeyMap == 1) {
                        inputmanager.mapToKey(action[i], key);
                    } else {
                        inputmanager.mapToMouse(action[i], key);
                    }
                }
            }
        } catch (Exception ex) {
            TetrisGameConfig.mapKeyDefault(inputmanager);
            log.error("read file error");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    public static boolean saveConfigButton(InputManager inputmanager) {
        GameAction[] action = new GameAction[]{TetrisGameConfig.MOVE_LEFT, TetrisGameConfig.MOVE_DOWN,
            TetrisGameConfig.MOVE_RIGHT, TetrisGameConfig.ROTATE, TetrisGameConfig.SHIFT,
            TetrisGameConfig.PAUSE, TetrisGameConfig.EXIT, TetrisGameConfig.CHAT
        };
        BufferedWriter writer = null;
        try {
            File file = new File(DEFAULT_CONFIG_FILE);
            if (file.getParent() != null) {
                File folder = new File(file.getParent());
                if (!folder.exists() && !folder.mkdirs()) {
                    JOptionPane.showMessageDialog(null, "Cannot save your configuration", "Errer",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(musicPrederred + "");
            writer.newLine();
            writer.write(soundPreferred + "");
            writer.newLine();
            for (int i = 0; i < action.length; i++) {
                List keys = inputmanager.getKeyMaps(action[i]);
                Iterator k = keys.iterator();
                while (k.hasNext()) {
                    String s = k.next().toString();
                    writer.write(s + " 1 ");
                }
                keys = inputmanager.getMouseMaps(action[i]);
                k = keys.iterator();
                while (k.hasNext()) {
                    String s = k.next().toString();
                    writer.write(s + " 0 ");
                }
                writer.newLine();
            }
            return true;
        } catch (IOException ex) {
            log.error("cannot write to file", ex);
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                //Logger.getLogger(Configurator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
