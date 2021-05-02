/*
 * Created on 23.02.2005
 */
package de.df.jutils.gui.window;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;

import javax.swing.JDialog;

import de.df.jutils.gui.JGlassPane;

public class JGlassDialog extends JDialog {

    private static final long serialVersionUID = 4050486728878141494L;

    private JGlassPane        glass            = new JGlassPane(this);

    public JGlassDialog() {
        super();
    }

    public JGlassDialog(Frame arg0) {
        super(arg0);
    }

    public JGlassDialog(Frame arg0, boolean arg1) {
        super(arg0, arg1);
    }

    public JGlassDialog(Frame arg0, String arg1) {
        super(arg0, arg1);
    }

    public JGlassDialog(Frame arg0, String arg1, boolean arg2) {
        super(arg0, arg1, arg2);
    }

    public JGlassDialog(Frame arg0, String arg1, boolean arg2, GraphicsConfiguration arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public JGlassDialog(Dialog arg0) {
        super(arg0);
    }

    public JGlassDialog(Dialog arg0, boolean arg1) {
        super(arg0, arg1);
    }

    public JGlassDialog(Dialog arg0, String arg1) {
        super(arg0, arg1);
    }

    public JGlassDialog(Dialog arg0, String arg1, boolean arg2) {
        super(arg0, arg1, arg2);
    }

    public JGlassDialog(Dialog arg0, String arg1, boolean arg2, GraphicsConfiguration arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        glass.setVisible(!enabled);
    }
}