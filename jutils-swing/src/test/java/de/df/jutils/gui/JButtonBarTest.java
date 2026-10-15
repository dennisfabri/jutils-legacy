package de.df.jutils.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.junit.jupiter.api.Test;

class JButtonBarTest {

    private static JButtonBar barWith(int orientation, String... texts) {
        JButtonBar bar = new JButtonBar(orientation);
        for (String text : texts) {
            bar.add(new JButton(text));
        }
        bar.setSize(bar.getPreferredSize());
        bar.doLayout();
        return bar;
    }

    private static void hover(JButton button, boolean entered) {
        MouseEvent event = new MouseEvent(button, entered ? MouseEvent.MOUSE_ENTERED : MouseEvent.MOUSE_EXITED, 0, 0, 1,
                1, 0, false);
        for (var listener : button.getMouseListeners()) {
            if (entered) {
                listener.mouseEntered(event);
            } else {
                listener.mouseExited(event);
            }
        }
    }

    private static BufferedImage paint(JButtonBar bar) {
        BufferedImage image = new BufferedImage(bar.getWidth(), bar.getHeight(), BufferedImage.TYPE_INT_ARGB);
        var g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        bar.paint(g);
        g.dispose();
        return image;
    }

    @Test
    void horizontalBarPlacesButtonsSideBySideStartingAtTheLeft() {
        JButtonBar bar = barWith(SwingConstants.HORIZONTAL, "A", "A longer text", "C");

        Component a = bar.getComponent(0);
        Component b = bar.getComponent(1);
        Component c = bar.getComponent(2);
        assertEquals(JButtonBar.MARGIN, a.getX());
        assertTrue(a.getX() + a.getWidth() <= b.getX());
        assertTrue(b.getX() + b.getWidth() <= c.getX());
        assertEquals(a.getY(), b.getY());
    }

    @Test
    void verticalBarStacksButtonsStartingAtTheTop() {
        JButtonBar bar = barWith(SwingConstants.VERTICAL, "A", "B", "C");

        Component a = bar.getComponent(0);
        Component b = bar.getComponent(1);
        assertEquals(JButtonBar.MARGIN, a.getY());
        assertTrue(a.getY() + a.getHeight() <= b.getY());
        assertEquals(a.getX(), b.getX());
    }

    @Test
    void allButtonsHaveTheSizeOfTheLargest() {
        JButtonBar bar = barWith(SwingConstants.HORIZONTAL, "A", "A much longer text", "C");

        Dimension largest = bar.getComponent(1).getPreferredSize();
        for (Component button : bar.getComponents()) {
            assertEquals(largest, button.getSize());
        }
    }

    @Test
    void verticalBarStretchesTheButtonsToItsWidthButKeepsTheirHeight() {
        JButtonBar bar = barWith(SwingConstants.VERTICAL, "A", "B");
        Dimension before = bar.getComponent(0).getSize();

        bar.setSize(2000, 1000);
        bar.doLayout();

        for (Component button : bar.getComponents()) {
            assertEquals(before.height, button.getHeight());
            assertEquals(2000 - 2 * JButtonBar.MARGIN, button.getWidth());
            assertEquals(JButtonBar.MARGIN, button.getX());
        }
    }

    @Test
    void horizontalBarStretchesTheButtonsToItsHeightButKeepsTheirWidth() {
        JButtonBar bar = barWith(SwingConstants.HORIZONTAL, "A", "B");
        Dimension before = bar.getComponent(0).getSize();

        bar.setSize(2000, 1000);
        bar.doLayout();

        for (Component button : bar.getComponents()) {
            assertEquals(before.width, button.getWidth());
            assertEquals(1000 - 2 * JButtonBar.MARGIN, button.getHeight());
            assertEquals(JButtonBar.MARGIN, button.getY());
        }
    }

    @Test
    void verticalBarKeepsTheNaturalWidthWhenNoMoreSpaceIsGiven() {
        JButtonBar bar = barWith(SwingConstants.VERTICAL, "A", "A much longer text");

        Dimension largest = bar.getComponent(1).getPreferredSize();
        assertEquals(largest.width, bar.getComponent(0).getWidth());
    }

    @Test
    void stretchingRespectsTheBorderOfTheBar() {
        JButtonBar bar = new JButtonBar(SwingConstants.VERTICAL);
        bar.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 7, 3, 5));
        bar.add(new JButton("A"));
        bar.setSize(500, 400);
        bar.doLayout();

        assertEquals(500 - 12 - 2 * JButtonBar.MARGIN, bar.getComponent(0).getWidth());
    }

    @Test
    void preferredSizeIsTheSpaceTheButtonsNeed() {
        JButtonBar bar = barWith(SwingConstants.VERTICAL, "A", "B", "C");

        Dimension cell = bar.getComponent(0).getSize();
        Dimension preferred = bar.getPreferredSize();

        assertEquals(cell.width + 2 * JButtonBar.MARGIN, preferred.width);
        assertTrue(preferred.height >= 3 * cell.height + 2 * JButtonBar.MARGIN);
    }

    @Test
    void defaultOrientationIsHorizontal() {
        assertEquals(SwingConstants.HORIZONTAL, new JButtonBar().getOrientation());
    }

    @Test
    void otherOrientationsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new JButtonBar(SwingConstants.LEFT));
    }

    @Test
    void addedButtonsShowTheirTextBelowTheIcon() {
        JButtonBar bar = barWith(SwingConstants.HORIZONTAL, "A");

        JButton button = (JButton) bar.getComponent(0);
        assertEquals(SwingConstants.CENTER, button.getHorizontalTextPosition());
        assertEquals(SwingConstants.BOTTOM, button.getVerticalTextPosition());
    }

    @Test
    void addedButtonsAreFlat() {
        JButtonBar bar = barWith(SwingConstants.HORIZONTAL, "A");

        JButton button = (JButton) bar.getComponent(0);
        assertFalse(button.isContentAreaFilled());
        assertFalse(button.isFocusPainted());
        assertFalse(button.isOpaque());
    }

    @Test
    void hoveringAButtonPaintsTheHighlightBehindIt() {
        JButtonBar bar = barWith(SwingConstants.HORIZONTAL, "A", "B");
        JButton first = (JButton) bar.getComponent(0);
        int x = first.getX() + 2;
        int y = first.getY() + 2;
        int idle = paint(bar).getRGB(x, y);

        hover(first, true);
        int hovered = paint(bar).getRGB(x, y);
        hover(first, false);
        int after = paint(bar).getRGB(x, y);

        assertNotEquals(idle, hovered);
        assertEquals(idle, after);
    }

    @Test
    void hoveringOneButtonDoesNotHighlightTheOthers() {
        JButtonBar bar = barWith(SwingConstants.HORIZONTAL, "A", "B");
        JButton second = (JButton) bar.getComponent(1);
        int x = second.getX() + 2;
        int y = second.getY() + 2;
        int idle = paint(bar).getRGB(x, y);

        hover((JButton) bar.getComponent(0), true);

        assertEquals(idle, paint(bar).getRGB(x, y));
    }

    @Test
    void buttonsStartInsideTheBorderOfTheBar() {
        JButtonBar bar = new JButtonBar();
        bar.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 7, 3, 5));
        bar.add(new JButton("A"));
        bar.add(new JButton("B"));
        bar.setSize(bar.getPreferredSize());
        bar.doLayout();

        assertEquals(7 + JButtonBar.MARGIN, bar.getComponent(0).getX());
        assertEquals(20 + JButtonBar.MARGIN, bar.getComponent(0).getY());
    }

    @Test
    void preferredSizeIncludesTheBorderOfTheBar() {
        JButtonBar plain = barWith(SwingConstants.HORIZONTAL, "A", "B");
        JButtonBar bordered = new JButtonBar();
        bordered.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 7, 3, 5));
        bordered.add(new JButton("A"));
        bordered.add(new JButton("B"));

        assertEquals(plain.getPreferredSize().width + 12, bordered.getPreferredSize().width);
        assertEquals(plain.getPreferredSize().height + 23, bordered.getPreferredSize().height);
    }

    @Test
    void selectedToggleButtonHasAShadedBackground() {
        JButtonBar bar = new JButtonBar();
        javax.swing.JToggleButton first = new javax.swing.JToggleButton("A");
        bar.add(first);
        bar.add(new javax.swing.JToggleButton("B"));
        bar.setSize(bar.getPreferredSize());
        bar.doLayout();
        int x = first.getX() + 3;
        int y = first.getY() + 3;
        int idle = paint(bar).getRGB(x, y);

        first.setSelected(true);

        assertNotEquals(idle, paint(bar).getRGB(x, y));
    }

    @Test
    void barBackgroundIsLighterThanThePanelInALightTheme() {
        Color before = javax.swing.UIManager.getColor("Panel.background");
        try {
            javax.swing.UIManager.put("Panel.background", new Color(0xf2, 0xf2, 0xf2));

            Color background = new JButtonBar().getBackground();

            assertTrue(brightness(background) > brightness(new Color(0xf2, 0xf2, 0xf2)));
        } finally {
            javax.swing.UIManager.put("Panel.background", before);
        }
    }

    @Test
    void barBackgroundIsDarkerThanThePanelInADarkTheme() {
        Color before = javax.swing.UIManager.getColor("Panel.background");
        try {
            javax.swing.UIManager.put("Panel.background", new Color(0x3c, 0x3f, 0x41));

            Color background = new JButtonBar().getBackground();

            assertTrue(brightness(background) < brightness(new Color(0x3c, 0x3f, 0x41)));
        } finally {
            javax.swing.UIManager.put("Panel.background", before);
        }
    }

    @Test
    void barPaintsItsOwnBackground() {
        JButtonBar bar = barWith(SwingConstants.HORIZONTAL, "A");

        assertTrue(bar.isOpaque());
    }

    private static int brightness(Color c) {
        return c.getRed() + c.getGreen() + c.getBlue();
    }

    private static final String LONG_TEXT = "International - Ocean Mixed";

    private static int textWidth(String text) {
        JButton b = new JButton();
        return b.getFontMetrics(b.getFont()).stringWidth(text);
    }

    @Test
    void textWiderThanTheLimitIsWrappedIntoLines() {
        JButtonBar bar = new JButtonBar();
        bar.setMaxTextWidth(textWidth(LONG_TEXT) - 1);
        JButton button = new JButton(LONG_TEXT);

        bar.add(button);

        assertEquals("<html><center>International<br>Ocean Mixed</center></html>", button.getText());
    }

    @Test
    void textThatFitsStaysPlain() {
        JButtonBar bar = new JButtonBar();
        JButton button = new JButton("DLRG 2026");

        bar.add(button);

        assertEquals("DLRG 2026", button.getText());
    }

    @Test
    void defaultLimitIs140Pixels() {
        assertEquals(140, new JButtonBar().getMaxTextWidth());
    }

    @Test
    void wrappedButtonsAreNarrowerThanUnwrappedOnes() {
        JButtonBar wide = new JButtonBar();
        wide.setMaxTextWidth(10000);
        wide.add(new JButton(LONG_TEXT));
        JButtonBar narrow = new JButtonBar();
        narrow.setMaxTextWidth(textWidth(LONG_TEXT) - 1);
        narrow.add(new JButton(LONG_TEXT));

        assertTrue(narrow.getPreferredSize().width < wide.getPreferredSize().width);
        assertTrue(narrow.getPreferredSize().height > wide.getPreferredSize().height);
    }

    @Test
    void changingTheTextLaterWrapsTheNewText() {
        JButtonBar bar = new JButtonBar();
        bar.setMaxTextWidth(textWidth(LONG_TEXT) - 1);
        JButton button = new JButton("Kurz");
        bar.add(button);

        button.setText(LONG_TEXT);

        assertEquals("<html><center>International<br>Ocean Mixed</center></html>", button.getText());
    }

    @Test
    void raisingTheLimitRestoresThePlainText() {
        JButtonBar bar = new JButtonBar();
        bar.setMaxTextWidth(textWidth(LONG_TEXT) - 1);
        JButton button = new JButton(LONG_TEXT);
        bar.add(button);

        bar.setMaxTextWidth(10000);

        assertEquals(LONG_TEXT, button.getText());
    }

    @Test
    void textThatIsAlreadyHtmlIsNotTouched() {
        JButtonBar bar = new JButtonBar();
        bar.setMaxTextWidth(5);
        JButton button = new JButton("<html><b>Fett und lang</b></html>");

        bar.add(button);

        assertEquals("<html><b>Fett und lang</b></html>", button.getText());
    }

    @Test
    void specialCharactersAreEscapedInWrappedText() {
        JButtonBar bar = new JButtonBar();
        bar.setMaxTextWidth(5);
        JButton button = new JButton("A & B");

        bar.add(button);

        assertEquals("<html><center>A &amp;<br>B</center></html>", button.getText());
    }

    @Test
    void contentIsAlignedToTheTopSoIconsLineUp() {
        JButtonBar bar = barWith(SwingConstants.HORIZONTAL, "A");

        assertEquals(SwingConstants.TOP, ((JButton) bar.getComponent(0)).getVerticalAlignment());
    }
}
