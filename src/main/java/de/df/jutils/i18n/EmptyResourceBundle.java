package de.df.jutils.i18n;

import java.util.*;

public class EmptyResourceBundle extends ResourceBundle {

    private Hashtable<String, String> keys = new Hashtable<String, String>();

    @Override
    public Enumeration<String> getKeys() {
        return keys.keys();
    }

    @Override
    protected Object handleGetObject(String key) {
        return "";
    }

}
