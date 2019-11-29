package de.df.jutils.data;

import java.util.Comparator;

public class CompoundComparator<T> implements Comparator<T> {

    private final Comparator<T>[] c;

    @SuppressWarnings("unchecked")
    public CompoundComparator() {
        c = new Comparator[0];
    }

    @SafeVarargs
    public CompoundComparator(Comparator<T>... cs) {
        this.c = cs;
    }

    @Override
    public int compare(T o1, T o2) {
        for (int x = 0; x < c.length; x++) {
            int value = c[x].compare(o1, o2);
            if (value != 0) {
                return value;
            }
        }
        return 0;
    }
}
