/*
 * Created on 21.11.2004
 */
package de.df.jutils.topsort;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author Dennis Mueller
 * @date 21.11.2004
 */
public final class TopSort {

    private TopSort() {
        // Not used
    }

    public static <T> LinkedList<Node<T>> sort(LinkedList<Node<T>> nodes) {
        if (nodes == null) {
            throw new NullPointerException("Argument must not be null");
        }
        LinkedList<Node<T>> result = new LinkedList<Node<T>>();
        if (nodes.size() == 0) {
            return result;
        }
        LinkedList<Node<T>> queue = new LinkedList<Node<T>>();
        ListIterator<Node<T>> li = nodes.listIterator();
        while (li.hasNext()) {
            Node<T> node = li.next();
            if (node.getIndegree() == 0) {
                queue.addLast(node);
            }
        }
        while (queue.size() > 0) {
            Node<T> node = queue.removeFirst();
            result.addFirst(node);
            Enumeration<Node<T>> e = node.getNodes();
            while (e.hasMoreElements()) {
                Node<T> n = e.nextElement();
                node.removeEdge(n);
                if (n.getIndegree() == 0) {
                    queue.addLast(n);
                }
            }
        }
        return result;
    }
}
