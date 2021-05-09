/*
 * Created on 20.01.2005
 */
package de.df.jutils.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DateFormatter;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.border.ExtendedLineBorder;
import de.df.jutils.io.csv.FixedDecimal;

public class AlignmentCellRenderer<T> implements TableCellRenderer {

    public static enum BorderPositions {
        NONE, LEFT, RIGHT, BOTH
    }

    /**
     * Comment for <code>serialVersionUID</code>
     */
    static final long serialVersionUID = 3257848792169330232L;

    private final int defaultAlign;

    private final int[] alignments;

    private final JList<Object> list = new JList<>();
    private final JPanel listPanel = new JPanel(
            new FormLayout("0dlu,fill:default:grow,0dlu", "0dlu,fill:default:grow,0dlu"));
    private final JLabel label = new JLabel();

    private static TableCellRenderer fallback = new DefaultTableCellRenderer();

    private DateFormatter df = new DateFormatter();
    private NumberFormat nf = NumberFormat.getInstance();

    private double[] colors = new double[0];
    private boolean[] visible = new boolean[0];
    private BorderPositions[] borders = new BorderPositions[0];

    public AlignmentCellRenderer(int[] aligns, int align) {
        if (aligns == null) {
            alignments = new int[0];
        } else {
            alignments = aligns;
        }
        defaultAlign = align;
        list.setOpaque(true);
        list.setBorder(null);
        list.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = -1855198070805025589L;

            private final JLabel listlabel = new JLabel();

            @Override
            public Component getListCellRendererComponent(JList<?> parent, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                if (value instanceof Component) {
                    return (Component) value;
                }
                listlabel.setText(value.toString());
                return listlabel;
            }
        });
    }

    public void setBorders(BorderPositions[] col) {
        if (col == null) {
            if (borders.length > 0) {
                borders = new BorderPositions[0];
            }
            return;
        }

        borders = new BorderPositions[col.length];
        for (int x = 0; x < colors.length; x++) {
            borders[x] = (col[x] == null ? BorderPositions.NONE : col[x]);
        }
    }

    public void setColor(double[] col) {
        if (col == null) {
            if (colors.length > 0) {
                colors = new double[0];
            }
            return;
        }

        colors = new double[col.length];
        for (int x = 0; x < colors.length; x++) {
            colors[x] = Math.min(1.0, Math.max(0.0, col[x]));
        }
    }

    public void setVisible(boolean[] col) {
        if (col == null) {
            if (visible.length > 0) {
                visible = new boolean[0];
            }
            return;
        }

        visible = new boolean[col.length];
        System.arraycopy(col, 0, visible, 0, colors.length);
    }

    public Border createBorder(int column) {
        if (column >= borders.length) {
            return new EmptyBorder(0, 0, 0, 0);
        }
        switch (borders[column]) {
        case BOTH:
            return new ExtendedLineBorder(Color.BLACK, 0, 1, 0, 1);
        case LEFT:
            return new ExtendedLineBorder(Color.BLACK, 0, 1, 0, 0);
        case NONE:
            return new EmptyBorder(0, 0, 0, 0);
        case RIGHT:
            return new ExtendedLineBorder(Color.BLACK, 0, 0, 0, 1);
        }
        return new EmptyBorder(0, 0, 0, 0);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        int alignment = defaultAlign;
        if (column < alignments.length) {
            alignment = alignments[column];
        }

        if (value instanceof Date) {
            try {
                value = df.valueToString(value);
            } catch (ParseException e) {
                // Nothing to do
            }
        }

        if (value instanceof TableRenderDataProvider) {
            value = ((TableRenderDataProvider) value).getTableRenderData();
        }

        if (value instanceof FixedDecimal) {
            FixedDecimal fd = (FixedDecimal) value;
            int post = fd.getLength();
            nf.setGroupingUsed(false);
            nf.setRoundingMode(RoundingMode.HALF_UP);
            nf.setMaximumFractionDigits(post);
            nf.setMinimumFractionDigits(post);
            value = nf.format(fd.doubleValue());
        }

        if (value == null) {
            value = "\u00a0";
        }
        if (value instanceof String[]) {
            String[] s = (String[]) value;
            if (s.length == 0) {
                value = "\u00a0";
            } else if (s.length == 1) {
                value = s[0];
            } else {
                for (int x = 0; x < s.length; x++) {
                     s[x] = s[x].replace(' ', '\u00A0');
                }
            }
        }
        if (value instanceof String) {
            value = value.toString().replace(' ', '\u00A0');
        }

        // If different data is found:
        Component c = fallback.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // c.setForeground(UIManager.getDefaults().getColor("Text"));
        label.setForeground(c.getForeground());
        label.setBackground(c.getBackground());
        // label.setOpaque(c.isOpaque());
        label.setOpaque(true);
        label.setFont(c.getFont());

        updateColors(label, isSelected);

        if (column < colors.length) {
            if ((column < visible.length) && (visible[column])) {
                if (colors[column] < 1.0) {
                    double v = colors[column];

                    Color c1 = label.getForeground();
                    Color c2 = label.getBackground();

                    Color fg = new Color((int) (v * c1.getRed() + (1.0 - v) * c2.getRed()),
                            (int) (v * c1.getGreen() + (1.0 - v) * c2.getGreen()),
                            (int) (v * c1.getBlue() + (1.0 - v) * c2.getBlue()));
                    label.setForeground(fg);
                }
            } else {
                Color color = new Color(0, 0, 0, 0);
                label.setForeground(color);
            }
        }
        if (table.getColumnModel().getColumn(column).getMaxWidth() == 0) {
            label.setForeground(label.getBackground());
        }

        if (!table.getShowVerticalLines()) {
            label.setBorder(createBorder(column));
            if (c instanceof JComponent) {
                ((JComponent) c).setBorder(createBorder(column));
            }
        }

        if (value instanceof String[]) {
            String[] s = (String[]) value;

            listPanel.removeAll();
            listPanel.add(list, CC.xy(2, 2));
            // listPanel.setForeground(label.getForeground());
            listPanel.setBackground(label.getBackground());
            listPanel.setOpaque(true);

            list.removeAll();
            // list.setForeground(label.getForeground());
            // list.setBackground(label.getBackground());
            list.setOpaque(false);

            Object[] labels = new Object[s.length];
            for (int x = 0; x < s.length; x++) {
                JLabel temporaryLabel = new JLabel(s[x]);
                temporaryLabel.setFont(label.getFont());
                temporaryLabel.setForeground(label.getForeground());
                temporaryLabel.setBackground(label.getBackground());
                temporaryLabel.setOpaque(false);
                temporaryLabel.setHorizontalAlignment(alignment);
                labels[x] = temporaryLabel;
            }
            list.setListData(labels);
            if (hasFocus) {
                try {
                    list.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                } catch (NullPointerException npe) {
                    // Fix for OpenJDK-Bug
                }
            } else {
                try {
                    Border border = UIManager.getBorder("Table.focusCellHighlightBorder");
                    list.setBorder(border);
                    Border b = list.getBorder();

                    Insets i = b.getBorderInsets(list);
                    list.setBorder(new EmptyBorder(i));
                } catch (NullPointerException npe) {
                    // Fix for OpenJDK-Bug
                }
            }
            // list.setBorder(label.getBorder());
            return listPanel;
        }

        if (c instanceof JLabel) {
            JLabel l = (JLabel) c;
            l.setHorizontalAlignment(alignment);
        }

        return c;
    }

    private static void updateColors(JComponent label, boolean isSelected) {
        Color c1 = label.getForeground();
        if (c1 == null) {
            if (isSelected) {
                c1 = UIManager.getColor("Table.selectionForeground");
            } else {
                c1 = UIManager.getColor("Table.foreground");
            }
        }
        Color c2 = label.getBackground();
        if (c2 == null) {
            if (isSelected) {
                c2 = UIManager.getColor("Table.selectionBackground");
            } else {
                c2 = UIManager.getColor("Table.background");
            }
        }
        if (c2 == null) {
            if (c1 != null) {
                c2 = ColorUtils.contrastColor(c1);
            } else {
                c2 = Color.WHITE;
            }
        }
        if (c1 == null) {
            c1 = ColorUtils.contrastColor(c2);
        }
        label.setForeground(c1);
        label.setBackground(c2);
    }
}