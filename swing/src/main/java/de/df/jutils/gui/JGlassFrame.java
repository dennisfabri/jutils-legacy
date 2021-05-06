/*
 * Created on 23.02.2005
 */
package de.df.jutils.gui;

import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;

public class JGlassFrame extends JFrame {

    private static final long serialVersionUID = 3257564027247736368L;

    private JGlassPane        glass            = new JGlassPane(this);

    public JGlassFrame() {
        super();
    }

    public JGlassFrame(GraphicsConfiguration arg0) {
        super(arg0);
    }

    public JGlassFrame(String arg0) {
        super(arg0);
    }

    public JGlassFrame(String arg0, GraphicsConfiguration arg1) {
        super(arg0, arg1);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        glass.setVisible(!enabled);
    }
}