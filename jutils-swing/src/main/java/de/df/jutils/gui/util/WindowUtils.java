/*
 * Created on 07.06.2005
 */
package de.df.jutils.gui.util;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

public final class WindowUtils {

    private WindowUtils() {
        // Hide
    }

    public static void addEscapeAction(JFrame window) {
        addEscapeAction(window, new CloseRunnable(window));
    }

    public static void addEscapeAction(JFrame window, Runnable exe) {
        addAction(window, exe, KeyEvent.VK_ESCAPE, 0, "ESCAPE");
    }

    public static void addAction(JFrame window, Runnable exe, int key, int modifiers, String name) {
        KeyStroke escape = KeyStroke.getKeyStroke(key, modifiers, false);
        window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, name);
        window.getRootPane().getActionMap().put(name, new EscapeAction(exe));
    }

    private static void addAction(JDialog window, Runnable exe, int key, int modifiers, String name) {
        KeyStroke escape = KeyStroke.getKeyStroke(key, modifiers, false);
        window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, name);
        window.getRootPane().getActionMap().put(name, new EscapeAction(exe));
    }

    private static void addEnterAction(JFrame window) {
        addEnterAction(window, new CloseRunnable(window));
    }

    public static void addEnterAction(JDialog window) {
        addEnterAction(window, new CloseRunnable(window));
    }

    public static void addEnterAction(JFrame window, Runnable exe) {
        addAction(window, exe, KeyEvent.VK_ENTER, 0, "ENTER");
    }

    public static void addEscapeAction(JDialog window) {
        addEscapeAction(window, new CloseRunnable(window));
    }

    public static void addEscapeAction(JDialog window, Runnable exe) {
        addAction(window, exe, KeyEvent.VK_ESCAPE, 0, "ESCAPE");
    }

    public static void addEnterAction(JDialog window, Runnable exe) {
        addAction(window, exe, KeyEvent.VK_ENTER, 0, "ENTER");
    }

    public static final class CloseRunnable implements Runnable {

        private Window window;

        public CloseRunnable(Window w) {
            window = w;
        }

        @Override
        public void run() {
            window.setVisible(false);
        }
    }

    private static final class EscapeAction extends AbstractAction {

        private static final long serialVersionUID = 3257572818995525944L;

        private final Runnable executor;

        private EscapeAction(Runnable exe) {
            executor = exe;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            executor.run();
        }
    }

    public static void center(Window frame) {
        center(frame, null);
    }

    public static void center(Window frame, Window parent) {
        if (parent == null) {
            Rectangle r = getVirtualScreenBounds(frame);

            int x = (int) (r.getX() + Math.max(0, (r.getWidth() - frame.getWidth()) / 2));
            int y = (int) (r.getY() + Math.max(0, (r.getHeight() - frame.getHeight()) / 2));

            frame.setLocation(x, y);
        } else {
            frame.setLocationRelativeTo(parent);
        }
    }

    private static Rectangle getVirtualScreenBounds(Window w) {
        return w.getGraphicsConfiguration().getDevice().getDefaultConfiguration().getBounds();
    }

    public static void setSize(Window w, int width, int height) {
        w.pack();
        Dimension d = w.getSize();
        w.setSize(Math.max(width, d.width), Math.max(height, d.height));
    }

    public static void checkMinimumSize(Window w) {
        int minWidth = w.getMinimumSize().width;
        int minHeight = w.getMinimumSize().height;
        Dimension d = w.getSize();

        int width = d.width;
        int height = d.height;

        if (width < minWidth || height < minHeight) {
            w.setSize(Math.max(width, minWidth), Math.max(height, minHeight));
        }
    }

    public static void maximize(Frame f) {
        f.setExtendedState(Frame.MAXIMIZED_BOTH);
    }
}
