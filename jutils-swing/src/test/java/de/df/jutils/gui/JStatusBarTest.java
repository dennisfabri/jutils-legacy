package de.df.jutils.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JStatusBarTest {

    @Test
    void textIsShown() {
        JStatusBar bar = new JStatusBar();

        bar.setText("Fertig");

        assertEquals("Fertig", bar.getText());
    }

    @Test
    void nullTextIsShownAsEmpty() {
        JStatusBar bar = new JStatusBar();
        bar.setText("Fertig");

        bar.setText(null);

        assertEquals("", bar.getText());
    }
}
