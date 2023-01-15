/*
 * Created on 29.11.2003
 */
package de.df.jutils.resourcebundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

/**
 * @author Dennis Fabri
 */
public final class MultipleResourceBundle extends ResourceBundle {

    private LinkedList<ResourceBundle> resourceBundles = new LinkedList<>();

    private boolean verbose = true;

    public MultipleResourceBundle() {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ResourceBundle#getKeys()
     */
    @Override
    public Enumeration<String> getKeys() {
        if (resourceBundles.isEmpty()) {
            return null;
        }

        List<String> result = new ArrayList<>();
        ListIterator<ResourceBundle> li = resourceBundles.listIterator();
        while (li.hasNext()) {
            ResourceBundle rb = li.next();
            Enumeration<String> e = rb.getKeys();
            while (e.hasMoreElements()) {
                String key = e.nextElement();
                if (!result.contains(key)) {
                    result.add(key);
                }
            }
        }
        return Collections.enumeration(result);
    }

    public void add(final ResourceBundle rb) {
        resourceBundles.addLast(rb);
    }

    /*
     * (non-Javadoc)
     * 
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
