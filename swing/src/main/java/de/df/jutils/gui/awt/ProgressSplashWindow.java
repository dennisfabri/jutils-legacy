/*
 * Created on 22.05.2004
 */
package de.df.jutils.gui.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

/**
 * @author Dennis Mueller
 * @date 22.05.2004
 */
public final class ProgressSplashWindow extends Dialog {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3834593201360154935L;

    public static final String[] DOTS = new String[] { " .", " ..", " ..." };

    private Component imageLabel = null;
    private Label textLabel = new Label("");
    private ProgressBar progress = new ProgressBar(0, 10);
    private int status = -1;
    private int factor = 0;
    private int maximum = 0;
    private String statusText = "";
    private int number = 0;
    private boolean autoclose = true;

    /**
     * @param icon       mainimage
     * @param foreground foregroundcolor
     * @param background backgroundcolor
     * @param steps      count of statusmessages
     * @param autoclose  close if steps has been reached
     * @throws java.awt.HeadlessException see JDialog
     */
    public ProgressSplashWindow(final Image bufferedImage, final int steps, boolean autoclose) {
        super(new Frame());
        setUndecorated(true);

        imageLabel = new ImageComponent(bufferedImage);

        // imageLabel = new JLabel(new ImageIcon(bufferedImage));

        this.autoclose = autoclose;
        factor = 100 / steps;
        maximum = steps * factor - 1;
        progress.setMaximum(maximum);

        setBackground(Color.WHITE);
        textLabel.setBackground(Color.WHITE);
        progress.setBackground(Color.WHITE);

        setLayout(null);
        add(imageLabel);
        add(textLabel);
        add(progress);
        pack();

        int labelheight = (int) Math
                .ceil(getFont().getStringBounds("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", 0, 2,
                        new FontRenderContext(new AffineTransform(), false, false)).getHeight());

        imageLabel.setLocation(5, 5);
        imageLabel.setSize(bufferedImage.getWidth(null), bufferedImage.getHeight(null));

        textLabel.setLocation(5, 5 + imageLabel.getHeight() + 5);
        textLabel.setSize(imageLabel.getWidth() / 2 - 5, labelheight);
        progress.setLocation(5 + textLabel.getWidth() + 5, 5 + imageLabel.getHeight() + 5);
        progress.setSize(imageLabel.getWidth() / 2 - 5, labelheight);

        setSize(imageLabel.getWidth() + 10, imageLabel.getHeight() + 16 + 15);
        setAlwaysOnTop(true);

        getGraphics().drawImage(bufferedImage, 5, 5, null);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        int x = (screenSize.width - windowSize.width) / 8;
        int y = (screenSize.height - windowSize.height) / 8;
        setLocation(x, y);
    }

    @Override
    public void setForeground(Color c) {
        super.setForeground(c);
        progress.setForeground(c);
        textLabel.setForeground(c);
    }

    /**
     * @param status statusinformation
     */
    public void setStatus(final String newStatus) {
        setVisible(true);
        statusText = newStatus;
        updateText();
        runIncProgress();
    }

    private void runIncProgress() {
        new Incrementer().start();
    }

    boolean incProgress() {
        if (status < maximum) {
            status++;
            progress.setValue(status);
            return false;
        }
        if (autoclose) {
            setVisible(false);
        }
        return true;
    }

    @Override
    public void setVisible(final boolean vis) {
        if (vis == isVisible()) {
            return;
        }
        super.setVisible(vis);
        if (vis) {
            new StatusModifier().start();
            SplashScreen splash = SplashScreen.getSplashScreen();
            if (splash != null) {
                splash.close();
            }
        }
    }

    final class StatusModifier extends Thread {
        StatusModifier() {
            super("JProgressSplashWindow$StatusModifier");
            setDaemon(true);
            setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void run() {
            animate();
        }
    }

    private class Incrementer extends Thread {

        public Incrementer() {
            super("JProgressSplash$Incrementer");
            setDaemon(true);
            // setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void run() {
            for (int x = 0; x <= factor; x++) {
                boolean exit = incProgress();
                if (exit || !isVisible()) {
                    return;
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ie) {
                    // Nothing to do
                }
            }
        }
    }

    protected void updateText() {
        imageLabel.repaint();
        progress.repaint();
        textLabel.setText(statusText + DOTS[number]);
    }

    protected void animate() {
        while (true) {
            for (int x = 0; x < 3; x++) {
                if (!isVisible()) {
                    return;
                }
                number = x;
                updateText();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    // Nothing to do
                }
            }
        }
    }
}