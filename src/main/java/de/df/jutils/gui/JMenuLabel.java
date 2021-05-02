/**
 * Based on
 * http://weblogs.java.net/blog/kirillcool/archive/2005/03/how_to_create_c.html
 */
package de.df.jutils.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;

@Deprecated
public class JMenuLabel extends JMenuItem {

    private static final long serialVersionUID = 3257569499002911026L;

    private Color             right            = null;

    public JMenuLabel() {
        super("");
        setEnabled(false);
    }

    public JMenuLabel(String text, Color background) {
        this(text);
        setRightColor(background);
    }

    public void setRightColor(Color c) {
        right = c;
    }

    public Color getRightColor() {
        return right;
    }

    public JMenuLabel(String text) {
        super(text);
        setEnabled(false);
    }

    public static final int CUBE_DIMENSION = 5;

    public static BufferedImage getGradientCubesImage(int width, int height, Color leftColor, Color rightColor, int transitionStart, int transitionEnd) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint(transitionStart, 0, leftColor, transitionEnd, 0, rightColor);
        graphics.setPaint(gradient);
        graphics.fillRect(transitionStart, 0, transitionEnd - transitionStart, height);

        graphics.setColor(leftColor);
        graphics.fillRect(0, 0, transitionStart, height);

        graphics.setColor(rightColor);
        graphics.fillRect(transitionEnd, 0, width - transitionEnd, height);

        int cubeCountY = height / CUBE_DIMENSION;
        int cubeCountX = 1 + (transitionEnd - transitionStart) / CUBE_DIMENSION;
        int cubeStartY = (height % CUBE_DIMENSION) / 2;
        int cubeStartX = transitionStart - (CUBE_DIMENSION - ((transitionEnd - transitionStart) % CUBE_DIMENSION));

        for (int col = 0; col < cubeCountX; col++) {
            for (int row = 0; row < cubeCountY; row++) {
                // decide if we should put a cube
                if (Math.random() < 0.5) {
                    continue;
                }
                // Make semi-random choice of color. It should lie
                // close to the interpolated color, but still appear
                // random
                double coef = 1.0 - (((double) col / (double) cubeCountX) + 0.9 * (Math.random() - 0.5));
                coef = Math.max(0.0, coef);
                coef = Math.min(1.0, coef);
                // Compute RGB components
                int r = (int) (coef * leftColor.getRed() + (1.0 - coef) * rightColor.getRed());
                int g = (int) (coef * leftColor.getGreen() + (1.0 - coef) * rightColor.getGreen());
                int b = (int) (coef * leftColor.getBlue() + (1.0 - coef) * rightColor.getBlue());
                // fill cube
                graphics.setColor(new Color(r, g, b));
                graphics.fillRect(cubeStartX + col * CUBE_DIMENSION, cubeStartY + row * CUBE_DIMENSION, CUBE_DIMENSION, CUBE_DIMENSION);
                // draw cube's border in slightly brighter color
                graphics.setColor(new Color(255 - (int) (0.95 * (255 - r)), 255 - (int) (0.9 * (255 - g)), 255 - (int) (0.9 * (255 - b))));
                graphics.drawRect(cubeStartX + col * CUBE_DIMENSION, cubeStartY + row * CUBE_DIMENSION, CUBE_DIMENSION, CUBE_DIMENSION);
            }
        }
        return image;
    }

    @Override
    protected final void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        Color mainMidColor = getBackground();
        Color mainUltraDarkColor = right;
        if (right == null) {
            mainUltraDarkColor = mainMidColor.darker().darker();
        }

        Object oldHint = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // paint background
        // image
        graphics.drawImage(getGradientCubesImage(this.getWidth(), this.getHeight(), mainMidColor, mainUltraDarkColor, (int) (0.7 * this.getWidth()),
                (int) (0.9 * this.getWidth())), 0, 0, null);
        // of the center
        int x = Math.min(10, this.getWidth() - graphics.getFontMetrics().stringWidth(this.getText()));
        int y = (int) (graphics.getFontMetrics().getLineMetrics(this.getText(), graphics).getHeight()); // paint the text with black

        // shadow
        Color text = getForeground();
        Color background = new Color(255 - text.getRed(), 255 - text.getGreen(), 255 - text.getBlue());
        graphics.setColor(background);
        graphics.drawString(getText(), x + 1, y + 1);
        graphics.setColor(text);
        graphics.drawString(getText(), x, y);

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, oldHint);
    }
}