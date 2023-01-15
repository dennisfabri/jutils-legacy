/*
 * Created on 17.02.2005
 */
package de.df.jutils.gui;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author Dennis Fabri
 */
public class JTransparentButton extends JButton {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3546358414209987128L;

    private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);

    /**
     * 
     */
    public JTransparentButton() {
        super();
        changeToTransparent();
    }

    /**
     * @param arg0
     */
    public JTransparentButton(Icon arg0) {
        super(arg0);
        changeToTransparent();
    }

    /**
     * @param arg0
     */
    public JTransparentButton(String arg0) {
        super(arg0);
        changeToTransparent();
    }

    /**
     * @param arg0
     */
    public JTransparentButton(Action arg0) {
        super(arg0);
        changeToTransparent();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public JTransparentButton(String arg0, Icon arg1) {
        super(arg0, arg1);
        changeToTransparent();
    }

    public void changeToTransparent() {
        changeToTransparent(this);
    }

    public static void changeToTransparent(JButton button) {
        button.setMargin(NO_INSETS);
        button.setBorder(null);
        button.setContentAreaFilled(false);
    }
}
