/*
 * Created on 23.02.2005
 */
package de.df.jutils.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Based on the article "Create stylish glass pane on long GUI-blocking
 * operations"published at:
 * http://jroller.com/page/javaproxy/20050221#create_stylish_glass_pane_on
 * 
 * @author Kirill
 * @date 23.02.2005
 */
public class JGlassPane extends JPanel {

    private static final long serialVersionUID = 3905246744735462710L;

    protected int             opacity          = 0;
    private boolean           animate          = false;

    public JGlassPane() {
        setOpaque(false);
        setVisible(false);
    }

    public JGlassPane(JFrame parent) {
        this();
        parent.setGlassPane(this);
    }

    public JGlassPane(JDialog parent) {
        this();
        parent.setGlassPane(this);
    }

    void setAnimated(boolean animate) {
        this.animate = animate;
    }

    @Override
    public void setVisible(boolean visible) {
        if (animate) {
            opacity = 0;
            super.setVisible(visible);
            if (visible) {
                Thread worker = new Thread() {
                    @Override
                    public void run() {
                        for (int x = 10; x <= 120; x += 10) {
                            try {
                                Thread.sleep(40);
                            } catch (InterruptedException e) {
                                // Nothing to do;
                            }
                            opacity = x;
                            repaint();
                        }
                    }
                };
                worker.start();
            }
        } else {
            opacity = 120;
            super.setVisible(visible);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color midColor = getBackground();
        Color darkColor = midColor.darker();
        Color lightColor = midColor.brighter();
        Color ultralightColor = lightColor.brighter();

        int height = getHeight();
        int width = getWidth();

        Graphics2D graphics = (Graphics2D) g;
        BufferedImage oddLine = createGradientLine(width, lightColor, darkColor, opacity);
        BufferedImage evenLine = createGradientLine(width, ultralightColor, midColor, opacity);

        for (int row = 0; row < height; row += 2) {
            graphics.drawImage(evenLine, 0, row, null);
        }
        for (int row = 1; row < height; row += 2) {
            graphics.drawImage(oddLine, 0, row, null);
        }
    }

    public static BufferedImage createGradientLine(int width, Color leftColor, Color rightColor, int iOpacity) {
        BufferedImage image = new BufferedImage(width, 1, BufferedImage.TYPE_INT_ARGB);

        for (int col = 0; col < width; col++) {
            double coef = (double) col / (double) width;
            int r = (int) (leftColor.getRed() + coef * (rightColor.getRed() - leftColor.getRed()));
            int g = (int) (leftColor.getGreen() + coef * (rightColor.getGreen() - leftColor.getGreen()));
            int b = (int) (leftColor.getBlue() + coef * (rightColor.getBlue() - leftColor.getBlue()));

            int color = (iOpacity << 24) | (r << 16) | (g << 8) | b;
            image.setRGB(col, 0, color);
        }
        return image;
    }
}