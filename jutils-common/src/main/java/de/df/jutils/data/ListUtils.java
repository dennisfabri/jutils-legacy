/*
 * Created on 20.01.2006
 */
package de.df.jutils.data;

import java.util.LinkedList;
import java.util.ListIterator;

public final class ListUtils {

    private ListUtils() {
        // Hide
    }

    public static <T> void addAll(final LinkedList<T> list, T[] data) {
        for (T t : data) {
            if (!list.contains(t)) {
                list.addLast(t);
            }
        }
    }

    public static <T> void addAll(final LinkedList<T> list, LinkedList<T> data) {
        for (T t : data) {
            if (!list.contains(t)) {
                list.addLast(t);
            }
        }
    }

    public static <T> LinkedList<T> mergeLists(final LinkedList<T>[] lsi) {
        LinkedList<T> ll = new LinkedList<T>();
        for (LinkedList<T> aLsi : lsi) {
            ll.addAll(aLsi);
        }
        return ll;
    }

    @SuppressWarnings("unchecked")
    public static <T> LinkedList<T>[] split(final LinkedList<T> list, int index) {
        if ((index < 0) || (index > list.size())) {
            throw new IllegalArgumentException("Index to high or to low. Should be between 0 and " + list.size() + " and was " + index + ".");
        }
        LinkedList<T>[] listen = new LinkedList[2];
        for (int x = 0; x < 2; x++) {
            listen[x] = new LinkedList<T>();
        }

        int pos = 0;
        int counter = 0;
        ListIterator<T> li = list.listIterator();
        while (li.hasNext()) {
            if (counter >= index) {
                pos = 1;
            }
            listen[pos].addLast(li.next());
            counter++;
        }

        return listen;
    }
}
