package skt.swing.scroll;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

/**
 * Modified version. The original can be found at:
 * 
 * @author Santhosh Kumar T
 * @email santhosh@in.fiorano.com
 * @link
 *       http://jroller.com/trackback/santhosh/Weblog/enhanced_scrolling_in_swing
 */
class ScrollGlassPane extends JPanel implements ActionListener, MouseInputListener, SwingConstants {

    private static final long     serialVersionUID = -899406324852168721L;

    private static final Image[]  IMAGES           = ScrollUtils.createImages();
    private static final Cursor[] CURSORS          = ScrollUtils.createCursors();

    private Component             oldGlassPane     = null;
    private Point                 location         = null;

    Timer                         movingTimer;
    private Point                 mouseLocation;
    private JViewport             viewport;
    private int                   oldDirection     = -1;

    public ScrollGlassPane(Component oldGlassPane, JViewport viewport, Point location) {
        this.oldGlassPane = oldGlassPane;
        this.viewport = viewport;
        this.location = location;
        mouseLocation = location;
        changeCursor();

        setOpaque(false);

        ScrollGestureRecognizer.getInstance().stop();
        addMouseListener(this);
        addMouseMotionListener(this);

        movingTimer = new Timer(100, this);
        movingTimer.setRepeats(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.drawImage(IMAGES[getDirections()], location.x - 15, location.y - 15, this);
    }

    private int getDirections() {
        return ScrollUtils.getDirections(viewport);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int deltax = (mouseLocation.x - location.x) / 4;
        int deltay = (mouseLocation.y - location.y) / 4;

        Point p = viewport.getViewPosition();
        p.translate(deltax, deltay);

        if (p.x < 0) {
            p.x = 0;
        } else {
            if (p.x >= viewport.getView().getWidth() - viewport.getWidth()) {
                p.x = viewport.getView().getWidth() - viewport.getWidth();
            }
        }
        if (p.y < 0) {
            p.y = 0;
        } else {
            if (p.y >= viewport.getView().getHeight() - viewport.getHeight()) {
                p.y = viewport.getView().getHeight() - viewport.getHeight();
            }
        }
        viewport.setViewPosition(p);
        changeCursor();
    }

    private void changeCursor() {
        int dx = mouseLocation.x - location.x;
        int dy = mouseLocation.y - location.y;

        int direction = ScrollUtils.DIRECTION_NONE;

        if (Math.abs(dy) > 3) {
            if (dy > 0) {
                direction = direction | ScrollUtils.DIRECTION_BOTTOM;
            } else {
                direction = direction | ScrollUtils.DIRECTION_TOP;
            }
        }

        if (Math.abs(dx) > 3) {
            if (dx > 0) {
                direction = direction | ScrollUtils.DIRECTION_RIGHT;
            } else {
                direction = direction | ScrollUtils.DIRECTION_LEFT;
            }
        }

        direction = direction & ScrollUtils.getDirections(viewport);

        if (direction != oldDirection) {
            oldDirection = direction;
            setCursor(CURSORS[direction]);
        }
    }

    /*------------------------[ MouseListener ]--------------------------*/

    @Override
    public void mousePressed(MouseEvent e) {
        if (ignoreMousePress) {
            ignoreMousePress = false;
            return;
        }
        if (movingTimer.isRunning()) {
            movingTimer.stop();
            try {
                new Robot().mouseRelease(InputEvent.BUTTON2_MASK);
            } catch (AWTException ignore) {
                // Nothing to do
            }
            setVisible(false);
            JRootPane rootPane = SwingUtilities.getRootPane(this);
            rootPane.setGlassPane(oldGlassPane);
            ScrollGestureRecognizer.getInstance().start();
        } else {
            movingTimer.start();
        }
    }

    private boolean ignoreMousePress = false;

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragged) {
            mousePressed(e);
        } else {
            try {
                ignoreMousePress = true;
                Robot robot = new Robot();
                robot.mousePress(InputEvent.BUTTON2_MASK);
            } catch (AWTException ignore) {
                mousePressed(e);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLocation = e.getPoint();
        changeCursor();
    }

    private boolean dragged = false;

    @Override
    public void mouseDragged(MouseEvent e) {
        dragged = true;
        mouseLocation = e.getPoint();
        changeCursor();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Nothing to do
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Nothing to do
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Nothing to do
    }
}