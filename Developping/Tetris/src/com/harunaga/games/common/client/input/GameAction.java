package com.harunaga.games.common.client.input;

/**
The GameAction class is an abstract to a user-initiated
action, like jumping or moving. GameActions can be mapped
to keys or the mouse with the InputManager.
 */
public class GameAction {

    long time = 0;
    private boolean isFirstTime = true;
    private long startTime = 0;
    public static final long TIME_FOR_REPEATED = 120;
    /**
    Normal behavior. The isPressed() method returns true
    as long as the key is held down.
     */
    public static final int NORMAL = 0;
    /**
    Initial press behavior. The isPressed() method returns
    true only after the key is first pressed, and not again
    until the key is released and pressed again.
     */
    public static final int DETECT_INITAL_PRESS_ONLY = 1;
    //state
    private static final int STATE_RELEASED = 0;
    private static final int STATE_PRESSED = 1;
    private static final int STATE_WAITING_FOR_RELEASE = 2;
    private String name;
    private int behavior;
    private int amount;
    private int state;

    /**
    Create a new GameAction with the NORMAL behavior.
     */
    public GameAction(String name) {
        this(name, NORMAL);
    }

    /**
    Create a new GameAction with the specified behavior.
     */
    public GameAction(String name, int behavior) {
        this.name = name;
        this.behavior = behavior;
        reset();
    }

    /**
    Gets the name of this GameAction.
     */
    public String getName() {
        return name;
    }

    /**
    Resets this GameAction so that it appears like it hasn't
    been pressed.
     */
    public void reset() {
        isFirstTime = true;
        state = STATE_RELEASED;
        amount = 0;
    }

    /**
    Taps this GameAction. Same as calling press() followed
    by release().
     */
    public void tap() {
        press();
        release();
    }

    /**
    Signals that the key was pressed.
     */
    public void press() {
        press(1);
//        long old = System.currentTimeMillis();
//        System.out.println("------------------Key pressed : " + (old - time));
//        time = old;
    }

    /**
    Signals that the key was pressed a specified number of
    times, or that the mouse move a spcified distance.
     */
    public void press(int amount) {
        if (state != STATE_WAITING_FOR_RELEASE) {
            if (isFirstTime) {
                startTime = System.currentTimeMillis();
            }
            this.amount += amount;
            state = STATE_PRESSED;
        }
    }

    /**
    Signals that the key was released
     */
    public void release() {
        state = STATE_RELEASED;
//        long old = System.currentTimeMillis();
//        System.out.println(">>>>>>>>>>>>>>>>>>Key release : " + (old - time));
//        time = old;
    }

    /**
    Returns whether the key was pressed or not since last
    checked.
     */
    public boolean isPressed() {
        return (getAmount() != 0);
    }

    /**
    For keys, this is the number of times the key was
    pressed since it was last checked.
    For mouse movement, this is the distance moved.
     */
    public int getAmount() {
        int retVal = amount;
        if (retVal != 0) {
            if (state == STATE_RELEASED) {
                amount = 0;
                if (!isFirstTime) {
                    retVal = 0;
                }
                isFirstTime = true;
            } else if (behavior == DETECT_INITAL_PRESS_ONLY) {
                amount = 0;
                isFirstTime = true;
                state = STATE_WAITING_FOR_RELEASE;
            } else if (System.currentTimeMillis() - startTime < TIME_FOR_REPEATED) {
                if (isFirstTime) {
                    isFirstTime = false;
                } else {
//                    System.out.println("Me kiep dien qua");
                    return 0;
                }
            }
        }
        return retVal;
    }
}
