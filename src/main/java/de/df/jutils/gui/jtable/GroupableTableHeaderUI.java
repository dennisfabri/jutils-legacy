/*
 * This file is based on a tutorial "How do I create a JTable with nested
 * headers?" by Nobuo Tamemasa first posted at
 * http://www2.gol.com/users/tame/swing/examples/JTableExamples1.html and found
 * at http://www.esus.com/docs/GetQuestionPage.jsp?uid=1272 Since the sources
 * were not working with J2SE 1.4 I had to make a few changes 5th July 2004
 * Dennis Mueller
 */
package de.df.jutils.gui.jtable;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.*;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.renderer.SubstanceDefaultTableHeaderCellRenderer;
import org.pushingpixels.substance.internal.ui.*;

import de.df.jutils.reflection.ReflectionUtils;

public class GroupableTableHeaderUI extends BasicTableHeaderUI {

    final class TableMouseInputListener extends MouseInputHandler {
        @Override
        public void mouseMoved(MouseEvent e) {
            updateRolloverColumn(e);
            super.mouseMoved(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            updateRolloverColumn(e);
            super.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            rolloverColumn = -1;
            super.mouseExited(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            updateRolloverColumn(e);
            super.mouseDragged(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            updateRolloverColumn(e);
            super.mouseReleased(e);
        }
    }

    int                          rolloverColumn = -1;

    private TableHeaderUI        ui;
    @SuppressWarnings("hiding")
    private GroupableTableHeader header         = null;
    @SuppressWarnings("hiding")
    private CellRendererPane     rendererPane   = null;

    public GroupableTableHeaderUI(TableHeaderUI ui) {
        this.ui = ui;
    }

    public GroupableTableHeaderUI() {
    }

    private void initUI(JComponent c) {
        if (ui == null) {
            try {
                ui = (TableHeaderUI) ReflectionUtils
                        .getObject(UIManager.getLookAndFeelDefaults().getString("TableHeaderUI"));
            } catch (ClassCastException cce) {
                cce.printStackTrace();
            }
            if (ui == null) {
                if (UIManager.getLookAndFeel() instanceof SubstanceLookAndFeel) {
                    ui = (SubstanceTableHeaderUI) SubstanceTableHeaderUI.createUI(c);
                    // header = (GroupableTableHeader) c;
                    // header.setDefaultRenderer(new SubstanceDefaultTableHeaderCellRenderer());
                } else {
                    ui = new BasicTableHeaderUI();
                }
            }
        }
    }

    @Override
    public void installUI(JComponent c) {
        if (c instanceof GroupableTableHeader) {
            super.installUI(c);
            initUI(c);
            ui.installUI(c);
            header = (GroupableTableHeader) c;
            Component[] cs = header.getComponents();
            for (Component c1 : cs) {
                if (c1 instanceof CellRendererPane) {
                    rendererPane = (CellRendererPane) c1;
                }
            }
        }
    }

    @Override
    public void uninstallUI(JComponent c) {
        ui.uninstallUI(c);
        super.uninstallUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        if (c == null) {
            return;
        }
        Rectangle clipBounds = g.getClipBounds();
        if (header.getColumnModel() == null) {
            return;
        }
        header.setColumnMargin();
        int column = 0;
        Dimension size = header.getSize();
        Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
        Hashtable<ColumnGroup, Rectangle> hashtable;
        hashtable = new Hashtable<ColumnGroup, Rectangle>();
        Enumeration<TableColumn> enumeration = header.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            cellRect.height = size.height;
            cellRect.y = 0;
            TableColumn aColumn = enumeration.nextElement();
            Enumeration<Object> cGroups = header.getColumnGroups(aColumn);
            if (cGroups != null) {
                int groupHeight = 0;
                while (cGroups.hasMoreElements()) {
                    ColumnGroup cGroup = (ColumnGroup) cGroups.nextElement();
                    cGroup.setHeaderRenderer(aColumn.getHeaderRenderer());
                    Rectangle groupRect = hashtable.get(cGroup);
                    if (groupRect == null) {
                        groupRect = new Rectangle(cellRect);
                        Dimension d = cGroup.getSize(header.getTable());
                        groupRect.width = d.width - 1;
                        groupRect.height = d.height;
                        hashtable.put(cGroup, groupRect);
                    }
                    paintCell(g, groupRect, cGroup);
                    groupHeight += groupRect.height;
                    cellRect.height = size.height - groupHeight;
                    cellRect.y = groupHeight;
                }
            }
            cellRect.width = aColumn.getWidth();
            if (cellRect.intersects(clipBounds)) {
                paintCell(g, cellRect, column);
            }
            cellRect.x += cellRect.width;
            column++;
        }
    }

    public static ComponentUI createUI(JComponent component) {
        if (component instanceof GroupableTableHeader) {
            return new GroupableTableHeaderUI();
        }
        return null;
    }

    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
        TableCellRenderer renderer = header.getDefaultRenderer();
        TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        Component component = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false,
                false, -1, columnIndex);
        if (component instanceof JComponent) {
            ((JComponent) component).setOpaque(header.isOpaque());
        }
        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height,
                true);
    }

    private void paintCell(Graphics g, Rectangle cellRect, ColumnGroup cGroup) {
        Object[] cols = cGroup.getColumns();
        while (cols[0] instanceof ColumnGroup) {
            cols = ((ColumnGroup) cols[0]).getColumns();
        }
        TableColumn c = (TableColumn) cols[0];
        int x = c.getModelIndex();
        if (x == rolloverColumn) {
            // Make sure that GroupColumnHeaders are not painted with focus
            x++;
        }

        TableCellRenderer renderer = header.getDefaultRenderer();
        Component component = renderer.getTableCellRendererComponent(header.getTable(), cGroup.getHeaderValue(), false,
                false, -1, x);
        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height,
                true);
    }

    private int getHeaderHeight() {
        TableCellRenderer renderer = header.getDefaultRenderer();
        int height = 0;
        TableColumnModel columnModel = header.getColumnModel();
        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            TableColumn aColumn = columnModel.getColumn(column);
            Component comp = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false,
                    false, -1, column);
            int cHeight = comp.getPreferredSize().height;
            Enumeration<Object> e = header.getColumnGroups(aColumn);
            if (e != null) {
                while (e.hasMoreElements()) {
                    ColumnGroup cGroup = (ColumnGroup) e.nextElement();
                    cHeight += cGroup.getSize(header.getTable()).height;
                }
            }
            height = Math.max(height, cHeight);
        }
        return height;
    }

    private Dimension createHeaderSize(long width) {
        TableColumnModel columnModel = header.getColumnModel();
        width += columnModel.getColumnMargin() * columnModel.getColumnCount();
        if (width > Integer.MAX_VALUE) {
            width = Integer.MAX_VALUE;
        }
        return new Dimension((int) width, getHeaderHeight());
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        long width = 0;
        Enumeration<TableColumn> enumeration = header.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = enumeration.nextElement();
            width = width + aColumn.getPreferredWidth();
        }
        return createHeaderSize(width);
    }

    void updateRolloverColumn(MouseEvent e) {
        if (header.getDraggedColumn() == null && header.contains(e.getPoint())) {
            int col = header.columnAtPoint(e.getPoint());
            if (col != rolloverColumn) {
                rolloverColumn = col;
                header.repaint();
            }
        }
    }

    @Override
    protected MouseInputListener createMouseInputListener() {
        return new TableMouseInputListener();
    }
}