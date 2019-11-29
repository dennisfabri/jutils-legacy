package net.java.swingfx.waitwithstyle;

import java.awt.event.ActionListener;

import javax.swing.JComponent;

/**
 * This is a modified version of sources found in the swingfx-project:
 * A common interface that the CancelableProgressAdapter can use to hook into.
 * <p>
 * There are multiple panels that handle progress, the CancelableProgressAdapter
 * can hook into any panel that implements this interface.
 * 
 * @author Michael Bushe michael@bushe.com
 */
interface CancelableAdaptee {
    /**
     * Starts the animation.
     */
    void start();

    /**
     * Stops the animation.
     */
    void stop();

    /**
     * Sets the text in the animation
     */
    void setText(String text);

    /**
     * Gets the interface as a JComponent (usually returns "this")
     */
    JComponent getComponent();

    /**
     * Adds a listener to the cancel button. Usually delegated to the
     * CancelableProgressAdapter.
     */
    void addCancelListener(ActionListener listener);

    /**
     * Removes a listener from the cancel button. Usually delegated to the
     * CancelableProgressAdapter.
     */
    void removeCancelListener(ActionListener listener);
}