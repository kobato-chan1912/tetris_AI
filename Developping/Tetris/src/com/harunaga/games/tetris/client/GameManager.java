package com.harunaga.games.tetris.client;

import com.harunaga.games.common.client.gamecore.GameCore;
import com.harunaga.games.common.client.graphics.NullRepaintManager;
import com.harunaga.games.common.client.graphics.ScreenManager;
import com.harunaga.games.common.client.input.InputManager;
import com.harunaga.games.common.client.miscellaneous.GlassPanel;
import com.harunaga.games.common.client.net.NetManager;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.common.client.sound.MidiPlayer;
import com.harunaga.games.common.client.sound.SoundManager;
import com.harunaga.games.common.client.state.GameStateManager;
import com.harunaga.games.common.client.util.TimeSmoothie;
import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.tetris.client.net.TetrisClient;
import com.harunaga.games.tetris.client.states.*;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.sound.sampled.AudioFormat;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
GameManager manages all parts of the game.
 */
public class GameManager extends GameCore {

    static final Logger log = Logger.getLogger(GameManager.class);
    public static JFrame frame;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        new GameManager(new ScreenManager(new JFrame("Tetris"))).run();
    }

    public GameManager(ScreenManager screen) {
        super(screen);
        frame = screen.getScreen();
        frame.setSize(TetrisGameConfig.BACKGROUND_WIDTH, TetrisGameConfig.BACKGROUND_HEIGHT);
        //frame.pack();
        //frame.setUndecorated(true);
        //frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
//        frame.setIconImage(null);
        //frame.setDefaultLookAndFeelDecorated(false);
        //System.out.println("FrameWidth=" + frame.getWidth() + " FrameHeight=" + frame.getHeight());
        frame.setLocation(10, 10);
        frame.setResizable(false);
        frame.setIgnoreRepaint(true);
        frame.setVisible(true);
//        System.out.println("FrameWidth=" + frame.getWidth() + " FrameHeight=" + frame.getHeight());
        Insets inset = frame.getInsets();
        frame.setSize(frame.getWidth() + inset.left + inset.right, frame.getHeight() + inset.top + inset.bottom);
        //frame.validate();
////          frame.setVisible(true);
        //System.out.println("FrameWidth=" + frame.getWidth() + " FrameHeight=" + frame.getHeight());
    }
    //static final Logger log = Logger.getLogger("com.harunaga");
    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
            new AudioFormat(44100, 8, 1, true, false);
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private InputManager inputManager;
    private GameStateManager gameStateManager;
    private TimeSmoothie timeSmoothie = new TimeSmoothie();
//    private Configurator config = new Configurator();
//    public static final GameAction changeMode = new GameAction("changeMode",
//            GameAction.DETECT_INITAL_PRESS_ONLY);
//

    @Override
    public void init() {
        //------------debug
//        Toolkit.getDefaultToolkit().addAWTEventListener(
//                new AWTEventListener() {
//
//                    public void eventDispatched(AWTEvent event) {
//                        System.out.println(event);
//                    }
//                }, -1);
//---------------end

        NullRepaintManager.install();
        //log.setLevel(Level.INFO);

        //log.info("init sound manager");
        soundManager = new SoundManager(PLAYBACK_FORMAT, 8);

        //log.info("init midi player");
        midiPlayer = new MidiPlayer();


        //log.info("init gamecore");
        super.init();

        //log.info("init input manager");
        inputManager = new InputManager(screen.getScreen());

        resourceManager = new TetrisResourceManager(screen.getScreen().getGraphicsConfiguration(), soundManager, midiPlayer);
        //log.info("init resource manager");
//        resourceManager = new TileGameResourceManager(
//                screen.getScreenWindow().getGraphicsConfiguration(),
//                soundManager, midiPlayer);
        frame.setIconImage(resourceManager.getImage("icon.png"));
        //frame.getRootPane().setDefaultButton(new JButton("HA"));
        NetManager net = new TetrisClient();
        //log.info("init game states");
        gameStateManager = new GameStateManager(resourceManager, inputManager,
                resourceManager.getImage(TetrisGameConfig.DEFAULT_BG), net, frame);
        //frame.setOpacity(0.5f);
        // load resources (in separate thread)
        new Thread() {

            @Override
            public void run() {
                Container contentPane = frame.getContentPane();
                // make sure the content pane is transparent
                if (contentPane instanceof JComponent) {
                    ((JComponent) contentPane).setOpaque(false);
                }
                TetrisGameConfig config = new TetrisGameConfig(soundManager, midiPlayer, inputManager);
                config.getConfig(resourceManager);
                frame.setGlassPane(new GlassPanel());
                gameStateManager.addState(new PlayingGameState(config));
                gameStateManager.addState(new MenuGameState(config));
                gameStateManager.addState(new SplashGameState());
                gameStateManager.addState(new LogedInGameState());
                gameStateManager.addState(new CollectingPlayerState());
                gameStateManager.addState(new ResultState());
                gameStateManager.addState(new OptionState());
                log.info("loading resources");
                gameStateManager.loadAllResources(resourceManager);
                log.info("setting to Splash state");
                gameStateManager.setState(TetrisGameState.STATE_GAME_MENU);
                // gameStateManager.setState(TetrisGameState.STATE_RESULT);
                //gameStateManager.setState(TetrisGameState.STATE_PLAYING);
                //gameStateManager.setState(TetrisGameState.STATE_OPTION);
                //gameStateManager.setState(TetrisGameState.STATE_LOGEDIN);
                // gameStateManager.setState(TetrisGameState.STATE_COLLECTING_PLAYERS);
            }
        }.start();
    }

    /**
    Closes any resurces used by the GameManager.
     */
    @Override
    public void stop() {
        //log.info("stopping game");
        super.stop();
        //log.info("closing midi player");
        midiPlayer.close();
        //log.info("closing sound manager");
        soundManager.close();
        //config.saveConfigButton(main, inputManager);
    }

    @Override
    public void update(long elapsedTime) {
//        if (changeMode.isPressed()) {
//            if (screen.isFullScreen()) {
//                screen.setNomalScreen();
//            } else {
//                screen.setFullScreen();
//            }
//        }

        if (gameStateManager.isDone()) {
            stop();
        } else {
            elapsedTime = timeSmoothie.getTime(elapsedTime);
            //gameStateManager.updateFrame(screen.getScreenWindow());
            gameStateManager.update(elapsedTime);
        }
    }

    public void draw(Graphics2D g) {
        gameStateManager.draw(g);
    }
}
