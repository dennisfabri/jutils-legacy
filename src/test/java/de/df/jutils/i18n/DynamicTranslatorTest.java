/*
 * Created on 28.11.2003
 */
package de.df.jutils.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.*;

import org.junit.jupiter.api.*;

import de.df.jutils.exception.ParserException;
import de.df.jutils.i18n.TextProcessor;
import de.df.jutils.resourcebundle.TableResourceBundle;

/**
 * @author Dennis Fabri
 */
public class DynamicTranslatorTest {

    private TableResourceBundle translator;
    private Hashtable<String, Object> dynamics;
    
    @BeforeEach
    protected void setUp() throws Exception {
        translator = new TableResourceBundle();
        translator.put("0", "{");
        translator.put("1", "eins");
        translator.put("2", "eins {2}");
        translator.put("3", "eins {2} {3}");
        translator.put("4", "eins {2} drei {4}");
        translator.put("5", "{5} sechs");
        translator.put("6", "'{{'{5}'}}'");
        translator.put("7", "''");
        
        dynamics = new Hashtable<String, Object>();
        dynamics.put("0", "0");
        dynamics.put("1", "1");
        dynamics.put("2", "2");
        dynamics.put("3", "3");
        dynamics.put("4", "4");
        dynamics.put("5", "5");
    }

    /*
     * Test for void Translator(ITranslator)
     */
    @Test
    public void testDynamicTranslatorITranslator() {
        new TextProcessor(null);
        new TextProcessor(translator);
    }

    /*
     * Test for String getTranslation(String)
     */
    @Test
    public void testGetTranslationString() throws ParserException {
        TextProcessor t = new TextProcessor(translator);
        String result = null;

        result = t.process("1");
        assertEquals("eins", result);

        result = t.process("2");
        assertEquals("eins {2}", result);

        result = t.process("3");
        assertEquals("eins {2} {3}", result);

        result = t.process("4");
        assertEquals("eins {2} drei {4}", result);

        result = t.process("5");
        assertEquals("{5} sechs", result);

        result = t.process("6");
        assertEquals("{{{5}}}", result);

        result = t.process("7");
        assertEquals("'", result);

        try {
            t.process("42");
        } catch (MissingResourceException npe) {
            // ok
        }

        try {
            t.process(null);
        } catch (NullPointerException npe) {
            // ok
        }
    }

    /*
     * Test for String getTranslation(String, Hashtable)
     */
    public void testGetTranslationStringHashtable() throws ParserException {
        TextProcessor t = new TextProcessor(translator);
        String result = null;

        try {
            t.process("0", dynamics);
            fail();
        } catch (ParserException pe) {
            // ok
        }
        try {
            t.process("0");
            fail();
        } catch (ParserException pe) {
            // ok
        }

        result = t.process("1", dynamics);
        assertEquals("eins", result);
        result = t.process("1");
        assertEquals("eins", result);

        result = t.process("2", dynamics);
        assertEquals("eins 2", result);
        result = t.process("2");
        assertEquals("eins {2}", result);

        result = t.process("3", dynamics);
        assertEquals("eins 2 3", result);
        result = t.process("3");
        assertEquals("eins {2} {3}", result);

        result = t.process("4", dynamics);
        assertEquals("eins 2 drei 4", result);
        result = t.process("4");
        assertEquals("eins {2} drei {4}", result);

        result = t.process("5", dynamics);
        assertEquals("5 sechs", result);
        result = t.process("5");
        assertEquals("{5} sechs", result);

        result = t.process("6", dynamics);
        assertEquals("{{5}}", result);
        result = t.process("6");
        assertEquals("{{{5}}}", result);

        try {
            t.process("42", dynamics);
        } catch (MissingResourceException npe) {
            // ok
        }

        try {
            t.process(null, dynamics);
        } catch (NullPointerException npe) {
            // ok
        }
    }
}