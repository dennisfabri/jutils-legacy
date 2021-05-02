package de.df.jutils.data;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class HashtableUtils {

    private HashtableUtils() {
        // Hide constructor
    }

    public static <T, S> List<T> getKeys(Hashtable<T, S> data) {
        LinkedList<T> keys = new LinkedList<>();
        for (T t : getKeyIterable(data)) {
            keys.add(t);
        }
        return keys;
    }
    
    public static <T, S> Iterable<T> getKeyIterable(Hashtable<T, S> data) {
        return new EnumerationIterable<T>(data.keys());
    }
    
    private static class EnumerationIterable<T> implements Iterable<T> {

        Enumeration<T> en;

        public EnumerationIterable(Enumeration<T> e) {
            en = e;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return en.hasMoreElements();
                }

                @Override
                public T next() {
                    return en.nextElement();
                }

                @Override
                public void remove() {
                    throw new IllegalAccessError("remove not allowed");
                }
            };
        }

    }
}
