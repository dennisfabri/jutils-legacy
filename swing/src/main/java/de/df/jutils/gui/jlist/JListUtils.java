package de.df.jutils.gui.jlist;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.renderer.AlternatingListCellRenderer;

public final class JListUtils {

    private JListUtils() {
        // Hide
    }

    static Color getOddDefault(Component list) {
        Color odd = UIManager.getColor("textHighlight");
        Color base = UIManager.getColor("List.background");
        if (base == null) {
            base = Color.WHITE;
        }
        if (odd == null) {
            odd = UIManager.getColor("InternalFrame.borderShadow");
        }
        odd = ColorUtils.calculateColor(odd, base, 0.65);
        Color gray = ColorUtils.toGray(odd);
        odd = ColorUtils.calculateColor(odd, gray, 0.5);

        return odd;
    }

    static Color getEvenDefault(Component list) {
        return UIManager.getColor("List.background");
    }

    public static void setAlternatingListCellRenderer(JList<?> list) {
        setAlternatingListCellRenderer(list, null);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void setAlternatingListCellRenderer(JList list, ListCellRenderer lcr) {
        if (lcr == null) {
            lcr = list.getCellRenderer();
            if (lcr == null) {
                lcr = new DefaultListCellRenderer();
            }
        }
        Component c = lcr.getListCellRendererComponent(list, null, 0, false, false);
        list.setCellRenderer(new AlternatingListCellRenderer(getOddDefault(c), getEvenDefault(c), lcr));
    }
}
