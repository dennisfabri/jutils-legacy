/*
 * Created on 01.03.2005
 */
package de.df.jutils.gui;

import java.awt.Component;

import javax.swing.JLayeredPane;

import net.java.swingfx.waitwithstyle.InfiniteProgressPanel;
import net.java.swingfx.waitwithstyle.InfiniteProgressUtils;

class JInfiniteProgressPane extends JLayeredPane {

    private static final long     serialVersionUID = 3258128068173312306L;

    private InfiniteProgressPanel glass;
    private Component             component        = null;

    public JInfiniteProgressPane(Component c) {
        component = c;

        glass = InfiniteProgressUtils.createPanel();
        glass.setVisible(false);

        add(component, 0);
        add(glass, Integer.MAX_VALUE);
        setEnabled(true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) {
            return;
        }
        component.setEnabled(enabled);
        super.setEnabled(enabled);
        if (enabled) {
            glass.interrupt();
        } else {
            glass.start();
        }
    }

    public InfiniteProgressPanel getGlassPanel() {
        return glass;
    }
}