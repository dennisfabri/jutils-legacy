package de.df.jutils.plugin;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import de.df.jutils.gui.JButtonBar;

class JPanelContainerTest {

    private static PanelInfo info(String name, boolean top, int priority) {
        JPanel panel = new JPanel();
        panel.setName(name);
        return new PanelInfo(panel, null, top, false, priority);
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

    private static void layoutAll(Container c) {
        c.doLayout();
        for (Component child : c.getComponents()) {
            if (child instanceof Container container) {
                layoutAll(container);
            }
        }
    }

    @Test
    void sideBarIsNeverNarrowerThanItsButtons() {
        List<PanelInfo> panels = new ArrayList<>(List.of(info("Meldungen", true, 1), info("Ergebnisse", false, 1)));
        JPanelContainer container = new JPanelContainer(panels, false);
        container.setSize(1000, 700);
        layoutAll(container);

        for (JButtonBar bar : find(container, JButtonBar.class)) {
            JScrollPane scroller = (JScrollPane) bar.getParent().getParent();
            assertTrue(scroller.getWidth() >= scroller.getPreferredSize().width,
                    "width " + scroller.getWidth() + " preferred " + scroller.getPreferredSize().width);
        }
    }

    @Test
    void aScrollbarInTheSideBarDoesNotCoverTheButtons() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        List<PanelInfo> panels = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            panels.add(info("Panel Nummer " + i, true, i));
        }
        panels.add(info("Ergebnisse", false, 1));
        JPanelContainer container = new JPanelContainer(panels, false);
        JFrame frame = new JFrame();
        try {
            SwingUtilities.invokeAndWait(() -> {
                frame.add(container);
                frame.setSize(1000, 300);
                frame.setVisible(true);
            });
            for (int i = 0; i < 4; i++) {
                SwingUtilities.invokeAndWait(() -> {
                });
            }

            JButtonBar bar = find(container, JButtonBar.class).get(0);
            JScrollPane scroller = (JScrollPane) bar.getParent().getParent();
            assertTrue(scroller.getVerticalScrollBar().isVisible(), "precondition: scrollbar is needed");
            assertTrue(scroller.getViewport().getWidth() >= bar.getPreferredSize().width,
                    "viewport " + scroller.getViewport().getWidth() + " bar " + bar.getPreferredSize().width);
        } finally {
            SwingUtilities.invokeAndWait(frame::dispose);
        }
    }
}
