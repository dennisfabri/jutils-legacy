package de.df.jutils.print;

import org.junit.jupiter.api.Test;

import java.awt.Font;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PrintManagerTests {
    @Test
    void test() {
        Font f = PrintManager.getDefaultFont();
        List<String> log = PrintManager.getDefaultFontLog();
        log.forEach(System.out::println);

        assertNotNull(f);
    }
}
