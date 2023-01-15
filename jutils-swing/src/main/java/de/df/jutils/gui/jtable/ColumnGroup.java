/*
 * This file is based on a tutorial "How do I create a JTable with nested
 * headers?" by Nobuo Tamemasa first posted at
 * http://www2.gol.com/users/tame/swing/examples/JTableExamples1.html and found
 * at http://www.esus.com/docs/GetQuestionPage.jsp?uid=1272 Since the sources
 * were not working with J2SE 1.4 I had to make a few changes. 5th July 2004
 * Dennis Fabri
 */
package de.df.jutils.gui.jtable;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * * ColumnGroup * *
 * 
 * @version 1.0 10/20/98 *
 * @author Nobuo Tamemasa
 */
public class ColumnGroup {

    private TableCellRenderer renderer;
    private Vector<Object> v;
    private String text;
    private int margin;

    public ColumnGroup(String newText) {
        this(null, newText);
    }

    public ColumnGroup(TableCellRenderer newRenderer, String newText) {
        if (newRenderer == null) {
            this.renderer = new ColumnGroupCellRenderer();
        } else {
            this.renderer = newRenderer;
        }
        this.text = newText;
        v = new Vector<>();
    }

    /**
     * *
     * 
     * @param obj TableColumn or ColumnGroup
     */
    public void add(Object obj) {
        if (obj == null) {
            return;
        }
        v.addElement(obj);
    }

    public void setText(String t) {
        text = t;
    }

    /**
     * *
     * 
     * @param c TableColumn *
     * @param v ColumnGroups
     */
    public Vector<Object> getColumnGroups(TableColumn c, Vector<Object> g) {
        g.addElement(this);
        if (v.contains(c)) {
            return g;
        }
        Enumeration<Object> e = v.elements();
        while (e.hasMoreElements()) {
            Object obj = e.nextElement();
            if (obj instanceof ColumnGroup) {
                Vector<Object> groups = ((ColumnGroup) obj).getColumnGroups(c, new Vector<>(g));
                if (groups != null) {
                    return groups;
                }
            }
        }
        return null;
    }

    public Object[] getColumns() {
        return v.toArray();
    }

    public TableCellRenderer getHeaderRenderer() {
        if (renderer == null) {
            return new ColumnGroupCellRenderer();
        }
        return renderer;
    }

    public void setHeaderRenderer(TableCellRenderer newRenderer) {
        if (newRenderer != null) {
            this.renderer = newRenderer;
            if (v != null) {
                Enumeration<Object> enumeration = v.elements();
                while (enumeration.hasMoreElements()) {
                    Object o = enumeration.nextElement();
                    if (o instanceof ColumnGroup) {
                        ((ColumnGroup) o).setHeaderRenderer(newRenderer);
                    }
                    if (o instanceof TableColumn) {
                        ((TableColumn) o).setHeaderRenderer(newRenderer);
                    }
                }
            }
        }
    }

    public Object getHeaderValue() {
        return text;
    }

    public Dimension getSize(JTable table) {
        if (table == null) {
            return new Dimension(10, 10);
        }
        if (table.getTableHeader().getDefaultRenderer() != null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }

        Component comp = renderer.getTableCellRendererComponent(table, getHeaderValue(), false, false, -1, -1);
        margin = Math.max(margin, table.getColumnModel().getColumnMargin());
        int height = comp.getPreferredSize().height;
        int width = 0;
        Enumeration<Object> e = v.elements();
        while (e.hasMoreElements()) {
            Object obj = e.nextElement();
            if (obj instanceof TableColumn) {
                TableColumn aColumn = (TableColumn) obj;
                width += aColumn.getWidth();
                width += margin - 1;
            } else {
                ColumnGroup cg = (ColumnGroup) obj;
                width += cg.getSize(table).width + margin - 1;
            }
        }
        return new Dimension(width + 1, height);
    }

    public void setColumnMargin(int newMargin) {
        this.margin = newMargin;
        Enumeration<Object> e = v.elements();
        while (e.hasMoreElements()) {
            Object obj = e.nextElement();
            if (obj instanceof ColumnGroup) {
                ((ColumnGroup) obj).setColumnMargin(newMargin);
            }
        }
    }

    static final class ColumnGroupCellRenderer extends DefaultTableCellRenderer {

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3258133561352400945L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
                setBorder(header.getBorder());
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            setText((value == null) ? "" : value.toString());
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            return this;
        }
    }
}
