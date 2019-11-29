/*
 * This is a modified version of sources found in the swingfx-project: Copyright
 * (c) 2005, romain guy (romain.guy@jext.org) and craig wickesser
 * (craig@codecraig.com) All rights reserved. Redistribution and use in source
 * and binary forms, with or without modification, are permitted provided that
 * the following conditions are met: * Redistributions of source code must
 * retain the above copyright notice, this list of conditions and the following
 * disclaimer. * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. *
 * Neither the name of the <ORGANIZATION> nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * The code has been optimized based on
 * https://github.com/jenkinsci/slave-installer-module/blob/master/src/main/java/org/jenkinsci/modules/slave_installer/impl/InfiniteProgressPanel.java
 * Visited: 22.06.2017
 * The code has been modified by Dennis Fabri
 */
package net.java.swingfx.waitwithstyle;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.*;

import javax.swing.JComponent;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.util.EDTUtils;

/**
 * An infinite progress panel displays a rotating figure and a message to notice
 * the user of a long, duration unknown task. The shape and the text are drawn
 * upon a white veil which alpha level (or shield value) lets the underlying
 * component shine through. This panel is meant to be used asa <i>glass pane</i>
 * in the window performing the long operation.
 * <p>
 * Contrary to regular glass panes, you don't need to set it visible or not by
 * yourself. Once you've started the animation all the mouse events are
 * intercepted by this panel, preventing them from being forwared to the
 * underlying components.
 * <p>
 * The panel can be controlled by the <code>start()</code>, <code>stop()</code>
 * and <code>interrupt()</code> methods.
 * <p>
 * Example:
 * <p>
 * 
 * <pre>
 *                   InfiniteProgressPanel pane = new InfiniteProgressPanel();
 *                    frame.setGlassPane(pane);
 *                    ... later in some other EDT event 
 *                    ... (otherwise the panel doesn't know the size and draws real funky)
 *                    pane.start()
 * </pre>
 * <p>
 * Several properties can be configured at creation time. The message and its
 * font can be changed at runtime. Changing the font can be done using
 * <code>setFont()</code> and <code>setForeground()</code>.
 * <p>
 * If you experience performance issues, prefer the
 * <code>PerformanceInfiniteProgressPanel</code>.
 * <p>
 * For cancelable progress use the <code>CancelableProgressPanel</code> or the
 * <code>CancelableProgressAdapter</code> with a Panel.
 * 
 * @author Romain Guy, 17/02/2005
 * @since 1.0 <br>
 *        $Revision: 1.5 $
 */

public class InfiniteProgressPanel extends JComponent implements CancelableAdaptee {
    private static final long         serialVersionUID        = 3546080263571714356L;

    private static MouseListener      EmptyMouseListener      = new MouseAdapter() {
                                                              };

    private static final int          SCALE                   = 4;

    /** Contains the bars composing the circular shape. */
    protected Ticker                  ticker                  = null;
    /**
     * The animation thread is responsible for fade in/out and rotation.
     */
    protected Animator                animation               = null;
    /**
     * Notifies whether the animation is running or not.
     */
    protected boolean                 started                 = false;
    /**
     * Alpha level of the veil, used for fade in/out.
     */
    protected int                     alphaLevel              = 0;
    /**
     * Duration of the veil's fade in/out.
     */
    protected int                     rampDelay               = 300;
    /**
     * Alpha level of the veil.
     */
    protected float                   shield                  = 0.70f;
    /**
     * Message displayed below the circular shape.
     */
    protected String                  text                    = "";
    /**
     * Amount of bars composing the circular shape.
     */
    protected int                     barsCount               = 16;
    /**
     * Amount of frames per seconde. Lowers this to save CPU.
     */
    protected float                   fps                     = 10.0f;
    /**
     * Rendering hints to set anti aliasing.
     */
    protected RenderingHints          hints                   = null;
    /**
     * An infiniteProgressAdapter to performa special drawing, ex: a cancel
     * button.
     */
    protected InfiniteProgressAdapter infiniteProgressAdapter = null;

    private static Color              colorNormal             = new Color(200, 200, 200);

    private static Color              colorFocus              = new Color(80, 80, 80);

    /**
     * Creates a new progress panel with default values:<br />
     * <ul>
     * <li>No message</li>
     * <li>14 bars</li>
     * <li>Veil's alpha level is 70%</li>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     */
    public InfiniteProgressPanel() {
        this("");
    }

    /**
     * Creates a new progress panel with default values:<br />
     * <ul>
     * <li>14 bars</li>
     * <li>Veil's alpha level is 70%</li>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     * 
     * @param text
     *            The message to be displayed. Can be null or empty.
     */
    public InfiniteProgressPanel(String text) {
        this(text, 16);
    }

    /**
     * Creates a new progress panel with default values:<br />
     * <ul>
     * <li>Veil's alpha level is 70%</li>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     * 
     * @param text
     *            The message to be displayed. Can be null or empty.
     * @param barsCount
     *            The amount of bars composing the circular shape
     */
    private InfiniteProgressPanel(String text, int barsCount) {
        this(text, barsCount, 0.70f);
    }

    /**
     * Creates a new progress panel with default values:<br />
     * <ul>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     * 
     * @param text
     *            The message to be displayed. Can be null or empty.
     * @param barsCount
     *            The amount of bars composing the circular shape.
     * @param shield
     *            The alpha level between 0.0 and 1.0 of the colored shield (or
     *            veil).
     */
    private InfiniteProgressPanel(String text, int barsCount, float shield) {
        this(text, barsCount, shield, 10.0f);
    }

    /**
     * Creates a new progress panel with default values:<br />
     * <ul>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     * 
     * @param text
     *            The message to be displayed. Can be null or empty.
     * @param barsCount
     *            The amount of bars composing the circular shape.
     * @param shield
     *            The alpha level between 0.0 and 1.0 of the colored shield (or
     *            veil).
     * @param fps
     *            The number of frames per second. Lower this value to decrease
     *            CPU usage.
     */
    private InfiniteProgressPanel(String text, int barsCount, float shield, float fps) {
        this(text, barsCount, shield, fps, 300);
    }

    /**
     * Creates a new progress panel.
     * 
     * @param text
     *            The message to be displayed. Can be null or empty.
     * @param barsCount
     *            The amount of bars composing the circular shape.
     * @param shield
     *            The alpha level between 0.0 and 1.0 of the colored shield (or
     *            veil).
     * @param fps
     *            The number of frames per second. Lower this value to decrease
     *            CPU usage.
     * @param rampDelay
     *            The duration, in milli seconds, of the fade in and the fade
     *            out of the veil.
     */
    public InfiniteProgressPanel(String text, int barsCount, float shield, float fps, int rampDelay) {
        this.text = text;
        this.rampDelay = rampDelay >= 0 ? rampDelay : 0;
        this.shield = shield >= 0.0f ? shield : 0.0f;
        this.fps = fps > 0.0f ? fps : 15.0f;
        this.barsCount = barsCount > 0 ? barsCount : 16;

        this.hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        this.hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    /**
     * Changes the displayed message at runtime.
     * 
     * @param text
     *            The message to be displayed. Can be null or empty.
     */
    @Override
    public void setText(String text) {
        this.text = text;
        repaint();
    }

    /**
     * Returns the current displayed message.
     */
    public String getText() {
        return text;
    }

    public static void setColorNormal(Color cNormal) {
        colorNormal = cNormal;
    }

    public static void setColorFocus(Color cFocus) {
        colorFocus = cFocus;
    }

    /**
     * @param infiniteProgressAdapter
     *            an infiniteProgressAdapter to perform special drawing (ex: a
     *            cancel button)
     */
    public void setInfiniteProgressAdapter(InfiniteProgressAdapter infiniteProgressAdapter) {
        this.infiniteProgressAdapter = infiniteProgressAdapter;
    }

    /**
     * Adds a listener to the cancel button in this progress panel.
     * 
     * @throws RuntimeException
     *             if the infiniteProgressAdapter is null or is not a
     *             CancelableProgessAdapter
     * @param listener
     */
    @Override
    public void addCancelListener(ActionListener listener) {
        if (infiniteProgressAdapter instanceof CancelableProgessAdapter) {
            ((CancelableProgessAdapter) infiniteProgressAdapter).addCancelListener(listener);
        } else {
            throw new RuntimeException("Expected CancelableProgessAdapter for cancel listener.  Adapter is " + infiniteProgressAdapter);
        }
    }

    /**
     * Removes a listener to the cancel button in this progress panel.
     * 
     * @throws RuntimeException
     *             if the infiniteProgressAdapter is null or is not a
     *             CancelableProgessAdapter
     * @param listener
     */
    @Override
    public void removeCancelListener(ActionListener listener) {
        if (infiniteProgressAdapter instanceof CancelableProgessAdapter) {
            ((CancelableProgessAdapter) infiniteProgressAdapter).removeCancelListener(listener);
        } else {
            throw new RuntimeException("Expected CancelableProgessAdapter for cancel listener.  Adapter is " + infiniteProgressAdapter);
        }
    }

    /**
     * Starts the waiting animation by fading the veil in, then rotating the
     * shapes. This method handles the visibility of the glass pane.
     */
    @Override
    public void start() {
        addMouseListener(EmptyMouseListener);
        EDTUtils.setVisible(this, true);
        buildTicker();
        animation = new Animator(true);
        if (infiniteProgressAdapter != null) {
            infiniteProgressAdapter.animationStarting();
        }
        animation.start();
    }

    /**
     * Stops the waiting animation by stopping the rotation of the circular
     * shape and then by fading out the veil. This methods sets the panel
     * invisible at the end.
     */
    @Override
    public void stop() {
        if (infiniteProgressAdapter != null) {
            infiniteProgressAdapter.animationStopping();
        }
        if (animation != null) {
            animation.interrupt();
            try {
                animation.join();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            animation = null;

            animation = new Animator(false);
            animation.start();
        }
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     * Interrupts the animation, whatever its state is. You can use it when you
     * need to stop the animation without running the fade out phase. This
     * methods sets the panel invisible at the end.
     */
    public void interrupt() {
        if (animation != null) {
            animation.interrupt();
            animation = null;

            removeMouseListener(EmptyMouseListener);
            EDTUtils.setVisible(this, false);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (started) {
            int width = getWidth();
            int height = getHeight();

            if (width == 0 || height == 0) {
                return;
            }

            Ticker lticker = getTicker();
            if (lticker == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            if (hints != null) {
                g2.setRenderingHints(hints);
            }

            g2.setColor(new Color(255, 255, 255, (int) (alphaLevel * shield)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i < lticker.bars.length; i++) {
                // int channel = 224 - 128 / (i + 1);
                // g2.setColor(new Color(channel, channel, channel, alphaLevel));
                double part = 2.0 / 3.0;
                double percent = -1.0 * i / lticker.bars.length;
                percent = Math.max((percent + part) / part, 0.0);
                g2.setColor(ColorUtils.calculateColor(colorNormal, colorFocus, percent));
                g2.fill(lticker.bars[i]);
            }

            double textMaxY = drawTextAt(text, getFont(), g2, width, lticker.maxY, getForeground());

            if (infiniteProgressAdapter != null) {
                infiniteProgressAdapter.paintSubComponents(textMaxY);
            }
        }
    }

    /**
     * Draw text in a Graphics2D.
     * 
     * @param text
     *            the text to draw
     * @param font
     *            the font to use
     * @param g2
     *            the graphics context to draw in
     * @param width
     *            the width of the parent, so it can be centered
     * @param y
     *            the height at which to draw
     * @param foreGround
     *            the foreground color to draw in
     * @return the y value that is the y param + the text height.
     */
    public static double drawTextAt(String text, Font font, Graphics2D g2, int width, double y, Color foreGround) {
        if (text != null && text.length() > 0) {
            FontRenderContext context = g2.getFontRenderContext();
            TextLayout layout = new TextLayout(text, font, context);
            Rectangle2D bounds = layout.getBounds();
            g2.setColor(foreGround);
            float textX = (float) (width - bounds.getWidth()) / 2;
            y = (float) (y + layout.getLeading() + 2 * layout.getAscent());
            layout.draw(g2, textX, (float) y);
        }
        return y;
    }

    /**
     * Ticker is not built until set bounds is called (or our width and height
     * are > 0).
     * 
     * @return null if not ready, or the built ticker
     */
    synchronized Ticker getTicker() {
        if (ticker == null) {
            buildTicker();
        }
        return ticker;
    }

    /**
     * Builds the circular shape and returns the result as an array of
     * <code>Area</code>. Each <code>Area</code> is one of the bars composing
     * the shape.
     */
    private void buildTicker() {
        Area[] areas = new Area[barsCount];
        int width = getWidth();
        int height = getHeight();
        // Sometimes the bounds are set, rebuild the ticker later.
        if (width == 0 || height == 0) {
            return;
        }

        AffineTransform toCenter = AffineTransform.getTranslateInstance(1.0 * width / 2, 1.0 * height / 2);
        double fixedAngle = 2.0 * Math.PI / barsCount;

        AffineTransform scaler = AffineTransform.getTranslateInstance(0, 0);
        {
            double radius = ((double) Math.min(getWidth(), getHeight())) / 2;
            double size = 45 /* from 'toBorder' above */ + 42 /* size of the pill */ + 20 /* margin */;
            if (radius < size) {
                // if our tickers are too big for the area, we need to scale it down
                scaler = AffineTransform.getScaleInstance(radius / size, radius / size);
            }
        }

        AffineTransform toBorder = AffineTransform.getTranslateInstance(7.5 * SCALE, -1.0 * SCALE);

        double maxY = 0.0d;
        for (int i = 0; i < barsCount; i++) {
            Area primitive = buildPrimitive();

            AffineTransform toCircle = AffineTransform.getRotateInstance(-1.0 * i * fixedAngle);

            primitive.transform(toBorder);
            primitive.transform(toCircle);
            primitive.transform(scaler);
            primitive.transform(toCenter);

            areas[i] = primitive;

            Rectangle2D bounds = primitive.getBounds2D();
            if (bounds.getMaxY() > maxY) {
                maxY = bounds.getMaxY();
            }
        }

        ticker = new Ticker();
        ticker.bars = areas;
        ticker.maxY = maxY;
    }

    /**
     * Builds a bar.
     */
    private Area buildPrimitive() {
        // Rectangle2D.Double body = new Rectangle2D.Double(6, 0, 30, 12);
        // Ellipse2D.Double head = new Ellipse2D.Double(0, 0, 12, 12);
        // Ellipse2D.Double tail = new Ellipse2D.Double(30, 0, 12, 12);

        Rectangle2D.Double body = new Rectangle2D.Double(1.0 * SCALE, 0, 5.0 * SCALE, 2.0 * SCALE);
        Ellipse2D.Double head = new Ellipse2D.Double(0, 0, 2.0 * SCALE, 2.0 * SCALE);
        Ellipse2D.Double tail = new Ellipse2D.Double(5.0 * SCALE, 0, 2.0 * SCALE, 2.0 * SCALE);

        Area tick = new Area(body);
        tick.add(new Area(head));
        tick.add(new Area(tail));

        return tick;
    }

    static class Ticker {
        double maxY = 0.0;
        Area[] bars;
    }

    /**
     * Animation thread.
     */
    private class Animator extends Thread {
        private boolean rampUp = true;

        protected Animator(boolean rampUp) {
            setPriority(Thread.NORM_PRIORITY);
            setName("InfiniteProgressPanel.Animator");
            this.rampUp = rampUp;
        }

        class GetTicker implements Runnable {

            private Ticker gticker = null;

            @Override
            public void run() {
                gticker = InfiniteProgressPanel.this.getTicker();
            }

            public Ticker getTicker() {
                return gticker;
            }
        }

        @Override
        public void run() {
            GetTicker gt = new GetTicker();
            EDTUtils.executeOnEDT(gt);
            Ticker lticker = gt.getTicker();
            if (lticker == null) {
                return;
            }

            Point2D.Double center = new Point2D.Double((double) getWidth() / 2, (double) getHeight() / 2);
            double fixedIncrement = 2.0 * Math.PI / barsCount;
            AffineTransform toCircle = AffineTransform.getRotateInstance(fixedIncrement, center.getX(), center.getY());

            long start = System.currentTimeMillis();
            if (rampDelay == 0) {
                alphaLevel = rampUp ? 255 : 0;
            }

            started = true;
            boolean inRamp = rampUp;

            while (!Thread.interrupted()) {
                if (!inRamp) {
                    for (int i = 0; i < lticker.bars.length; i++) {
                        lticker.bars[i].transform(toCircle);
                    }
                }

                EDTUtils.repaint(InfiniteProgressPanel.this);

                if (rampUp) {
                    if (alphaLevel < 255) {
                        double time = System.currentTimeMillis();
                        alphaLevel = (int) (255 * (time - start) / rampDelay);
                        if (alphaLevel >= 255) {
                            alphaLevel = 255;
                            inRamp = false;
                            if (infiniteProgressAdapter != null) {
                                infiniteProgressAdapter.rampUpEnded();
                            }
                        }
                    }
                } else if (alphaLevel > 0) {
                    alphaLevel = (int) (255 - (255 * (System.currentTimeMillis() - start) / rampDelay));
                    if (alphaLevel <= 0) {
                        alphaLevel = 0;
                        break;
                    }
                } else {
                    break;
                }

                try {
                    Thread.sleep(inRamp ? 10 : (int) (1000 / fps));
                } catch (InterruptedException ie) {
                    break;
                }
                Thread.yield();
            }

            if (!rampUp) {
                started = false;
                EDTUtils.repaint(InfiniteProgressPanel.this);
                EDTUtils.setVisible(InfiniteProgressPanel.this, false);
                removeMouseListener(EmptyMouseListener);
            }
        }
    }
}