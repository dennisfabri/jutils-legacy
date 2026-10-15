package de.df.jutils.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.junit.jupiter.api.Test;

class TipOfTheDayTest {

    private static final TipOfTheDay.Texts TEXTS = new TipOfTheDay.Texts("Titel", "Wussten Sie?", "Beim Start", "Zurück",
            "Weiter", "Schließen");

    private static final Icon ICON = new ImageIcon(new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB));

    private static class Choice implements TipOfTheDay.ShowOnStartupChoice {
        boolean showing;
        final List<Boolean> stored = new ArrayList<>();

        Choice(boolean showing) {
            this.showing = showing;
        }

        @Override
        public boolean isShowingOnStartup() {
            return showing;
        }

        @Override
        public void setShowingOnStartup(boolean showOnStartup) {
            showing = showOnStartup;
            stored.add(showOnStartup);
        }
    }

    private static <T extends Component> List<T> find(Container c, Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Component child : c.getComponents()) {
            if (type.isInstance(child)) {
                result.add(type.cast(child));
            }
            if (child instanceof Container container) {
                result.addAll(find(container, type));
            }
        }
        return result;
    }

    private static JButton button(Container c, String text) {
        return find(c, JButton.class).stream().filter(b -> text.equals(b.getText())).findFirst().orElseThrow();
    }

    @Test
    void nextAndPreviousWrapAround() {
        TipOfTheDay tips = new TipOfTheDay(List.of("a", "b", "c"));

        assertEquals("a", tips.getCurrentTip());
        tips.previous();
        assertEquals("c", tips.getCurrentTip());
        tips.next();
        assertEquals("a", tips.getCurrentTip());
        tips.next();
        assertEquals("b", tips.getCurrentTip());
    }

    @Test
    void currentIndexIsKeptInRange() {
        TipOfTheDay tips = new TipOfTheDay(List.of("a", "b", "c"));

        tips.setCurrentIndex(7);

        assertEquals(1, tips.getCurrentIndex());
        assertEquals("b", tips.getCurrentTip());
    }

    @Test
    void withoutTipsEverythingIsEmpty() {
        TipOfTheDay tips = new TipOfTheDay(List.of());

        tips.next();
        tips.previous();
        tips.setCurrentIndex(3);

        assertEquals("", tips.getCurrentTip());
        assertTrue(tips.isEmpty());
    }

    @Test
    void panelShowsTheCurrentTipAndTheTexts() {
        TipOfTheDay tips = new TipOfTheDay(List.of("Erster", "Zweiter"));

        TipOfTheDayPanel panel = new TipOfTheDayPanel(tips, TEXTS, ICON, new Choice(true));

        assertEquals("Erster", find(panel, JTextComponent.class).get(0).getText());
        assertTrue(find(panel, JLabel.class).stream().anyMatch(l -> "Wussten Sie?".equals(l.getText())));
        assertEquals("Beim Start", find(panel, JCheckBox.class).get(0).getText());
        button(panel, "Zurück");
        button(panel, "Weiter");
        button(panel, "Schließen");
    }

    @Test
    void nextAndPreviousButtonsChangeTheShownTip() {
        TipOfTheDay tips = new TipOfTheDay(List.of("Erster", "Zweiter"));
        TipOfTheDayPanel panel = new TipOfTheDayPanel(tips, TEXTS, ICON, new Choice(true));

        button(panel, "Weiter").doClick();
        assertEquals("Zweiter", find(panel, JTextComponent.class).get(0).getText());

        button(panel, "Zurück").doClick();
        assertEquals("Erster", find(panel, JTextComponent.class).get(0).getText());
    }

    @Test
    void checkboxStartsWithTheStoredChoice() {
        TipOfTheDayPanel panel = new TipOfTheDayPanel(new TipOfTheDay(List.of("a")), TEXTS, ICON, new Choice(false));

        assertFalse(find(panel, JCheckBox.class).get(0).isSelected());
    }

    @Test
    void saveChoiceStoresTheCheckboxState() {
        Choice choice = new Choice(true);
        TipOfTheDayPanel panel = new TipOfTheDayPanel(new TipOfTheDay(List.of("a")), TEXTS, ICON, choice);

        find(panel, JCheckBox.class).get(0).setSelected(false);
        panel.saveChoice();

        assertEquals(List.of(false), choice.stored);
    }

    @Test
    void saveChoiceDoesNotWriteAnUnchangedChoice() {
        Choice choice = new Choice(true);
        TipOfTheDayPanel panel = new TipOfTheDayPanel(new TipOfTheDay(List.of("a")), TEXTS, ICON, choice);

        panel.saveChoice();

        assertTrue(choice.stored.isEmpty());
    }

    private static void layoutAll(Container c) {
        c.doLayout();
        for (Component child : c.getComponents()) {
            if (child instanceof Container container) {
                layoutAll(container);
            }
        }
    }

    @Test
    void headingShowsTheGivenIconWithSpaceToTheText() {
        TipOfTheDayPanel panel = new TipOfTheDayPanel(new TipOfTheDay(List.of("a")), TEXTS, ICON, new Choice(true));

        JLabel heading = find(panel, JLabel.class).stream().filter(l -> "Wussten Sie?".equals(l.getText()))
                .findFirst().orElseThrow();

        assertSame(ICON, heading.getIcon());
        assertTrue(heading.getIconTextGap() >= 10, "gap " + heading.getIconTextGap());
    }

    @Test
    void closeButtonEndsWhereTheTextFieldEnds() {
        TipOfTheDayPanel panel = new TipOfTheDayPanel(new TipOfTheDay(List.of("a")), TEXTS, ICON, new Choice(true));
        panel.setSize(panel.getPreferredSize());
        layoutAll(panel);

        JScrollPane scroll = find(panel, JScrollPane.class).get(0);
        JButton close = button(panel, "Schließen");
        int scrollRight = SwingUtilities.convertPoint(scroll, scroll.getWidth(), 0, panel).x;
        int closeRight = SwingUtilities.convertPoint(close, close.getWidth(), 0, panel).x;

        assertEquals(scrollRight, closeRight);
    }

    @Test
    void previousAndNextButtonsAreSmallerThanTheCloseButton() {
        TipOfTheDay.Texts symbols = new TipOfTheDay.Texts("Titel", "Wussten Sie?", "Beim Start", "<", ">", "Schließen");
        TipOfTheDayPanel panel = new TipOfTheDayPanel(new TipOfTheDay(List.of("a")), symbols, ICON, new Choice(true));

        int close = button(panel, "Schließen").getPreferredSize().width;

        assertTrue(button(panel, "<").getPreferredSize().width < close / 2);
        assertTrue(button(panel, ">").getPreferredSize().width < close / 2);
    }
}
