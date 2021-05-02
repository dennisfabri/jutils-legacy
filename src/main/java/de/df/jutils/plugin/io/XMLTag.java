/*
 * Tag.java Created on 2. March 2002, 14:35
 */

package de.df.jutils.plugin.io;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author dennis
 */
class XMLTag {

    public static final String[] NAMEN       = { "Nichts", "Anfang", "Ende  ", "Toggle", "Other ", "Info" };

    public static final int      TAG_NOTHING = 0;
    public static final int      TAG_ANFANG  = 1;
    public static final int      TAG_ENDE    = 2;
    public static final int      TAG_TOGGLE  = 3;
    public static final int      TAG_OTHER   = 4;
    public static final int      TAG_INFO    = 5;
    public static final int      TAG_MAX     = 5;

    private final int            tagID;

    private final Object         daten;

    /** Creates a new instance of Tag */

    public XMLTag(int id, Object o) {
        if ((id < 1) || (id > TAG_MAX)) {
            id = 0;
        }
        tagID = id;
        daten = o;
    }

    @Override
    public String toString() {
        return "" + NAMEN[tagID] + ": " + getDetailedInfo(daten);
    }

    /**
     * @return Returns the daten.
     */
    public Object getDaten() {
        return daten;
    }

    /**
     * @return Returns the tagID.
     */
    public int getTagID() {
        return tagID;
    }

    @SuppressWarnings("unchecked")
    private static String getDetailedInfo(final Object o) {
        if (o == null) {
            return "null";
        }
        if (o instanceof LinkedList) {
            int zahl = 0;
            LinkedList<Object> ll = ((LinkedList<Object>) o);
            ListIterator<Object> li = ll.listIterator();
            StringBuilder sb = new StringBuilder();
            sb.append("LinkedList[");
            sb.append(ll.size());
            sb.append("]:");
            while (li.hasNext()) {
                sb.append("(");
                sb.append(zahl);
                sb.append(":");
                sb.append(getDetailedInfo(li.next()));
                sb.append(")");
                zahl++;
            }
            return sb.toString();
        }
        if (o instanceof String) {
            return o.toString();
        }
        if (o instanceof Object[]) {
            Object[] oa = (Object[]) o;
            StringBuilder sb = new StringBuilder();
            sb.append("Array[");
            sb.append(oa.length);
            sb.append("]:");
            for (int x = 0; x < oa.length; x++) {
                sb.append("[");
                sb.append(x);
                sb.append(":");
                sb.append(getDetailedInfo(oa[x]));
                sb.append("]");
            }
            return sb.toString();
        }
        return o.getClass().toString() + ":" + o.toString();
    }
}