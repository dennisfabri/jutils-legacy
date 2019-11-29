/*
 * Created on 13.01.2004
 */
package de.df.jutils.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.*;

/**
 * @author Dennis
 */
public class StringToolsTest {

    private final char[] chars = new char[] { '\'', 'a', 'b', '\'', ' ', 'd' };
    private final String ps    = File.separator;

    @Test
    public void testSkip1() {
        int pos = StringTools.skip(chars, 1, '\'', false);
        assertEquals(3, pos);
    }

    @Test
    public void testSkip2() {
        int pos = StringTools.skip(chars, 0, '\'', false);
        assertEquals(0, pos);
    }

    @Test
    public void testSkip3() {
        int pos = StringTools.skip(chars, 1, '\'', true);
        assertEquals(4, pos);
    }

    @Test
    public void testSkip4() {
        int pos = StringTools.skip(chars, 0, '\'', true);
        assertEquals(1, pos);
    }

    @Test
    public void testSkip5() {
        int pos = StringTools.skip(chars, 4, '\'', true);
        assertEquals(6, pos);
    }

    @Test
    public void testSkip6() {
        int pos = StringTools.skip(chars, 4, '\'', false);
        assertEquals(6, pos);
    }

    @Test
    public void testConcatFileNameStringString() {
        String s = StringTools.concatFileName("a", "b");
        assertEquals("a" + ps + "b", s);

        s = StringTools.concatFileName("a" + ps, "b");
        assertEquals("a" + ps + "b", s);

        s = StringTools.concatFileName("a", ps + "b");
        assertEquals("a" + ps + "b", s);

        s = StringTools.concatFileName("a" + ps, ps + "b");
        assertEquals("a" + ps + "b", s);
    }

    @Test
    public void testConcatFileNameStringStringString() {
        String s = StringTools.concatFileName("a", "b", "c");
        assertEquals("a" + ps + "b" + ps + "c", s);
        s = StringTools.concatFileName("a" + ps, "b", "c");
        assertEquals("a" + ps + "b" + ps + "c", s);
        s = StringTools.concatFileName("a", ps + "b", "c");
        assertEquals("a" + ps + "b" + ps + "c", s);
        s = StringTools.concatFileName("a" + ps, ps + "b", "c");
        assertEquals("a" + ps + "b" + ps + "c", s);
    }

    @Test
    public void testCapitalize() {
        String s = StringTools.capitalize("hallo");
        assertEquals("Hallo", s);
        s = StringTools.capitalize("Hallo");
        assertEquals("Hallo", s);
    }

}
