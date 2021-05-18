/*
 * Created on 21.11.2004
 */
package de.df.jutils.plugin.topsort;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author Dennis Fabri
 * @date 21.11.2004
 */
public class Node<T> {

    private T                          data;
    private int                        indegree = 0;
    private Hashtable<String, Node<T>> nodes    = new Hashtable<String, Node<T>>();

    public Node(T d) {
        data = d;
    }

    public void addEdge(Node<T> n) {
        removeEdge(n);
        nodes.put(n.toString(), n);
        n.indegree++;
    }

    public Node<T> removeEdge(Node<T> n) {
        n = nodes.get(n.toString());
        if (n != null) {
            n.indegree--;
            nodes.remove(n.toString());
            if (n.indegree == 0) {
                return n;
            }
        }
        return null;
    }

    public Enumeration<Node<T>> getNodes() {
        return nodes.elements();
    }

    public int getIndegree() {
        return indegree;
    }

    public T getData() {
        return data;
    }
}
