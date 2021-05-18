/*
 * Created on 28.11.2003
 */
package de.df.jutils.resourcebundle;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * @author Dennis Fabri
 */
public final class IdentityResourceBundle extends ResourceBundle {

    /*
     * (non-Javadoc)
     * @see java.util.ResourceBundle#getKeys()
     */
    @Override
    public Enumeration<String> getKeys() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
     */
    @Override
    protected Object handleGetObject(final String id) {
        if (id == null) {
            throw new NullPointerException();
        }
        return id;
    }
}
