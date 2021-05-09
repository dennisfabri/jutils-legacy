/*
 * Created on 02.12.2003
 */
package de.df.jutils.i18n;

import java.util.ResourceBundle;

/**
 * @author Dennis Mueller
 */
public final class SafeTextProcessor extends ATextProcessor {

    public SafeTextProcessor(final ResourceBundle rb) {
        super(rb);
    }

    @Override
    public String process(final String id, final Object[] dynamics) {
        try {
            return simpleProcess(getString(id), dynamics);
        } catch (Exception e) {
            try {
                String s = getString(id);
                if (dynamics != null) {
                    for (int x = 0; x < dynamics.length; x++) {
                        s = s.replace("{" + x + "}", dynamics[x].toString());
                    }
                }
                return s;
            } catch (RuntimeException re) {
                return id;
            }
        }
    }
}