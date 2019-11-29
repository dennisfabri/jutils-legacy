package de.df.jutils.data;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterable<T> implements Iterable<T> {

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