/*
 * Created on 08.12.2003
 */
package de.df.jutils.gui.listener;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * @author Dennis
 */
public abstract class AComponentAdapter implements ComponentListener {

    @Override
    public void componentHidden(final ComponentEvent arg0) {
        // Nothing to do
    }

    @Override
    public void componentMoved(final ComponentEvent arg0) {
        // Nothing to do
    }

    @Override
    public void componentResized(final ComponentEvent arg0) {
        // Nothing to do
    }

    @Override
    public void componentShown(final ComponentEvent arg0) {
        // Nothing to do
    }

}
