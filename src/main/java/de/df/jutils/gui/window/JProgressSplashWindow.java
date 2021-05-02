/*
 * Created on 22.05.2004
 */
package de.df.jutils.gui.window;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Dennis Mueller
 * @date 22.05.2004
 */
public final class JProgressSplashWindow extends Dialog {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long    serialVersionUID = 3834593201360154935L;

    public static final String[] DOTS             = new String[] { " .", " ..", " ..." };

    private int[][]              image            = null;
    private JLabel               imageLabel       = new JLabel();
    protected JLabel             textLabel        = new JLabel("");
    private JProgressBar         progress         = new JProgressBar(0, 10);
    private int                  status           = -1;
    protected int                factor           = 0;
    private int                  maximum          = 0;
    protected String             statusText       = "";
    private int                  width            = 0;
    private int                  height           = 0;
    private long                 millis           = 0;
    private boolean              animateImage     = true;
    private int                  j;
    private int                  number           = 0;

    /**
     * @param icon
     *            mainimage
     * @param foreground
     *            foregroundcolor
     * @param background
     *            backgroundcolor
     * @param steps
     *            count of statusmessages
     * @throws java.awt.HeadlessException
     *             see JDialog
     */
    public JProgressSplashWindow(final BufferedImage bufferedImage, final boolean animate, final int steps) {
        super(new Frame());
        this.animateImage = animate;
        millis = System.currentTimeMillis();
        setImage(bufferedImage);
        factor = 100 / steps;
        maximum = steps * factor - 1;
        progress.setMaximum(maximum);

        setUndecorated(true);

        FormLayout layout = new FormLayout("4dlu,fill:default:grow,4dlu,fill:100dlu,4dlu", "fill:default,4dlu,fill:default,4dlu");

        setLayout(layout);
        add(imageLabel, CC.xywh(1, 1, 5, 1));
        add(textLabel, CC.xy(2, 3));
        add(progress, CC.xy(4, 3));

        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        int x = (screenSize.width - windowSize.width) / 8;
        int y = (screenSize.height - windowSize.height) / 8;
        setLocation(x, y);
    }

    private BufferedImage colorScale(final int percent) {
        int per = percent;
        if (per < 0) {
            per = 0;
        } else {
            if (per > 100) {
                per = 100;
            }
        }
        int contra = 100 - per;
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int xy = (y * width) + x;
                int mc = (image[xy][3] * contra);
                int r = (mc + (image[xy][0] * per)) / 100;
                int g = (mc + (image[xy][1] * per)) / 100;
                int b = (mc + (image[xy][2] * per)) / 100;
                dest.setRGB(x, y, (r << 16) + (g << 8) + b);
            }
        }
        return dest;
    }

    private void setImage(final BufferedImage i) {
        if (animateImage) {
            width = i.getWidth();
            height = i.getHeight();
            image = new int[width * height][4];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    j = (y * width) + x;
                    int rgb = (i.getRGB(x, y)) & 16777215;
                    image[j][0] = ((rgb >> 16) & 255);
                    image[j][1] = ((rgb >> 8) & 255);
                    image[j][2] = (rgb & 255);
                    image[j][3] = (image[j][0] + image[j][1] + image[j][2]) / 3;
                }
            }
            setImage(0);
        } else {
            imageLabel.setIcon(new ImageIcon(i));
        }
    }

    private void setImage(final int percent) {
        if (animateImage) {
            imageLabel.setIcon(new ImageIcon(colorScale(percent)));
        }
    }

    /**
     * @param status
     *            statusinformation
     */
    public void setStatus(final String newStatus) {
        setVisible(true);
        statusText = newStatus;
        updateText();
        runIncProgress();
    }

    private void runIncProgress() {
        new Thread(new Incrementer(), "JProgressSplash$Incrementer").start();
    }

    boolean incProgress() {
        if (status < maximum) {
            status++;
            if ((status % 16) == 0) {
                setImage(100 * status / (maximum - 1));
            }
            progress.setValue(status);
            return false;
        }
        setVisible(false);
        return true;
    }

    @Override
    public void setVisible(final boolean vis) {
        if (vis == isVisible()) {
            return;
        }
        super.setVisible(vis);
        if (vis) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    animate();
                }
            }, "JProgressSplashWindow$StatusModifier").start();
        } else {
            millis = System.currentTimeMillis() - millis;
        }
    }

    public long getStartupTime() {
        return millis;
    }

    class Incrementer implements Runnable {

        @Override
        public void run() {
            for (int x = 0; x < factor; x++) {
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