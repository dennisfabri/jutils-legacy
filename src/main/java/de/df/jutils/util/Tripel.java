package de.df.jutils.util;

@Deprecated
public class Tripel<A, B, C> {

    private A first;
    private B second;
    private C third;

    public Tripel() {
        this(null, null, null);
    }

    public Tripel(A f, B s, C t) {
        first = f;
        second = s;
        third = t;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    public C getThird() {
        return third;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public void setThird(C third) {
        this.third = third;
    }

    @Override
    public int hashCode() {
        if (first == null) {
            return 0;
        }
        return first.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (first == null) {
            sb.append("null");
        } else {
            String s = first.toString();
            s = s.replace("#", "\\#");
            sb.append(s);
        }
        sb.append("#");
        if (second == null) {
            sb.append("null");
        } else {
            String s = second.toString();
            s = s.replace("#", "\\#");
            sb.append(s);
        }
        sb.append("#");
        if (third == null) {
            sb.append("null");
        } else {
            String s = third.toString();
            s = s.replace("#", "\\#");
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tripel<?, ?, ?>)) {
            return false;
        }
        Tripel<?, ?, ?> t = (Tripel<?, ?, ?>) obj;
        if (!first.equals(t.getFirst())) {
            return false;
        }
        return second.equals(t.getSecond()) && third.equals(t.getThird());
    }
}