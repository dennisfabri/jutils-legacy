package de.df.jutils.util;

@Deprecated
public class Tupel<A, B> {

    private final A first;
    private final B second;

    public Tupel() {
        this(null, null);
    }

    public Tupel(A f, B s) {
        first = f;
        second = s;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (first == null) {
            sb.append("null");
        } else {
            String s = first.toString();
            s = s.replace("|", "||");
            sb.append(s);
        }
        sb.append("|");
        if (second == null) {
            sb.append("null");
        } else {
            String s = second.toString();
            s = s.replace("|", "||");
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        if (first == null) {
            return 0;
        }
        return first.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tupel<?, ?>)) {
            return false;
        }
        Tupel<?, ?> t = (Tupel<?, ?>) obj;
        if (first == null && t.getFirst() != null) {
            return false;
        }
        if (second == null && t.getSecond() != null) {
            return false;
        }
        return first.equals(t.getFirst()) && second.equals(t.getSecond());
    }
}