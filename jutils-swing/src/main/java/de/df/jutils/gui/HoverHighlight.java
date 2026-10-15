package de.df.jutils.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.util.GraphicsUtils;

public final class HoverHighlight {

    private final Border normal = new EmptyBorder(2, 2, 2, 2);
    private final Border highlighted;
    private final Color start;
    private final Color end;

    public HoverHighlight() {
        Color s = UIManager.getColor("Panel.background");
        Color e = UIManager.getColor("List.dropCellBackground");
        if (s == null) {
            s = UIManager.getColor("InternalFrame.borderColor");
        }
        if (e == null) {
            e = UIManager.getColor("List.selectionBackground");
        }
        Color base = UIManager.getColor("List.background");
        if (base == null) {
            base = Color.WHITE;
        }
        start = s;
        end = ColorUtils.calculateColor(base, e, 0.5);
        highlighted = new CompoundBorder(new EmptyBorder(1, 1, 1, 1), new LineBorder(end, 1));
    }

    public static Color barBackground() {
        Color panel = UIManager.getColor("Panel.background");
        if (panel == null) {
            panel = new javax.swing.JPanel().getBackground();
        }
        boolean dark = panel.getRed() + panel.getGreen() + panel.getBlue() < 3 * 128;
        return dark ? ColorUtils.calculateColor(panel, Color.BLACK, 0.25)
                : ColorUtils.calculateColor(panel, Color.WHITE, 0.5);
    }

    public Border border(boolean highlight) {
        return highlight ? highlighted : normal;
    }

    public void paintGradient(Graphics2D g, int x, int y, int width, int height) {
        GraphicsUtils.paintGradient(g, x + 1, y + 1, width - 2, height - 2, start, end);
    }

    public void paint(Graphics2D g, int x, int y, int width, int height) {
        paintGradient(g, x, y, width, height);
        Graphics2D outline = (Graphics2D) g.create();
        outline.setColor(end);
        outline.drawRect(x + 1, y + 1, width - 3, height - 3);
        outline.dispose();
    }
}
