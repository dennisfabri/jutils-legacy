package de.df.jutils.i18n;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class EmptyResourceBundle extends ResourceBundle {

    private Hashtable<String, String> keys = new Hashtable<>();

    @Override
    public Enumeration<String> getKeys() {
        return keys.keys();
    }

    @Override
    protected Object handleGetObject(String key) {
        return "";
    }

}
