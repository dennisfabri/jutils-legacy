package skt.swing.scroll;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JRootPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * Modified version. The original can be found at:
 * 
 * @author Santhosh Kumar T
 * @email santhosh@in.fiorano.com
 * @link http://jroller.com/trackback/santhosh/Weblog/enhanced_scrolling_in_swing
 */
public final class ScrollGestureRecognizer implements AWTEventListener {
    private static ScrollGestureRecognizer instance = new ScrollGestureRecognizer();

    private ScrollGestureRecognizer() {
        start();
    }

    public static ScrollGestureRecognizer getInstance() {
        return instance;
    }

    void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
    }

    void stop() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        MouseEvent me = (MouseEvent) event;
        boolean isGesture = SwingUtilities.isMiddleMouseButton(me) && me.getID() == MouseEvent.MOUSE_PRESSED;
        if (!isGesture) {
            return;
        }

        Component comp = me.getComponent();
        comp = SwingUtilities.getDeepestComponentAt(comp, me.getX(), me.getY());
        JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, comp);
        if (viewPort == null || !canScroll(viewPort)) {
            return;
        }
        viewPort.getViewSize();

        JRootPane rootPane = SwingUtilities.getRootPane(viewPort);
        if (rootPane == null) {
            return;
        }
        Point location = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), rootPane.getGlassPane());
        ScrollGlassPane glassPane = new ScrollGlassPane(rootPane.getGlassPane(), viewPort, location);
        rootPane.setGlassPane(glassPane);
        glassPane.setVisible(true);

        try {
            Robot robot = new Robot();
            robot.mouseRelease(InputEvent.BUTTON2_MASK);
            robot.mousePress(InputEvent.BUTTON2_MASK);
        } catch (AWTException ignore) {
            glassPane.movingTimer.start();
        }
    }

    private static boolean canScroll(JViewport viewport) {
        return (ScrollUtils.getDirections(viewport) != 0);
    }
}