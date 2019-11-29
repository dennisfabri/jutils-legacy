package de.df.jutils.data;

import java.util.Hashtable;
import java.util.LinkedList;

public final class HashtableUtils {

    private HashtableUtils() {
        // Hide constructor
    }

    public static <T, S> LinkedList<T> getKeys(Hashtable<T, S> data) {
        LinkedList<T> keys = new LinkedList<T>();
        Iterable<T> i = new EnumerationIterable<T>(data.keys());
        for (T t : i) {
            keys.add(t);
        }
        return keys;
    }
}
