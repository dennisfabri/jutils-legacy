package de.df.jutils.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

class HoverHighlightTest {

    private final HoverHighlight highlight = new HoverHighlight();

    @Test
    void highlightedAndNormalBorderTakeUpTheSameSpace() {
        JPanel panel = new JPanel();

        Insets normal = highlight.border(false).getBorderInsets(panel);
        Insets highlighted = highlight.border(true).getBorderInsets(panel);

        assertEquals(normal, highlighted);
        assertTrue(normal.top > 0 && normal.left > 0);
    }

    @Test
    void paintFillsTheAreaWithAGradient() {
        BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        var g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 40, 40);

        highlight.paint(g, 0, 0, 40, 40);
        g.dispose();

        assertNotEquals(Color.WHITE.getRGB(), image.getRGB(20, 20));
        assertNotEquals(image.getRGB(20, 2), image.getRGB(20, 37));
    }
}
