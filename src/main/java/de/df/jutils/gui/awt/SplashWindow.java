package de.df.jutils.gui.awt;

/*
 * @(#)SplashWindow.java 2.2 2005-04-03
 * Copyright (c) 2003-2005 Werner Randelshofer Staldenmattweg 2, Immensee,
 * CH-6405, Switzerland. All rights reserved.
 * This software is in the public domain.
 */

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * A Splash window.
 * <p>
 * Usage: MyApplication is your application class. Create a Splasher class which
 * opens the splash window, invokes the main method of your Application class,
 * and disposes the splash window afterwards. Please note that we want to keep
 * the Splasher class and the SplashWindow class as small as possible. The less
 * code and the less classes must be loaded into the JVM to open the splash
 * screen, the faster it will appear.
 * 
 * <pre>
 * class Splasher {
 *     public static void main(String[] args) {
 *         SplashWindow.splash(Startup.class.getResource(&quot;splash.gif&quot;));
 *         MyApplication.main(args);
 *         SplashWindow.disposeSplash();
 *     }
 * }
 * </pre>
 * 
 * @author Werner Randelshofer
 * @version 2.1 2005-04-03 Revised.
 */
public final class SplashWindow extends Window {
    /**
     * 
     */
    private static final long   serialVersionUID = 3545512915537770035L;

    /**
     * The current instance of the splash window. (Singleton design pattern).
     */
    private static SplashWindow instance;

    /**
     * The splash image which is displayed on the splash window.
     */
    private Image               image;

    /**
     * This attribute indicates whether the method paint(Graphics) has been
     * called at least once since the construction of this window.<br>
     * This attribute is used to notify method splash(Image) that the window has
     * been drawn at least once by the AWT event dispatcher thread.<br>
     * This attribute acts like a latch. Once set to true, it will never be
     * changed back to false again.
     * 
     * @see #paint
     * @see #splash
     */
    protected boolean           paintCalled      = false;

    /**
     * Creates a new instance.
     * 
     * @param parent
     *            the parent of the window.
     * @param image
     *            the splash image.
     */
    public SplashWindow(Image image) {
        super(new Frame());
        this.image = image;

        // Load the image
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 0);
        try {
            mt.waitForID(0);
        } catch (InterruptedException ie) {
            // Nothing to do
        }

        // Center the window on the screen
        int imgWidth = image.getWidth(this);
        int imgHeight = image.getHeight(this);
        setSize(imgWidth, imgHeight);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenDim.width - imgWidth) / 8;
        int y = (screenDim.height - imgHeight) / 8;
        setLocation(x, y);

        // Users shall be able to close the splash window by
        // clicking on its display area. This mouse listener
        // listens for mouse clicks and disposes the splash window.
        MouseAdapter disposeOnClick = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // Note: To avoid that method splash hangs, we
                // must set paintCalled to true and call notifyAll.
                // This is necessary because the mouse click may
                // occur before the contents of the window
                // has been painted.
                synchronized (this) {
                    paintCalled = true;
                    notifyAll();
                }
                dispose();
            }
        };
        addMouseListener(disposeOnClick);
    }

    /**
     * Updates the display area of the window.
     */
    @Override
    public void update(Graphics g) {
        // Note: Since the paint method is going to draw an
        // image that covers the complete area of the component we
        // do not fill the component with its background color
        // here. This avoids flickering.
        paint(g);
    }

    /**
     * Paints the image on the window.
     */
    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);

        // Notify method splash that the window
        // has been painted.
        // Note: To improve performance we do not enter
        // the synchronized block unless we have to.
        synchronized (this) {
            if (!paintCalled) {
                paintCalled = true;
                notifyAll();
            }
        }
    }

    /**
     * Open's a splash window using the specified image.
     * 
     * @param image
     *            The splash image.
     */
    public static void splash(Image image) {
        if (instance == null && image != null) {
            // Create the splash image
            instance = new SplashWindow(image);

            // Show the window.
            instance.setVisible(true);

            // Note: To make sure the user gets a chance to see the
            // splash window we wait until its paint method has been
            // called at least once by the AWT event dispatcher thread.
            // If more than one processor is available, we don't wait,
            // and maximize CPU throughput instead.
            if (!EventQueue.isDispatchThread()) {
                while (!instance.paintCalled) {
                    try {
                        instance.wait();
                    } catch (InterruptedException e) {
                        // Nothing to do
                    }
                }
            }
        }
    }

    /**
     * Open's a splash window using the specified image.
     * 
     * @param imageURL
     *            The url of the splash image.
     */
    public static void splash(URL imageURL) {
        if (imageURL != null) {
            splash(Toolkit.getDefaultToolkit().createImage(imageURL));
        }
    }

    /**
     * Open's a splash window using the specified image.
     * 
     * @param filename
     *            The name of the splash image.
     */
    public static void splash(String filename) {
        try {
            splash(ImageIO.read(new File("logo.png")));
        } catch (IOException e) {
            // Nothing to do
        }
    }

    /**
     * Closes the splash window.
     */
    public static void disposeSplash() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }
}