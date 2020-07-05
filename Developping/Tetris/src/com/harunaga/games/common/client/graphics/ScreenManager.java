package com.harunaga.games.common.client.graphics;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

enum ScreenMode {

    FULL_SCREEN, NOMAL
}

/**
The ScreenManager class manages initializing and displaying
full screen graphics modes.
 */
public class ScreenManager {

    // public int firstWith, firstheight;
    ScreenMode mode;
    private GraphicsDevice device;
    public static final DisplayMode POSSIBLE_MODES[] = {
        new DisplayMode(1280, 1024, 32, 0),
        new DisplayMode(1024, 768, 32, 0),
        new DisplayMode(1280, 960, 32, 0),
        new DisplayMode(1366, 768, 32, 0),
        new DisplayMode(640, 480, 32, 0),
        new DisplayMode(640, 480, 24, 0),
        new DisplayMode(1366, 768, 24, 0),
        new DisplayMode(1024, 768, 32, 0),
        new DisplayMode(1024, 768, 24, 0),};
    private JFrame drawArea = null;

    /**
    Creates a new ScreenManager object.
     */
    public ScreenManager(JFrame frame) {
        GraphicsEnvironment environment =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = environment.getDefaultScreenDevice();
        this.drawArea = frame;
    }

    /**
    Returns a list of compatible display modes for the
    default device on the system.
     */
    public DisplayMode[] getCompatibleDisplayModes() {
        return device.getDisplayModes();
    }

    /**
    Returns the first compatible mode in a list of modes.
    Returns null if no modes are compatible.
     */
    public DisplayMode findFirstCompatibleMode(DisplayMode modes[]) {
        DisplayMode goodModes[] = device.getDisplayModes();
        for (int i = 0; i < modes.length; i++) {
            for (int j = 0; j < goodModes.length; j++) {
                if (displayModesMatch(modes[i], goodModes[j])) {
                    return modes[i];
                }
            }

        }
        return null;
    }

    /**
    Returns the current display mode.
     */
    public DisplayMode getCurrentDisplayMode() {
        return device.getDisplayMode();
    }

    /**
    Determines if two display modes "match". Two display
    modes match if they have the same resolution, bit depth,
    and refresh rate. The bit depth is ignored if one of the
    modes has a bit depth of DisplayMode.BIT_DEPTH_MULTI.
    Likewise, the refresh rate is ignored if one of the
    modes has a refresh rate of
    DisplayMode.REFRESH_RATE_UNKNOWN.
     */
    public boolean displayModesMatch(DisplayMode mode1, DisplayMode mode2) {
        if (mode1.getWidth() != mode2.getWidth()
                || mode1.getHeight() != mode2.getHeight()) {
            return false;
        }

        if (mode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
                && mode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
                && mode1.getBitDepth() != mode2.getBitDepth()) {
            return false;
        }

        if (mode1.getRefreshRate()
                != DisplayMode.REFRESH_RATE_UNKNOWN
                && mode2.getRefreshRate()
                != DisplayMode.REFRESH_RATE_UNKNOWN
                && mode1.getRefreshRate() != mode2.getRefreshRate()) {
            return false;
        }

        return true;
    }

    /**
    Enters full screen mode and changes the display mode.
    If the specified display mode is null or not compatible
    with this device, or if the display mode cannot be
    changed on this system, the current display mode is used.
    <p>
    The display uses a BufferStrategy with 2 buffers.
     */
    public void init() {
//        this.mode = mode;
//        canvas.setVisible(false);
//        canvas.setIgnoreRepaint(true);
//        // frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
//        if (mode == ScreenMode.FULL_SCREEN) {
//            DisplayMode displayMode = findFirstCompatibleMode(POSSIBLE_MODES);
//            device.setFullScreenWindow(canvas);
//            if (displayMode != null && device.isDisplayChangeSupported()) {
//                try {
//                    device.setDisplayMode(displayMode);
//                } catch (IllegalArgumentException ex) {
//                }
//                // fix for mac os x
//                window.setSize(displayMode.getWidth(),
//                        displayMode.getHeight());
//            }
//        } else {
//            device.setFullScreenWindow(null);
//        }
        drawArea.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // avoid potential deadlock in 1.4.1_02
        try {
            EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                    drawArea.createBufferStrategy(2);
                    //System.out.println("Create BufferStrategy");
                }
            });
        } catch (InterruptedException ex) {
            // ignore
        } catch (InvocationTargetException ex) {
            // ignore
        }
    }

//    public void setFullScreen() {
//        setScreen(ScreenMode.FULL_SCREEN);
//    }
//
//    public void setNomalScreen(int width, int height) {
//        setScreen(ScreenMode.NOMAL);
//        window.setSize(width, height);
//    }
    /**
    Gets the graphics context for the display. The
    ScreenManager uses double buffering, so applications must
    call update() to show any graphics drawn.
    <p>
    The application must dispose of the graphics object.
     */
    public Graphics2D getGraphics() {
        if (drawArea != null) {
            BufferStrategy strategy = drawArea.getBufferStrategy();
            return (Graphics2D) strategy.getDrawGraphics();
        } else {
            return null;
        }
    }

    /**
    Updates the display.
     */
    public void update() {
        if (drawArea != null) {
            BufferStrategy strategy = drawArea.getBufferStrategy();
            if (!strategy.contentsLost()) {
                strategy.show();
            }
        }
        // Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
    }

    /**
    Returns the window currently used in full screen mode.
    Returns null if the device is not in full screen mode.
     */
    public JFrame getScreen() {
        return drawArea;
    }

    /**
    Returns the width of the window currently used in full
    screen mode. Returns 0 if the device is not in full
    screen mode.
     */
    public int getWidth() {
        if (drawArea != null) {
            return drawArea.getWidth();
        } else {
            return 0;
        }
    }

    /**
    Returns the height of the window currently used in full
    screen mode. Returns 0 if the device is not in full
    screen mode.
     */
    public int getHeight() {
        if (drawArea != null) {
            return drawArea.getHeight();
        } else {
            return 0;
        }
    }

    /**
    Restores the screen's display mode.
     */
    public void restoreScreen() {
        device.setFullScreenWindow(null);
    }

    /**
    Creates an image compatible with the current display.
     */
    public BufferedImage createCompatibleImage(int w, int h,
            int transparancy) {
        if (drawArea != null) {
            GraphicsConfiguration gc =
                    drawArea.getGraphicsConfiguration();
            return gc.createCompatibleImage(w, h, transparancy);
        }
        return null;
    }

    /** check if screen is FullScrean mode
    
     */
    public boolean isFullScreen() {
        return mode == ScreenMode.FULL_SCREEN;
    }
}
