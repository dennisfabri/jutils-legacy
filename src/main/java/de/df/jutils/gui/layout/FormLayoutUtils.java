/*
 * Created on 19.12.2003
 */
package de.df.jutils.gui.layout;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Dennis
 */
public final class FormLayoutUtils {

    private FormLayoutUtils() {
        // Never called
    }

    public static String createLayoutString(int rowsOrColumns) {
        return createLayoutString(rowsOrColumns, 4);
    }

    public static String createGrowingLayoutString(int rowsOrColumns) {
        return createGrowingLayoutString(rowsOrColumns, 4);
    }

    public static String createDoubleFormLayout(int columns) {
        return createDoubleFormLayout(columns, 4);
    }

    public static String createDoubleFormLayout(int columns, int dlu) {
        StringBuffer result = new StringBuffer();
        result.append(dlu);
        result.append("dlu");
        for (int x = 0; x < columns; x++) {
            result.append(",fill:default,");
            result.append(dlu);
            result.append("dlu");
            result.append(",fill:default:grow,");
            result.append(dlu);
            result.append("dlu");
        }
        return result.toString();
    }

    public static String createGrowingLayoutString(int rowsOrColumns, int dlu) {
        return createGrowingLayoutString(rowsOrColumns, dlu, dlu);
    }

    public static String createGrowingLayoutString(int rowsOrColumns, int dlu, int outer) {
        StringBuffer result = new StringBuffer("" + outer + "dlu");
        if (rowsOrColumns > 0) {
            for (int x = 0; x < rowsOrColumns - 1; x++) {
                result.append(",fill:default:grow,").append(dlu).append("dlu");
            }
            result.append(",fill:default:grow,").append(outer).append("dlu");
        }
        return result.toString();
    }

    public static String createLayoutString(int rowsOrColumns, int dlu, int outer) {
        return createLayoutString(rowsOrColumns, dlu, outer + "dlu", outer + "dlu");
    }

    public static String createLayoutString(int rowsOrColumns, int dlu, String leftOuter, String rightOuter) {
        StringBuffer result = new StringBuffer(leftOuter);
        if (rowsOrColumns > 0) {
            for (int x = 0; x < rowsOrColumns - 1; x++) {
                result.append(",fill:default,").append(dlu).append("dlu");
            }
            result.append(",fill:default,").append(rightOuter);
        }
        return result.toString();
    }

    public static String createLayoutString(int rowsOrColumns, int dlu) {
        return createLayoutString(rowsOrColumns, dlu, dlu);
    }

    public static String createFixedTable(int rowsOrColumns, int dlu) {
        StringBuffer result = new StringBuffer("1px");
        for (int x = 0; x < rowsOrColumns; x++) {
            result.append(",").append(dlu).append("dlu,1px");
        }
        return result.toString();
    }

    public static String createSpacingLayoutString(int rowsOrColumns) {
        return createSpacingLayoutString(rowsOrColumns, 4);
    }

    public static String createSpacingLayoutString(int rowsOrColumns, int dlu) {
        return createSpacingLayoutString(rowsOrColumns, dlu, dlu);
    }

    public static String createSpacingLayoutString(int rowsOrColumns, int dlu, int outer) {
        StringBuffer result = new StringBuffer("" + outer + "dlu:grow");
        if (rowsOrColumns > 0) {
            for (int x = 0; x < rowsOrColumns - 1; x++) {
                result.append(",fill:default,").append(dlu).append("dlu:grow");
            }
            result.append(",fill:default,").append(outer).append("dlu:grow");
        }
        return result.toString();
    }

    public static void setRowGroups(FormLayout layout, int count) {
        setRowGroups(layout, 0, count);
    }

    public static void setRowGroups(FormLayout layout, int offset, int count) {
        if (count <= 1) {
            layout.setRowGroups(new int[0][0]);
        } else {
            layout.setRowGroups(createGroups(offset, count));
        }
    }

    public static void setColumnGroups(FormLayout layout, int count) {
        setColumnGroups(layout, 0, count);
    }

    public static void setColumnGroups(FormLayout layout, int offset, int count) {
        if (count <= 1) {
            layout.setColumnGroups(new int[0][0]);
        } else {
            layout.setColumnGroups(createGroups(offset, count));
        }
    }

    private static int[][] createGroups(int offset, int count) {
        int[][] result = new int[1][count];
        for (int x = 0; x < count; x++) {
            result[0][x] = offset + (x + 1) * 2;
        }
        return result;
    }

    private static String getAlign(int index, int[] aligns, int align) {
        int value = align;
        if ((aligns != null) && (aligns.length > index)) {
            value = aligns[index];
        }
        switch (value) {
        default:
        case SwingConstants.LEFT:
            return "left";
        case SwingConstants.RIGHT:
            return "right";
        case SwingConstants.CENTER:
            return "center";
        case SwingConstants.NEXT:
            return "fill";
        }
    }

    public static JPanel createTable(JPanel p, String title, Object[] titles, Object[][] data, int[] aligns, int align, boolean growall) {
        if (p == null) {
            p = new JPanel();
        }

        int rows = data.length + 1;
        int columns = titles.length;
        for (Object[] aData : data) {
            columns = Math.max(columns, aData.length);
        }

        int offset = (title != null && aligns != null ? aligns.length * 2 : 0);

        FormLayout layout;
        if (growall) {
            layout = new FormLayout(createGrowingLayoutString(columns), createLayoutString(rows + (title != null ? 1 : 0)));
        } else {
            layout = new FormLayout("4dlu,fill:default:grow," + createLayoutString(columns - 1), createLayoutString(rows + (title != null ? 1 : 0)));
        }
        if (rows > 1) {
            setRowGroups(layout, rows);
        }
        p.setLayout(layout);

        if (title != null) {
            p.add(new JLabel(title), CC.xyw(4, 2, columns * 2 - 3, "center,center"));
        }

        for (int x = 0; x < titles.length; x++) {
            if (titles[x] != null) {
                Component c = null;
                if (titles[x] instanceof Component) {
                    c = (Component) titles[x];
                } else {
                    c = new JLabel(titles[x].toString());
                }
                p.add(c, CC.xy(2 + 2 * x, 2 + offset, "center,center"));
            }
        }

        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[x].length; y++) {
                if (data[x][y] != null) {
                    Component c = null;
                    if (data[x][y] instanceof Component) {
                        c = (Component) data[x][y];
                    } else {
                        c = new JLabel(data[x][y].toString());
                    }
                    p.add(c, CC.xy(2 + 2 * y, 4 + 2 * x + offset, getAlign(y, aligns, align) + ",fill"));
                }
            }
        }

        return p;
    }

    public static JPanel createButtonsPanel(JComponent... buttons) {
        FormLayout layout = new FormLayout(createLayoutString(buttons.length, 4, 0), "0dlu,fill:default,0dlu");
        JPanel p = new JPanel(layout);
        int[] group = new int[buttons.length];
        for (int x = 0; x < buttons.length; x++) {
            int pos = x * 2 + 2;
            p.add(buttons[x], CC.xy(pos, 2));
            group[x] = pos;
        }
        layout.setColumnGroups(new int[][] { group });
        return p;
    }
}