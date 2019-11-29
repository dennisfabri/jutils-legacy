package de.df.jutils.gui.util;

import java.awt.Window;

import de.df.uistate.UIStateManager;

public class UIStateUtils {
    private static class UIStateManageRunnable implements Runnable {

        private Window window;
        private String name;

        public UIStateManageRunnable(Window w, String n) {
            window = w;
            name = n;
        }

        @Override
        public void run() {
            UIStateManager.manage(window, name);
            WindowUtils.checkMinimumSize(window);
        }
    }

    public static void uistatemanage(Window w, String name) {
    	EDTUtils.executeOnEDT(new UIStateManageRunnable(w, name));
    }

    public static void uistatemanage(Window w) {
        EDTUtils.executeOnEDT(new UIStateManageRunnable(w, w.getClass().getName() + "|" + w.getName()));
    }

}
