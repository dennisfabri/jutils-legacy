/*
 * Created on 28.11.2003
 */
package de.df.jutils.i18n;

import java.util.Hashtable;
import java.util.ResourceBundle;

import de.df.jutils.exception.ParserException;

/**
 * @author Dennis Mueller
 */
public final class TextProcessor extends ATextProcessor {

    public TextProcessor() {
        super();
    }

    public TextProcessor(final ResourceBundle rb) {
        super(rb);
    }

    @Override
    public String process(final String id, final Hashtable<String, Object> dynamics) throws ParserException {
        if (id == null) {
            throw new NullPointerException();
        }
        return simpleProcess(getString(id), dynamics);
    }

    @Override
    public String process(final String id, final Object[] dynamics) throws ParserException {
        if (id == null) {
            throw new NullPointerException();
        }
        return simpleProcess(getString(id), dynamics);
    }
}