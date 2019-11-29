/*
 * Created on 28.11.2003
 */
package de.df.jutils.i18n;

import java.text.MessageFormat;
import java.util.*;

import de.df.jutils.exception.ParserException;

/**
 * @author Dennis Mueller
 */
public abstract class ATextProcessor {

    private static MessageFormat formatter = new MessageFormat("");
    private static Object[]      none      = new Object[0];

    private ResourceBundle       bundle    = null;

    public ATextProcessor() {
        this(null);
    }

    public ATextProcessor(final ResourceBundle rb) {
        setResourceBundle(rb);
    }

    public final void setResourceBundle(final ResourceBundle rb) {
        bundle = rb;
    }

    public final ResourceBundle getResourceBundle() {
        return bundle;
    }

    public final String getString(final String key) {
        if (bundle == null) {
            return null;
        }
        return bundle.getString(key);
    }

    protected static String simpleProcess(final String translation) throws ParserException {
        return simpleProcess(translation, none);
    }

    protected static Object[] hashtableToArray(final Hashtable<String, Object> dynamicValues) {
        LinkedList<Object> arguments = new LinkedList<Object>();
        if (dynamicValues != null) {
            int x = 0;
            while (dynamicValues.get("" + x) != null) {
                arguments.addLast(dynamicValues.get("" + x));
                x++;
            }
        }
        return arguments.toArray();
    }

    protected static String simpleProcess(final String original, final Hashtable<String, Object> dynamicValues) throws ParserException {
        if (original == null) {
            throw new NullPointerException();
        }
        return simpleProcess(original, hashtableToArray(dynamicValues));
    }

    protected static String simpleProcess(String original, Object[] dynamicValues) throws ParserException {
        try {
            formatter.applyPattern(original);
            return formatter.format(dynamicValues);
        } catch (Exception e) {
            throw new ParserException("MessageFormatter threw an Exception (" + original + ")", e);
        }
    }

    public final String process(final String id) throws ParserException {
        return process(id, none);
    }

    public abstract String process(final String id, final Hashtable<String, Object> dynamics) throws ParserException;

    public abstract String process(final String id, final Object[] dynamics) throws ParserException;
}