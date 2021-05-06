package de.df.jutils.gui.renderer;

import javax.swing.Icon;

/**
 * Based on an example from www.java2s.com
 */
public class TextWithIcon {

    private final String data;
    private final Icon   icon;

    public TextWithIcon(String data, Icon icon) {
        if (data != null) {
            data = data.replace(' ', '\u00a0');
        }
        this.data = data;
        this.icon = icon;
    }

    public String getData() {
        return data;
    }

    public Icon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return (data == null ? "" : data);
    }
}