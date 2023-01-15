package de.df.jutils.i18n;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class EmptyResourceBundle extends ResourceBundle {

    @Override
    public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
    }

    @Override
    protected Object handleGetObject(String key) {
        return "";
    }

}
