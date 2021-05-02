package de.df.jutils.data;

import java.io.Serializable;

@Deprecated
public class Triplet<A, B, C> implements Serializable, Comparable<Triplet<A, B, C>> {

    public final A Item1;
    public final B Item2;
    public final C Item3;

    @Deprecated
    public Triplet(A a, B b, C c) {
        Item1 = a;
        Item2 = b;
        Item3 = c;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private int compareToI(Object o1, Object o2) {
        Comparable c1 = null;
        Comparable c2 = null;
        if (o1 instanceof Comparable) {
            c1 = (Comparable<?>) o1;
        }
        if (o2 instanceof Comparable) {
            c2 = (Comparable<?>) o2;
        }
        if (c1 == null && c2 == null) {
            return 0;
        }
        if (c1 != null && c2 == null) {
            return -1;
        }
        if (c1 == null) {
            return 1;
        }
        int result = c1.compareTo(c2);
        if (result < 0) {
            return -1;
        }
        if (result > 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public int compareTo(Triplet<A, B, C> o2) {
        if (o2 == null) {
            return -1;
        }

        int result = compareToI(Item1, o2.Item1);
        if (result != 0) {
            return result;
        }
        result = compareToI(Item2, o2.Item2);
        if (result != 0) {
            return result;
        }
        result = compareToI(Item3, o2.Item3);
        return result;
    }
}
