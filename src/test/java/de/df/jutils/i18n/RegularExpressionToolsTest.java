/*
 * Created on 12.12.2003
 */
package de.df.jutils.i18n;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.df.jutils.util.RegularExpressionTools;

/**
 * @author Dennis
 */
public class RegularExpressionToolsTest {

    @Test
    public void testMatch() {
        boolean result = false;

        result = RegularExpressionTools.match(".*", "a");
        assertTrue(result);

        result = RegularExpressionTools.match("a", "a");
        assertTrue(result);

        result = RegularExpressionTools.match("dog.*", "dogs");
        assertTrue(result);

        result = RegularExpressionTools.match("[dog]{3}", "dog");
        assertTrue(result);

        result = RegularExpressionTools.match("[dog]{3}", "ddo");
        assertTrue(result);

        result = RegularExpressionTools.match("[dog]{3}", "ddd");
        assertTrue(result);

        result = RegularExpressionTools.match("[dog]{3}", "god");
        assertTrue(result);

        result = RegularExpressionTools.match("^[dog]{3}", "dog");
        assertTrue(result);

        result = RegularExpressionTools.match("^[dog]{3}$", "dog");
        assertTrue(result);
    }

    @Test
    public void testMatchFail() {
        boolean result = false;

        result = RegularExpressionTools.match(".{1}", "");
        assertTrue(!result);

        result = RegularExpressionTools.match(".{1}", "aa");
        assertTrue(!result);

        result = RegularExpressionTools.match("ab", "a");
        assertTrue(!result);

        result = RegularExpressionTools.match("dog", "dogs");
        assertTrue(!result);

        result = RegularExpressionTools.match("[dog]{3}", "do");
        assertTrue(!result);

        result = RegularExpressionTools.match("^[dog]{3}", "a dog");
        assertTrue(!result);

        result = RegularExpressionTools.match("^[dog]{3}$", "dogs");
        assertTrue(!result);
    }
}