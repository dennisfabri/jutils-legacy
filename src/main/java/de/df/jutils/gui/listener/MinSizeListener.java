/*
 * Created on 03.11.2003
 */
package de.df.jutils.gui.listener;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;

/**
 * @author Dennis Mueller
 */
public final class MinSizeListener extends AComponentAdapter {

    @Override
    public void componentResized(final ComponentEvent e) {
        Component component = e.getComponent();
        int width = component.getWidth();
        int height = component.getHeight();

        // we check if either the width
        // or the height are below minimum

        boolean resize = false;

        Dimension dim = component.getMinimumSize();
        if (width < dim.width) {
            resize = true;
            width = dim.width;
        }
        if (height < dim.height) {
            resize = true;
            height = dim.height;
        }
        if (resize) {
            component.setSize(width, height);
        }
    }
}
