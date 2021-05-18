/*
 * Created on 28.11.2003
 */
package de.df.jutils.resourcebundle;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * @author Dennis Fabri
 */
public final class SafeResourceBundle extends ResourceBundle {

    private ResourceBundle parentBundle = null;

    public SafeResourceBundle(final ResourceBundle rb) {
        setParent(rb);
    }

    private boolean verbose = true;

    @Override
    public Enumeration<String> getKeys() {
        if (parentBundle == null) {
            return null;
        }
        return parentBundle.getKeys();
    }

    /*
     * (non-Javadoc)
     * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
     */
    @Override
    protected Object handleGetObject(final String id) {
        if (id == null) {
            return "";
        }
        try {
            String result = parentBundle.getString(id);
            if (result != null) {
                return result;
            }
        } catch (RuntimeException e) {
            if (isVerbose()) {
                e.printStackTrace();
            }
        }
        return id;
    }

    @Override
    protected void setParent(final ResourceBundle rb) {
        this.parentBundle = rb;
        super.setParent(parentBundle);
    }

    /**
     * @return
     */
    public ResourceBundle getParent() {
        return parentBundle;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

}