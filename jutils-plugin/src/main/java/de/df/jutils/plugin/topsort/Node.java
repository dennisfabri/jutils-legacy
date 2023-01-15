/*
 * Created on 21.11.2004
 */
package de.df.jutils.plugin.topsort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Node<T> {

    private T data;
    private int indegree;
    private Map<String, Node<T>> nodes = new HashMap<>();

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

    public Collection<Node<T>> getNodes() {
        return new ArrayList<>(nodes.values());
    }

    public int getIndegree() {
        return indegree;
    }

    public T getData() {
        return data;
    }
}
