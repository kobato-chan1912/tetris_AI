package com.harunaga.games.common.client.state;

import com.harunaga.games.common.client.font.Font;
import com.harunaga.games.common.client.input.InputManager;
import com.harunaga.games.common.client.effect.Transition;
import com.harunaga.games.common.client.net.NetManager;
import com.harunaga.games.common.client.net.GameEventListener;
import com.harunaga.games.common.client.resource.ResourceManager;
import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.JFrame;
import org.apache.log4j.Logger;

public class GameStateManager {

    Logger log = Logger.getLogger(GameStateManager.class);
    public static final String EXIT_GAME = "_ExitGame";
    private Map<String, GameState> gameStates;
    private Image defaultImage;
    private GameState currentState;
    private InputManager inputManager;
    private ResourceManager resource;
    private NetManager net;
    private boolean done;
    private Font font;
    JFrame frame;
    private BufferedImage oldState;
    private boolean transitioning;
    Transition transition;
    int firstSleep = 0;
    private String str;
    int x = 100;

    public GameStateManager(ResourceManager resourceManager, InputManager inputManager,
            Image defaultImage, NetManager netManager, JFrame frame) {
        this.resource = resourceManager;
        this.inputManager = inputManager;
        this.defaultImage = defaultImage;
        this.frame = frame;
        this.net = netManager;
        gameStates = new HashMap<String, GameState>();
        font = Font.getFont("System", Font.STYLE_BOLD, 48);
        transitioning = false;
        setString("Loading...please be patien");
    }

    public void setFont(String fontName) {
        Font.loadFont(fontName);
        font = Font.getFont(fontName);
        x = (frame.getWidth() - font.stringWidth(str)) >> 1;
    }

    public final void setString(String string) {
        str = string;
        x = (frame.getWidth() - font.stringWidth(str)) >> 1;
    }

    public void loadAllResources(ResourceManager resourceManager) {
        Iterator i = getStates();
        while (i.hasNext()) {
            GameState gameState = (GameState) i.next();
            gameState.loadResources(resourceManager);
        }
    }

    public void addState(GameState state) {
        gameStates.put(state.getName(), state);
    }

    public Iterator getStates() {
        return gameStates.values().iterator();
    }

    public boolean isDone() {
        return done;
    }

    /**
    Sets the current state (by name).
     */
    public void setState(final String name) {
        if (EXIT_GAME.equals(name)) {
            setString("Bye bye");
            net.shutdown();
            done = true;
            if (currentState != null) {
                currentState.stop();
            }
            currentState = null;
        } else {
            // set new state
            GameState previous = currentState;
            currentState = gameStates.get(name);
            if (currentState instanceof GameEventListener) {
                net.setListener((GameEventListener) currentState);
            }
            if (!net.isConnect()) {
                net.setLogin(false);
            }
            if (currentState != null) {
                transition = currentState.getEffect();
                if (transition != null) {
                    if (previous != null) {
                        oldState = resource.getCompatibleImage(frame.getWidth(), frame.getHeight(), Transparency.TRANSLUCENT);
                        Graphics2D g = oldState.createGraphics();
                        previous.draw(g);
                        previous.stop();
                        g.dispose();
                    } else {
                        oldState = (BufferedImage) defaultImage;
                    }
                    transitioning = true;
                }
                currentState.start(inputManager, net);
            } else {
                log.error("nextstate null");
            }
        }
    }

    /**
    Updates world, handles input.
     */
    public void update(long elapsedTime) {
        if (transitioning) {
            transitioning = transition.isProcessing(elapsedTime);
            if (!transitioning) {
                transition = null;
                inputManager.resetAllGameActions();
            }
            return;
        }
        // if no state, pause a short time
        if (currentState == null) {
            try {
                Thread.sleep(1 << firstSleep);
                firstSleep += 5;
            } catch (InterruptedException ex) {
            }
        } else {
            String nextState = currentState.checkForStateChange();
            if (nextState != null) {
                net.setListener(null);
                setState(nextState);
            } else {
                currentState.update(elapsedTime);
            }
        }
    }

    /**
    Draws to the screen.
     */
    public void draw(Graphics2D g) {
        if (currentState != null) {
            if (transitioning) {
                transition.begin(oldState, g);
            }
            currentState.draw(g);
            if (transitioning) {
                transition.end(oldState, g);
            }
        } else {
            // if no state, begin the default image to the screen
            g.setColor(Color.RED);
            g.drawImage(defaultImage, 0, 0, null);
            font.drawString(str, x, frame.getHeight() >> 1, g);
        }
    }
}
