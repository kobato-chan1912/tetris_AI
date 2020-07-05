package com.harunaga.games.common.client.graphics;

import javax.swing.RepaintManager;
import javax.swing.JComponent;

/**
The NullRepaintManager is a RepaintManager that doesn't
do any repainting. Useful when all the rendering is done
manually by the application.
 */
public class NullRepaintManager extends RepaintManager {

    /**
    Installs the NullRepaintManager.  => ignore repaint of components  => won't get flicker
     */
    public static void install() {
        RepaintManager repaintManager = new NullRepaintManager();
        repaintManager.setDoubleBufferingEnabled(false);
        RepaintManager.setCurrentManager(repaintManager);      // RepaintManager now is my repaintManager
    }

    @Override
    public void addInvalidComponent(JComponent c) {
        // do nothing
    }

    @Override
    public void addDirtyRegion(JComponent c, int x, int y,
            int w, int h) {
        // do nothing
    }

    @Override
    public void markCompletelyDirty(JComponent c) {
        // do nothing
    }

    @Override
    public void paintDirtyRegions() {
        // do nothing
    }
}
