package de.df.jutils.gui.autocomplete;

import java.awt.*;
import java.util.Optional;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import javax.swing.text.JTextComponent;

// Based on: https://github.com/aterai/java-swing-tips/blob/master/ComboBoxEditorVerifier/src/java/example/MainPanel.java
class ValidationLayerUI<V extends JTextComponent> extends LayerUI<V> {

    private static final Color YELLOW = new Color(255, 255, 145, 128);

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        Container p = SwingUtilities.getAncestorOfClass(JComboBox.class, c);
        if (p instanceof JComboBox) {
            JComboBox<?> cb = (JComboBox<?>) p;
            getInputVerifier(cb).filter(iv -> !iv.verify(cb)).ifPresent(iv -> {
                drawBox(g, c, YELLOW);
                // drawBoxWithX(g, c, Color.ORANGE);
            });
        }
    }

    private void drawBox(Graphics g, JComponent c, Color color) {
        int pad = 1;
        int w = c.getWidth() - 2 * pad;
        int h = c.getHeight() - 2 * pad;
        int x = pad;
        int y = pad;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(color);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }

    @SuppressWarnings("unused")
    private void drawBoxWithX(Graphics g, JComponent c, Color color) {
        Insets insets = c.getInsets();
        int w = c.getWidth();
        int h = c.getHeight();
        int s = h - insets.bottom - insets.top - 4;
        int pad = 5;
        int x = w - pad - s;
        int y = (h - s) / 2;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(color);
        g2.fillRect(0, 0, s, s);
        g2.setPaint(Color.WHITE);
        g2.drawLine(0, 0, s, s);
        g2.drawLine(0, s, s, 0);
        g2.dispose();
    }

    private static Optional<? extends InputVerifier> getInputVerifier(JComponent c) {
        return Optional.ofNullable(c.getInputVerifier());
    }
}