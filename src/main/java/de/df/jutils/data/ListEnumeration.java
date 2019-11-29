/*
 * Created on 12.12.2003
 */
package de.df.jutils.data;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * This class uses a java.util.Iterator to simulate a java.util.Enumeration
 * 
 * @author Dennis
 */
public final class ListEnumeration<T extends Object> implements Enumeration<T> {

    /**
     * This is used the <code>iterator</code>.
     */
    private Iterator<T> iterator;

    /**
     * This constructor stores the passed Iterator so that it can be used to
     * simulate the Enumeration.
     * 
     * @param iter
     *            The Iterator used to simulate the Enumeration.
     * @throws NullPointerException
     *             A NullPointerException is thrown if the passed Iterator is
     *             null.
     */
    public ListEnumeration(final Iterator<T> iter) {
        if (iter == null) {
            throw new NullPointerException();
        }
        iterator = iter;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Enumeration#hasMoreElements()
     */
    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    /*
     * (non-Javadoc)
     * @see java.util.Enumeration#nextElement()
     */
    @Override
    public T nextElement() {
        return iterator.next();
    }

    /**
     * @return
     */
    public Iterator<T> getIterator() {
        return iterator;
    }

    /**
     * @param iterator
     */
    public void setIterator(final Iterator<T> iter) {
        iterator = iter;
    }

}
