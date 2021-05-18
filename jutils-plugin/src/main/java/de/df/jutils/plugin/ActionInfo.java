/*
 * Created on 21.11.2004
 */
package de.df.jutils.plugin;

/**
 * @author Dennis Fabri
 * @date 21.11.2004
 */
public class ActionInfo {

    private final Runnable action;
    private final int      key;
    private final int      modifiers;

    public ActionInfo(int key, int modifiers, Runnable action) {
        this.key = key;
        this.modifiers = modifiers;
        this.action = action;
    }

    public int getKey() {
        return key;
    }

    public Runnable getAction() {
        return action;
    }

    public int getModifiers() {
        return modifiers;
    }
}