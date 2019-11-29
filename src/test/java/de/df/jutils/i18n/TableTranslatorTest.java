/*
 * Created on 28.11.2003
 */
package de.df.jutils.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import de.df.jutils.resourcebundle.TableResourceBundle;

/**
 * @author Dennis Fabri
 */
public class TableTranslatorTest {

    @Test
    public void testTableTranslator() {
        new TableResourceBundle();
    }

    @Test
    public void testPut() {
        TableResourceBundle tt = new TableResourceBundle();
        tt.put("1", "eins");
        try {
            tt.put(null, "");
            fail();
        } catch (NullPointerException npe) {
            // ok
        }
    }

    @Test
    public void testRemove() {
        TableResourceBundle tt = new TableResourceBundle();
        tt.put("1", "eins");
        tt.remove("1");
        tt.remove("1");
        try {
            tt.remove(null);
            fail();
        } catch (NullPointerException npe) {
            // ok
        }
    }

    @Test
    public void testTranslation() {
        TableResourceBundle tt = new TableResourceBundle();

        String result = tt.getTranslation("1");
        assertNull(result);

        tt.put("1", "eins");
        result = tt.getTranslation("1");
        assertEquals("eins", result);
        tt.remove("1");

        result = tt.getTranslation("1");
        assertNull(result);
    }
}
