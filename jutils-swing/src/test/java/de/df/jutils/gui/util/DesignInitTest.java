package de.df.jutils.gui.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Font;

import javax.swing.UIManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DesignInitTest {

    private static final String[] KEYS = { "List.font", "Table.font", "ToolTip.font", "TextField.font",
            "FormattedTextField.font", "Viewport.background" };

    private final Object[] saved = new Object[KEYS.length];

    @BeforeEach
    void save() {
        for (int i = 0; i < KEYS.length; i++) {
            saved[i] = UIManager.get(KEYS[i]);
        }
    }

    @AfterEach
    void restore() {
        for (int i = 0; i < KEYS.length; i++) {
            UIManager.put(KEYS[i], saved[i]);
        }
    }

    @Test
    void listFontIsUsedForTablesToolTipsAndTextFields() {
        Font font = new Font("Serif", Font.ITALIC, 23);
        UIManager.put("List.font", font);

        DesignInit.tweakFonts();

        assertEquals(font, UIManager.get("Table.font"));
        assertEquals(font, UIManager.get("ToolTip.font"));
        assertEquals(font, UIManager.get("TextField.font"));
        assertEquals(font, UIManager.get("FormattedTextField.font"));
    }

    @Test
    void viewportBackgroundIsSetToTheTableBackgroundKey() {
        DesignInit.tweakFonts();

        assertEquals("Table.background", UIManager.get("Viewport.background"));
    }
}
