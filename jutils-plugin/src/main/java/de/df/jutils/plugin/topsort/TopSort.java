/*
 * Created on 21.11.2004
 */
package de.df.jutils.plugin.topsort;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Dennis Fabri
 * @date 21.11.2004
 */
public final class TopSort {

    private TopSort() {
        // Not used
    }

    public static <T> List<Node<T>> sort(List<Node<T>> nodes) {
        if (nodes == null) {
            throw new NullPointerException("Argument must not be null");
        }
        LinkedList<Node<T>> result = new LinkedList<>();
        if (nodes.isEmpty()) {
            return result;
        }
        LinkedList<Node<T>> queue = new LinkedList<>();
        ListIterator<Node<T>> li = nodes.listIterator();
        while (li.hasNext()) {
            Node<T> node = li.next();
            if (node.getIndegree() == 0) {
                queue.addLast(node);
            }
        }
        while (!queue.isEmpty()) {
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
