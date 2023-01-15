package de.df.jutils.gui.util;

@Deprecated
public class NullCallback<T extends Object> implements ISimpleCallback<T> {

    @Override
    public void callback(T t) {
        // Nothing to do
    }
}
