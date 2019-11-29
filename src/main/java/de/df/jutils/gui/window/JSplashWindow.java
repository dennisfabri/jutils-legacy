/*
 * Created on 22.05.2004
 */
package de.df.jutils.gui.window;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Dennis Mueller
 * @date 22.05.2004
 */
public final class JSplashWindow extends Dialog {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3689069543066513969L;
    private JLabel            imageLabel       = new JLabel();
    private JLabel            textLabel        = new JLabel(" ");
    private long              millis           = 0;

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
    public JSplashWindow(final BufferedImage image, final Color foreground, final Color background, final int steps) {
        super(new Frame());
        millis = System.currentTimeMillis();
        imageLabel.setIcon(new ImageIcon(image));

        setUndecorated(true);

        FormLayout layout = new FormLayout("4dlu,fill:default:grow,4dlu", "fill:default,4dlu,fill:default,4dlu");

        setLayout(layout);
        setBackground(background);
        setForeground(foreground);
        add(imageLabel, CC.xywh(1, 1, 3, 1));
        add(textLabel, CC.xy(2, 3));

        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        int x = (screenSize.width - windowSize.width) / 8;
        int y = (screenSize.height - windowSize.height) / 8;
        setLocation(x, y);
    }

    @Override
    public void setVisible(final boolean vis) {
        if (vis == isVisible()) {
            return;
        }
        super.setVisible(vis);
        if (!vis) {
            millis = System.currentTimeMillis() - millis;
        } else {
            SplashScreen splash = SplashScreen.getSplashScreen();
            if (splash != null) {
                splash.close();
            }
        }
    }

    /**
     * @param status
     *            statusinformation
     */
    public void setStatus(final String status) {
        textLabel.setText(status + " ...");
    }

    public long getStartupTime() {
        return millis;
    }
}