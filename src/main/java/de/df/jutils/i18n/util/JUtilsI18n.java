/*
 * Created on 29.11.2003
 */
package de.df.jutils.i18n.util;

import java.util.Locale;
import java.util.ResourceBundle;

import de.df.jutils.i18n.SafeTextProcessor;
import de.df.jutils.resourcebundle.IdentityResourceBundle;
import de.df.jutils.resourcebundle.MultipleResourceBundle;
import de.df.jutils.resourcebundle.SafeResourceBundle;
import de.df.jutils.resourcebundle.TableResourceBundle;

/**
 * @author Dennis Mueller
 */
public final class JUtilsI18n {

    private JUtilsI18n() {
        // Hide
    }

    private static TableResourceBundle fallback = null;
    private static SafeTextProcessor   instance = null;

    public static synchronized SafeTextProcessor getInstance() {
        if (instance == null) {
            MultipleResourceBundle rb = new MultipleResourceBundle();
            try {
                ResourceBundle fileRB = ResourceBundle.getBundle("jutils", Locale.getDefault(), JUtilsI18n.class.getClassLoader());
                rb.add(fileRB);
            } catch (RuntimeException re) {
                re.printStackTrace();
                // Nothing to do
            }
            rb.add(getFallbackResourceBundle());
            rb.add(new IdentityResourceBundle());
            instance = new SafeTextProcessor(new SafeResourceBundle(rb));
        }
        return instance;
    }

    private static synchronized ResourceBundle getFallbackResourceBundle() {
        if (fallback == null) {
            fallback = new TableResourceBundle();
        }
        return fallback;
    }

    public static String get(final String key, final Object... dynamics) {
        return getInstance().process(key, dynamics);
    }
}