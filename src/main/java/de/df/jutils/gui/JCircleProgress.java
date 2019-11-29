package de.df.jutils.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * An infinite progress panel displays a rotating figure for unknown duration
 * tasks. The shape is drawn upon a white veil with alpha level (or shield
 * value) lets the underlying component shine through. This panel is meant to be
 * used as a <i>glass pane</i> in the window performing the long operation.
 * Alternatively it can be combined with labels in a dialog to indicate busy
 * state. In any case cursor is set to the <cite>wait cursor</cite> shape.
 * <p>
 * The typical scenario when used in a glass pane is:<br>
 * <ol>
 * <li>capture the keyboard and mouse events to disable user input</li>
 * <li>add to / set as glass pane and make visible (causing animation to start)</li>
 * <li>commence the processing</li>
 * <li>processing is finished</li>
 * <li>stop blocking keyabord and mouse events</li>
 * <li>hide and remove progress panel (stopping the animation)</li>
 * </ol>
 * While the principal attributes are immutable (bar count, speed), others can
 * be modified even during active animations (color, bounds). As always invoke
 * them in AWT thread to be thread safe.
 * <p>
 * Animation is controlled using explicit start/stop method calls.
 * <p>
 * 
 * @author Romain Guy
 * @version 1.0.0
 */

public class JCircleProgress extends JComponent implements ActionListener {

    private static final long serialVersionUID  = 3833748793492386609L;

    /** Default bar count */
    private static final int  DEFAULT_BAR_COUNT = 14;

    /** Bar shapes */
    private Area[]            barShapes         = null;

    /** Bounds for all the bars */
    private Rectangle         barsBounds        = null;

    /** Normalization for bars to the component bounds. */
    private AffineTransform   normalizeTrans    = null;

    /** Bar colors */
    private Color[]           barColors         = null;

    /** Timer for animation steps */
    private Timer             timer             = null;

    /** Current color offset, managed by the timer */
    private int               colorOffs         = 0;

    /**
     * Init.
     * 
     * @param bar_cnt
     *            (optional) bar count
     * @param speed
     *            (optional) number of animations steps per second (default 15)
     */
    public JCircleProgress(int barcnt, final int speed) {
        assert (barcnt < 40) && (speed < 100);

        if (barcnt <= 0) {
            barcnt = DEFAULT_BAR_COUNT;
        }
        barShapes = buildBars(barcnt); // build bars
        // calculate bars bounding rectangle
        barsBounds = new Rectangle();
        int index = 0;
        for (; index < barcnt; index++) {
            barsBounds = barsBounds.union(barShapes[index].getBounds());
        }

        setForeground(new Color(224, 224, 224)); // init colors
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // set
        // cursor
        setOpaque(true); // set opaque

        timer = new Timer(1000 / (speed <= 0 ? 15 : speed), this);
        timer.setRepeats(true);
        timer.setCoalesce(true);
    }

    /**
     * Builds the circular shape and returns the result as an array of
     * <code>Area</code>. Each <code>Area</code> is one of the bars composing
     * the shape.
     */
    private static Area[] buildBars(final int barCount) {
        Area[] res = new Area[barCount];
        int auxIndex = 0;
        double cnt = barCount;
        double fixedAngle = 2.0 * Math.PI / cnt;
        Area newBar = null;
        Area barTemplate = buildBar();
        AffineTransform tranz = null;
        for (double index = 0; index < cnt; index++, auxIndex++) {
            newBar = (Area) barTemplate.clone(); // get a new bar
            // calc its transform
            tranz = AffineTransform.getRotateInstance(-index * fixedAngle);
            tranz.concatenate(AffineTransform.getTranslateInstance(45.0, -6.0));
            newBar.transform(tranz); // transform
            res[auxIndex] = newBar;
        }

        return res;
    } // buildBars

    /** Builds a bar. */
    private static Area buildBar() {
        Rectangle2D.Double body = new Rectangle2D.Double(6, 0, 30, 12);
        Ellipse2D.Double head = new Ellipse2D.Double(0, 0, 12, 12);
        Ellipse2D.Double tail = new Ellipse2D.Double(30, 0, 12, 12);

        Area bar = new Area(body);
        bar.add(new Area(head));
        bar.add(new Area(tail));
        return bar;
    }

    public boolean isRunning() {
        return timer.isRunning();
    }

    /** Start progress animation. */
    public void start() {
        timer.start();
    }

    /** Stop progress animation. */
    public void stop() {
        timer.stop();
    }

    /** Recalc bars based on changes in size */
    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, width, height);
        double scale = (double) Math.min(width, height) / (double) 200;
        // update centering transform
        normalizeTrans = new AffineTransform();
        normalizeTrans.translate(getWidth() / 2d, getHeight() / 2d);
        normalizeTrans.scale(scale, scale);
    }

    @Override
    public void setForeground(final Color c) {
        super.setForeground(c);

        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int maxComp = Math.max(Math.max(r, g), b);

        barColors = new Color[barShapes.length * 2];
        if (maxComp > 128) {
            for (int index = 0; index < barShapes.length; index++) {
                barColors[barShapes.length + index] = new Color(Math.max(0, r - 128 / (index + 1)), Math.max(0, g - 128 / (index + 1)),
                        Math.max(0, b - 128 / (index + 1)));
                barColors[index] = barColors[barShapes.length + index];
            }
        } else {
            for (int index = 0; index < barShapes.length; index++) {
                barColors[barShapes.length + index] = new Color(Math.min(255, r + 128 / (index + 1)), Math.min(255, g + 128 / (index + 1)),
                        Math.min(255, b + 128 / (index + 1)));
                barColors[index] = barColors[barShapes.length + index];
            }
        }
    } // setForeground

    /** Paint background dimmed and bars over top. */
    @Override
    protected void paintComponent(final Graphics gc) {
        Rectangle clip = gc.getClipBounds();

        if (isOpaque()) {
            gc.setColor(new Color(255, 255, 255, 160)); // semi-transparent
            gc.fillRect(clip.x, clip.y, clip.width, clip.height);
        }
        // move to center
        Graphics2D auxGC = (Graphics2D) gc.create();
        auxGC.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        auxGC.transform(normalizeTrans);
        // draw ticker
        for (int index = 0; index < barShapes.length; index++) {
            auxGC.setColor(barColors[index + colorOffs]);
            auxGC.fill(barShapes[index]);
        }
    } // paintComponent

    // Handlers ---------------------------------

    /** Called to animate the rotation of the bar's colors. */
    @Override
    public void actionPerformed(final ActionEvent e) { // rotate colors
        if (colorOffs == barShapes.length) {
            colorOffs = 0; // wrap around
        } else {
            colorOffs++;
        }
        repaint(); // repaint
    }
}