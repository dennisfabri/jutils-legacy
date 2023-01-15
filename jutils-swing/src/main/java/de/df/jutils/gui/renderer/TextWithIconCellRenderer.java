package de.df.jutils.gui.renderer;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Based on an example from www.java2s.com
 */
class TextWithIconCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    @Override
    protected void setValue(Object value) {
        if (value != null) {
            if (value instanceof TextWithIcon) {
                TextWithIcon d = (TextWithIcon) value;
                setText(d.toString());
                setIcon(d.getIcon());
                setHorizontalTextPosition(SwingConstants.RIGHT);
                setVerticalTextPosition(SwingConstants.CENTER);
                setHorizontalAlignment(SwingConstants.LEFT);
                setVerticalAlignment(SwingConstants.CENTER);
            } else {
                super.setValue(value);
            }
        } else {
            super.setValue(null);
        }
    }

    public static void installRenderer(JTable table) {
        table.setDefaultRenderer(TextWithIcon.class, new TextWithIconCellRenderer());
    }
}
