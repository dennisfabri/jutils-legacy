/*
 * Created on 19.01.2004
 */
package de.df.jutils.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Dennis
 */
public class ConverterTest {

    @Test
    public void testHexToInt() {
        int value = Converter.hexToInt("55");
        assertEquals(85, value);

        value = Converter.hexToInt("00");
        assertEquals(0, value);

        value = Converter.hexToInt("0");
        assertEquals(0, value);

        value = Converter.hexToInt("FF");
        assertEquals(255, value);

        value = Converter.hexToInt("A0");
        assertEquals(160, value);
    }

}
