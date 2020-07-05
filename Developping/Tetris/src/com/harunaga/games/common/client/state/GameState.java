package com.harunaga.games.common.client.state;

import com.harunaga.games.common.client.input.InputManager;
import com.harunaga.games.common.client.effect.Transition;
import com.harunaga.games.common.client.net.NetManager;
import com.harunaga.games.common.client.resource.ResourceManager;
import java.awt.Graphics2D;
import javax.swing.JFrame;

public interface GameState {

    /**
    Gets the name of this state. Used for
    the checkForStateChange() method.
     */
    public String getName();

    /**
    Returns the name of a state to change to if this state is
    ready to change to another state, or null otherwise.
     */
    public String checkForStateChange();

    /**
    Loads any resources for this state. This method is called
    in a background thread before any GameStates are set.
     */
    public void loadResources(ResourceManager resourceManager);

//    /**
//     * release all temporally unused resource
//     */
//    public void releaseResources();
    /**
    Initializes this state and sets up the input manager
     */
    public void start(InputManager inputManager, NetManager netManager);

    /**
    Performs any actions needed to stop this state.
     */
    public void stop();

    /**
    Updates world, handles input.
     */
    public void update(long elapsedTime);

    /**
    Draws to the screen.
     */
    public void draw(Graphics2D g);

    public Transition getEffect();
}
