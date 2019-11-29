/*
 * Created on 03.02.2006
 */
package de.df.jutils.gui.util;

import java.util.Hashtable;

import javax.swing.ImageIcon;

public abstract class AIconBundle {

    public static final int              SMALL  = 16;
    public static final int              MEDIUM = 32;
    public static final int              LARGE  = 48;

    private Hashtable<String, ImageIcon> icons;

    public AIconBundle() {
        icons = new Hashtable<String, ImageIcon>();
    }

    protected abstract ImageIcon readIcon(String name, int size);

    public final ImageIcon getIcon(String name, int size) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (size <= 0) {
            throw new IllegalArgumentException();
        }
        synchronized (this) {
            ImageIcon i = icons.get(name + "x" + size);
            if (i == null) {
                i = readIcon(name, size);
                if (i != null) {
                    icons.put(name + "x" + size, i);
                }
            }
            return i;
        }
    }

    public final ImageIcon getSmallIcon(String name) {
        return getIcon(name, SMALL);
    }

    public final ImageIcon getMediumIcon(String name) {
        return getIcon(name, MEDIUM);
    }

    public final ImageIcon getLargeIcon(String name) {
        return getIcon(name, LARGE);
    }
}