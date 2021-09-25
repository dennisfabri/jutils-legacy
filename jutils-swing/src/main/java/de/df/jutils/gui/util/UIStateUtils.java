package de.df.jutils.gui.util;

import java.awt.Window;
import java.io.PrintStream;
import java.util.List;
import java.util.ListIterator;
import java.util.prefs.Preferences;

import org.lisasp.legacy.uistate.UIStateHandler;
import org.lisasp.legacy.uistate.UIStateManager;
import org.lisasp.legacy.uistate.handlers.JListStateHandler;
import org.lisasp.legacy.uistate.handlers.JTabbedPaneStateHandler;
import org.lisasp.legacy.uistate.handlers.JTableStateHandler;
import org.lisasp.legacy.uistate.handlers.JTreeStateHandler;
import org.lisasp.legacy.uistate.handlers.JViewportStateHandler;

public class UIStateUtils {

    private static class UIStateManageRunnable implements Runnable {

        private Window parent;
        private Window window;
        private String name;

        public UIStateManageRunnable(Window parent, Window w, String n) {
            this.parent = parent;
            window = w;
            name = n;
        }

        @Override
        public void run() {
            WindowUtils.checkMinimumSize(window);
            WindowUtils.center(window, parent);

            UIStateManager.manage(window, name);
            WindowUtils.checkMinimumSize(window);
            if (!isOnScreen(window)) {
                WindowUtils.center(window, parent);
            }
        }
    }

    private static boolean isOnScreen(Window window) {
        java.awt.Rectangle windowSize = new java.awt.Rectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight());
        for (java.awt.GraphicsDevice screen : java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            for (java.awt.GraphicsConfiguration configuration : screen.getConfigurations()) {
                java.awt.Rectangle  screenSize = configuration.getBounds();
                if (intersect(screenSize, windowSize)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean intersect(java.awt.Rectangle  rect1, java.awt.Rectangle  rect2) {
        return oneDimensionalIntersect(rect1.getX(), rect1.getX()+rect1.getWidth(), rect2.getX(), rect2.getX()+rect2.getWidth())
                && oneDimensionalIntersect(rect1.getY(), rect1.getY()+rect1.getHeight(), rect2.getY(), rect2.getY()+rect2.getHeight());
    }
    
    private static boolean oneDimensionalIntersect(double min1, double max1, double min2, double max2) {
        return min2 <= max1 && min1 <= max2;        
    }

    public static void uistatemanage(Window parent, Window w, String name) {
        EDTUtils.executeOnEDT(new UIStateManageRunnable(parent, w, name));
    }

    public static void uistatemanage(Window w, String name) {
        uistatemanage(null, w, name);
    }

    public static void uistatemanage(Window parent, Window w) {
        uistatemanage(parent, w, w.getClass().getName() + "|" + w.getName());
    }

    public static void uistatemanage(Window w) {
        uistatemanage(null, w);
    }

    public static void initUIState() {
        try {
            UIStateManager.setPreferences(Preferences.userRoot().node("jauswertung/uistate"));
            List<UIStateHandler> l = UIStateManager.getDefaultHandlers();
            ListIterator<UIStateHandler> li = l.listIterator();
            while (li.hasNext()) {
                Object o = li.next();
                if (o instanceof JTreeStateHandler || o instanceof JListStateHandler || o instanceof JTableStateHandler
                        || o instanceof JViewportStateHandler || o instanceof JTabbedPaneStateHandler) {
                    li.remove();
                }
            }
            UIStateManager.setDefaultHandlers(l);
            l = UIStateManager.getDefaultHandlers();
            PrintStream err = System.err;
            if (l.size() != 4) {
                err.println("Changes in UIStateManager:");
                li = l.listIterator();
                while (li.hasNext()) {
                    err.println("  " + li.next());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
