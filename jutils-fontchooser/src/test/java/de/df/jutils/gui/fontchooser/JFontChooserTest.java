package de.df.jutils.gui.fontchooser;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.UIManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JFontChooserTest {

    private String family;
    private JFontChooser chooser;
    private List<PropertyChangeEvent> events;

    @BeforeEach
    void setUp() {
        String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        family = families[families.length / 2];
        chooser = new JFontChooser();
        events = new ArrayList<>();
        chooser.addPropertyChangeListener(JFontChooser.SELECTED_FONT_CHANGED_KEY, events::add);
    }

    @Test
    void defaultFontIsDialogPlainWithLookAndFeelSize() {
        Font f = chooser.getSelectedFont();
        assertEquals("Dialog", f.getFamily());
        assertEquals(Font.PLAIN, f.getStyle());
        assertEquals(UIManager.getFont("Label.font").getSize(), f.getSize());
    }

    @Test
    void defaultSizeFollowsLookAndFeelDialogFont() {
        Font old = UIManager.getFont("Label.font");
        try {
            UIManager.put("Label.font", new Font("Dialog", Font.PLAIN, 17));
            JFontChooser c = new JFontChooser();
            assertEquals(17, c.getSelectedFont().getSize());
            assertEquals(17, c.getSizeSpinner().getValue());
            assertEquals(17, new JFontChooser("No Such Font").getSelectedFont().getSize());
        } finally {
            UIManager.put("Label.font", old);
        }
    }

    @Test
    void defaultSizeIs12WithoutLookAndFeelFont() {
        Font old = UIManager.getFont("Label.font");
        try {
            UIManager.put("Label.font", null);
            assertEquals(12, new JFontChooser().getSelectedFont().getSize());
        } finally {
            UIManager.put("Label.font", old);
        }
    }

    @Test
    void roundTrip() {
        Font font = new Font(family, Font.BOLD | Font.ITALIC, 24);
        chooser.setSelectedFont(font);
        Font f = chooser.getSelectedFont();
        assertEquals(family, f.getFamily());
        assertEquals(Font.BOLD | Font.ITALIC, f.getStyle());
        assertEquals(24, f.getSize());
    }

    @Test
    void nonListSizeIsKept() {
        chooser.setSelectedFont(new Font(family, Font.PLAIN, 13));
        assertEquals(13, chooser.getSelectedFont().getSize());
    }

    @Test
    void nullIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> chooser.setSelectedFont(null));
    }

    @Test
    void firesOneEventPerChangeAndNoneWithoutChange() {
        Font font = new Font(family, Font.BOLD, 20);
        chooser.setSelectedFont(font);
        assertEquals(1, events.size());
        chooser.setSelectedFont(font);
        assertEquals(1, events.size());
        assertEquals(20, ((Font) events.get(0).getNewValue()).getSize());
    }

    @Test
    void sizeChangeViaSpinnerUpdatesFont() {
        chooser.setSelectedFont(new Font(family, Font.PLAIN, 12));
        chooser.getSizeSpinner().setValue(30);
        assertEquals(30, chooser.getSelectedFont().getSize());
    }

    @Test
    void sizeListAndSpinnerStayInSync() {
        chooser.getSizeList().setSelectedValue(18, true);
        assertEquals(18, chooser.getSizeSpinner().getValue());
        assertEquals(18, chooser.getSelectedFont().getSize());
    }

    @Test
    void styleListChangesStyle() {
        chooser.getStyleList().setSelectedIndex(1);
        assertEquals(Font.BOLD, chooser.getSelectedFont().getStyle());
    }

    @Test
    void fontListIsSortedAlphabetically() {
        List<String> names = chooser.getVisibleFamilies();
        assertFalse(names.isEmpty());
        java.text.Collator c = java.text.Collator.getInstance(chooser.getLocale());
        for (int i = 1; i < names.size(); i++) {
            assertTrue(c.compare(names.get(i - 1), names.get(i)) <= 0, names.get(i - 1) + " > " + names.get(i));
        }
    }

    @Test
    void filterIsCaseInsensitiveSubstringAndKeepsVisibleSelection() {
        chooser.setSelectedFont(new Font(family, Font.PLAIN, 12));
        String part = family.substring(1, Math.min(family.length(), 4)).toUpperCase(Locale.ROOT);
        chooser.getFilterField().setText(part);
        List<String> visible = chooser.getVisibleFamilies();
        assertTrue(visible.contains(family));
        for (String s : visible) {
            assertTrue(s.toLowerCase(Locale.ROOT).contains(part.toLowerCase(Locale.ROOT)));
        }
        assertEquals(family, chooser.getSelectedFont().getFamily());
    }

    @Test
    void filterWithoutMatchGivesEmptyListAndKeepsFont() {
        chooser.setSelectedFont(new Font(family, Font.PLAIN, 12));
        chooser.getFilterField().setText("\u0000no-such-font");
        assertTrue(chooser.getVisibleFamilies().isEmpty());
        assertEquals(family, chooser.getSelectedFont().getFamily());
    }

    @Test
    void firstAvailableNameWins() {
        assertTrue(chooser.setSelectedFontName("No Such Font 1", "  " + family.toUpperCase(Locale.ROOT) + " ", "Dialog"));
        assertEquals(family, chooser.getSelectedFont().getFamily());
    }

    @Test
    void noNameFoundLeavesSelectionUnchanged() {
        chooser.setSelectedFont(new Font(family, Font.BOLD, 20));
        events.clear();
        assertFalse(chooser.setSelectedFontName("No Such Font 1", null, " ", "No Such Font 2"));
        assertFalse(chooser.setSelectedFontName());
        assertFalse(chooser.setSelectedFontName((String[]) null));
        assertEquals(family, chooser.getSelectedFont().getFamily());
        assertTrue(events.isEmpty());
    }

    @Test
    void nameChangeKeepsStyleAndSize() {
        chooser.setSelectedFont(new Font("Dialog", Font.ITALIC, 22));
        chooser.setSelectedFontName(family);
        assertEquals(Font.ITALIC, chooser.getSelectedFont().getStyle());
        assertEquals(22, chooser.getSelectedFont().getSize());
    }

    @Test
    void constructorTakesNames() {
        JFontChooser c = new JFontChooser("No Such Font", family);
        assertEquals(family, c.getSelectedFont().getFamily());
        assertEquals(UIManager.getFont("Label.font").getSize(), c.getSelectedFont().getSize());
        assertEquals("Dialog", new JFontChooser("No Such Font").getSelectedFont().getFamily());
    }

    @Test
    void bundlesHaveSameKeys() {
        Set<String> en = keys(JFontChooser.bundle(Locale.ENGLISH));
        Set<String> de = keys(JFontChooser.bundle(Locale.GERMAN));
        assertFalse(en.isEmpty());
        assertEquals(en, de);
    }

    @Test
    void textsFollowLocale() {
        chooser.setLocale(Locale.ENGLISH);
        assertEquals("Bold", chooser.getStyleList().getModel().getElementAt(1));
        assertEquals("Size", chooser.getSizeLabel().getText());
        chooser.setLocale(Locale.GERMAN);
        assertEquals("Fett", chooser.getStyleList().getModel().getElementAt(1));
        assertEquals("Größe", chooser.getSizeLabel().getText());
    }

    @Test
    void localeChangeKeepsSelectedStyle() {
        chooser.setSelectedFont(new Font(family, Font.BOLD, 14));
        chooser.setLocale(Locale.GERMAN);
        assertEquals(Font.BOLD, chooser.getSelectedFont().getStyle());
    }

    @Test
    void previewWrapsAndUsesSelectedFont() {
        assertTrue(chooser.getPreview().getLineWrap());
        assertTrue(chooser.getPreview().getWrapStyleWord());
        assertFalse(chooser.getPreview().isEditable());
        chooser.setSelectedFont(new Font(family, Font.BOLD, 20));
        assertEquals(chooser.getSelectedFont(), chooser.getPreview().getFont());
    }

    @Test
    void previewBreaksLineBeforeNumbers() {
        for (Locale locale : new Locale[] { Locale.ENGLISH, Locale.GERMAN }) {
            chooser.setLocale(locale);
            String text = chooser.getPreview().getText();
            assertTrue(text.contains("\n0123456789"), locale + ": " + text);
        }
    }

    private static Set<String> keys(ResourceBundle rb) {
        Set<String> r = new HashSet<>();
        Enumeration<String> e = rb.getKeys();
        while (e.hasMoreElements()) {
            r.add(e.nextElement());
        }
        return r;
    }
}
