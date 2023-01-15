/*
 * Created on 21.11.2004
 */
package de.df.jutils.plugin;

import javax.swing.JComponent;

/**
 * @author Dennis Fabri
 * @date 21.11.2004
 */
public class ButtonInfo implements Comparable<ButtonInfo> {

    private int priotity;
    private JComponent[] buttons;

    public ButtonInfo(JComponent[] p, int prio) {
        buttons = p;
        priotity = prio;
    }

    public ButtonInfo(JComponent b, int prio) {
        this(new JComponent[] { b }, prio);
    }

    public int getPriotity() {
        return priotity;
    }

    public JComponent[] getButtons() {
        return buttons;
    }

    @Override
    public int hashCode() {
        return priotity;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ButtonInfo && compareTo((ButtonInfo) obj) == 0;
    }

    @Override
    public int compareTo(ButtonInfo pi) {
        return priotity - pi.priotity;
    }
}
