/*
 * Created on 22.12.2006
 */
package de.df.jutils.gui;

import java.awt.*;

import javax.swing.JComponent;

public class JIcon extends JComponent {

    private static final long serialVersionUID = 5253477068852817430L;

    private Image             image;
    private boolean           scaleup          = true;

    private Dimension         preferred        = null;

    public JIcon(Image i, boolean scaleup) {
        image = i;
        this.scaleup = scaleup;
    }

    public JIcon(Image i) {
        this(i, true);
    }

    public JIcon() {
        this(null, true);
    }

    @Override
    public Dimension getPreferredSize() {
        if (image == null) {
            return super.getPreferredSize();
        }
        if (preferred != null) {
            return new Dimension(preferred);
        }
        return new Dimension(image.getWidth(null), image.getHeight(null));
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        preferred = new Dimension(preferredSize);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public void setImage(Image i) {
        image = i;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) {
            return;
        }

        int width = image.getWidth(null);
        int height = image.getHeight(null);

        int offsetx = 0;
        int offsety = 0;

        Graphics2D g2d = (Graphics2D) g.create();

        int iwidth = getWidth();
        int iheight = getHeight();

        // System.out.println(width + "x" + height + " - " + iwidth + "x" + iheight);

        if ((width != iwidth) || (height != iheight)) {
            double wfactor = ((double) iwidth) / (double) width;
            double hfactor = ((double) iheight) / (double) height;
            double factor = Math.min(wfactor, hfactor);

            if ((factor >= 1)) {
                if (scaleup) {
                    offsetx = (int) ((iwidth - (factor * image.getWidth(null))) / 2);
                    offsety = (int) ((iheight - (factor * image.getHeight(null))) / 2);

                } else {
                    offsetx = (iwidth - image.getWidth(null)) / 2;
                    offsety = (iheight - image.getHeight(null)) / 2;
                    factor = 1;
                }
            } else {
                offsetx = (int) ((iwidth - (factor * image.getWidth(null))) / 2);
                offsety = (int) ((iheight - (factor * image.getHeight(null))) / 2);

            }
            g2d.translate(offsetx, offsety);
            g2d.scale(factor, factor);

            // System.out.println(" ->" + factor + " -- " + offsetx + "/" + offsety);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(image, 0, 0, width, height, null);

    }
}