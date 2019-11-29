package de.df.jutils.resourcebundle;

import java.util.*;

public final class TableResourceBundle extends ResourceBundle {

    private Hashtable<String, String> values;

    public TableResourceBundle() {
        values = new Hashtable<String, String>();
    }

    public String getTranslation(final String id) {
        return values.get(id);
    }

    public void put(final String id, final String value) {
        values.put(id, value);
    }

    public void remove(final String id) {
        values.remove(id);
    }

    @Override
    public Enumeration<String> getKeys() {
        return values.keys();
    }

    @Override
    protected Object handleGetObject(final String id) {
        return values.get(id);
    }

    /**
     * @return
     */
    public Hashtable<String, String> getValues() {
        return values;
    }

    /**
     * @param hashtable
     */
    public void setValues(final Hashtable<String, String> hashtable) {
        values = hashtable;
    }

}