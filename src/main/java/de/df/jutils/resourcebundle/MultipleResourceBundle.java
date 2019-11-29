/*
 * Created on 29.11.2003
 */
package de.df.jutils.resourcebundle;

import java.util.*;

import de.df.jutils.data.ListEnumeration;

/**
 * @author Dennis Mueller
 */
public final class MultipleResourceBundle extends ResourceBundle {

    private LinkedList<ResourceBundle> resourceBundles = new LinkedList<ResourceBundle>();

    private boolean                    verbose         = true;

    public MultipleResourceBundle() {
        // Nothing to do
    }

    public MultipleResourceBundle(final ResourceBundle rb) {
        add(rb);
    }

    public MultipleResourceBundle(final ResourceBundle rb1, final ResourceBundle rb2) {
        this(rb1);
        add(rb2);
    }

    /*
     * (non-Javadoc)
     * @see java.util.ResourceBundle#getKeys()
     */
    @Override
    public Enumeration<String> getKeys() {
        if (resourceBundles.size() > 0) {
            LinkedList<String> result = new LinkedList<String>();
            ListIterator<ResourceBundle> li = resourceBundles.listIterator();
            while (li.hasNext()) {
                ResourceBundle rb = li.next();
                Enumeration<String> e = rb.getKeys();
                while (e.hasMoreElements()) {
                    String key = e.nextElement();
                    if (!result.contains(key)) {
                        result.addLast(key);
                    }
                }
            }
            return new ListEnumeration<String>(result.listIterator());
        }
        return null;
    }

    public void add(final ResourceBundle rb) {
        resourceBundles.addLast(rb);
    }

    public boolean remove(final ResourceBundle rb) {
        return resourceBundles.remove(rb);
    }

    /*
     * (non-Javadoc)
     * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
     */
    @Override
    protected Object handleGetObject(final String key) {
        ListIterator<ResourceBundle> li = resourceBundles.listIterator();
        while (li.hasNext()) {
            try {
                ResourceBundle rb = li.next();
                Object result = rb.getObject(key);
                if (result != null) {
                    return result;
                }
            } catch (RuntimeException re) {
                if (verbose) {
                    re.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @return
     */
    public LinkedList<ResourceBundle> getResourceBundles() {
        return resourceBundles;
    }

    /**
     * @param list
     */
    public void setResourceBundles(final LinkedList<ResourceBundle> list) {
        resourceBundles = list;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

}