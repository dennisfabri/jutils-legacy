/*
 * Created on 27.09.2005
 */
package de.df.jutils.gui.awt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

class ImageComponent extends Component {

    private static final long serialVersionUID = -189175844670997641L;

    private Image             image;

    public ImageComponent(Image i) {
        image = i;
    }

    public void setImage(Image i) {
        image = i;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (image == null) {
            return super.getPreferredSize();
        }
        return new Dimension(image.getWidth(null), image.getHeight(null));
    }

    @Override
    public void paint(Graphics g) {
        // super.paint(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getBackground(), null);
        }
    }
}