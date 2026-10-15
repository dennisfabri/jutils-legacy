package de.df.jutils.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;

import org.junit.jupiter.api.Test;

class PanelSelectorTest {

    private final PanelSelector selector = new PanelSelector();
    private final List<PanelInfo> selections = new ArrayList<>();

    private static PanelInfo info(String name) {
        return new PanelInfo(name, null, 0);
    }

    private PanelSelector withListener() {
        selector.setSelectionListener(selections::add);
        return selector;
    }

    private AbstractButton button(int index) {
        return (AbstractButton) selector.getComponent().getComponent(index);
    }

    @Test
    void panelsAreShownAsButtonsInTheOrderTheyWereAdded() {
        PanelInfo a = info("A");
        PanelInfo b = info("B");

        selector.addLast(a);
        selector.addLast(b);

        assertEquals(List.of(a, b), selector.getAllElements());
        assertEquals("A", button(0).getText());
        assertEquals("B", button(1).getText());
        assertTrue(button(0) instanceof JToggleButton);
    }

    @Test
    void panelCanBeInsertedAtAPosition() {
        PanelInfo a = info("A");
        PanelInfo b = info("B");
        PanelInfo c = info("C");
        selector.addLast(a);
        selector.addLast(c);

        selector.add(b, 1);

        assertEquals(List.of(a, b, c), selector.getAllElements());
        assertEquals("B", button(1).getText());
    }

    @Test
    void clickingAButtonSelectsItAndTellsTheListener() {
        withListener();
        PanelInfo a = info("A");
        PanelInfo b = info("B");
        selector.addLast(a);
        selector.addLast(b);

        button(1).doClick();

        assertSame(b, selector.getSelected());
        assertEquals(List.of(b), selections);
    }

    @Test
    void onlyOneButtonIsSelectedAtATime() {
        selector.addLast(info("A"));
        selector.addLast(info("B"));

        button(0).doClick();
        button(1).doClick();

        assertFalse(button(0).isSelected());
        assertTrue(button(1).isSelected());
    }

    @Test
    void selectingProgrammaticallyAlsoTellsTheListenerOnce() {
        withListener();
        PanelInfo a = info("A");
        PanelInfo b = info("B");
        selector.addLast(a);
        selector.addLast(b);

        selector.select(b);
        selector.select(b);

        assertSame(b, selector.getSelected());
        assertEquals(List.of(b), selections);
    }

    @Test
    void removingTheSelectedPanelClearsTheSelection() {
        PanelInfo a = info("A");
        selector.addLast(a);
        selector.select(a);

        selector.remove(a);

        assertNull(selector.getSelected());
        assertEquals(0, selector.size());
        assertEquals(0, selector.getComponent().getComponentCount());
        assertFalse(selector.contains(a));
    }

    @Test
    void elementsCanBeLookedUpByIndex() {
        PanelInfo a = info("A");
        PanelInfo b = info("B");
        selector.addLast(a);
        selector.addLast(b);

        assertSame(b, selector.getElementAt(1));
        assertTrue(selector.contains(b));
        assertEquals(2, selector.size());
    }
}
