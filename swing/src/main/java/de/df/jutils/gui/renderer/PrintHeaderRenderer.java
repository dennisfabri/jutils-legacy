package de.df.jutils.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class PrintHeaderRenderer extends DefaultTableCellRenderer {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3978993167141778745L;

    private final Color       background;

    public PrintHeaderRenderer(Color background) {
        if (background == null) {
            this.background = Color.LIGHT_GRAY;
        } else {
            this.background = background;
        }

        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        setBackground(this.background);

        // This call is needed because DefaultTableCellRenderer calls
        // setBorder()
        // in its constructor, which is executed after updateUI()
        // setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setBorder(null);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBorder(null);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
        JTableHeader h = (table != null ? table.getTableHeader() : null);

        if (table != null && h != null) {
            setEnabled(h.isEnabled());
            setComponentOrientation(h.getComponentOrientation());

            setForeground(h.getForeground());
            setBackground(background);
            setFont(h.getFont());
        } else {
            /*
             * Use sensible values instead of random leftover values from the
             * last call
             */
            setEnabled(true);
            setComponentOrientation(ComponentOrientation.UNKNOWN);

            setForeground(UIManager.getColor("TableHeader.foreground"));
            setBackground(background);
            setFont(UIManager.getFont("TableHeader.font"));
        }

        if (value instanceof String) {
            value = value.toString().replace(' ', '\u00A0');
        } else if (value instanceof String[]) {
            String[] s = (String[]) value;
            for (int x = 0; x < s.length; x++) {
                s[x] = s[x].replace(' ', '\u00A0');
            }
        }

        setValue(value);

        return this;
    }
}