
package de.df.jutils.gui.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Class to draw an Icon Border around the JComponent
 * 
 * <pre>
 *           --------------------
 *           |                * |
 *           --------------------
 *   where * is the position of the icon
 * </pre>
 * 
 * @version 1b
 * @author Naveed Quadri
 */
public class IconBorder extends AbstractBorder {

    private static final long serialVersionUID = -4478916266780178839L;

    /**
     * icon to draw
     */
    private ImageIcon icon;

    private Image smallImage;

    /**
     * The actual border of the component. Draws any component decorations you may
     * have and then adds the icon
     */
    private Border originalBorder;

    /**
     * Creates an Icon Border with the specified icon
     * 
     * @param icon           icon to draw
     * @param originalBorder The actual border of the component. Draws any component
     *                       decorations you may have and then adds the icon
     */
    public IconBorder(ImageIcon icon, Border originalBorder) {
        this.icon = icon;
        this.originalBorder = originalBorder != null ? originalBorder : new EmptyBorder(0, 0, 0, 0);
    }

    /**
     * Reads the icon from the specified URL and creates an Icon Border from the
     * read Icon
     * 
     * @param imageURL
     * @param originalBorder
     */
    public IconBorder(URL imageURL, Border originalBorder) {
        this(new ImageIcon(java.awt.Toolkit.getDefaultToolkit().createImage(imageURL)), originalBorder);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (originalBorder != null) {
            originalBorder.paintBorder(c, g, x, y, width, height);
        }
        drawIconToBottomLeft(c, g, x, y, width, height);
    }

    private void drawIconToBottomLeft(Component c, Graphics g, int x, int y, int width, int height) {
        if (icon != null) {
            int size = height * 2 / 3;
            if (smallImage == null || (icon.getIconHeight() > size && size != smallImage.getHeight(null))) {
                smallImage = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            }

            int dx = 0;
            int dy = c.getHeight() - smallImage.getHeight(null);
            g.drawImage(smallImage, dx + x, dy + y, null);
        }
    }

    /**
     * Returns the insets of the border.
     * 
     * @param c the component for which this border insets value applies
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return originalBorder.getBorderInsets(c);
    }

    /**
     * Returns whether or not the border is opaque.
     */
    @Override
    public boolean isBorderOpaque() {
        return false;
    }

}
