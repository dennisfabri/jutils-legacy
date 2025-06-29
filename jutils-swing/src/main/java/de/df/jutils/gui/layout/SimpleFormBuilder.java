/*
 * Created on 02.05.2005
 */
package de.df.jutils.gui.layout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.Border;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.df.jutils.gui.JLabelSeparator;

public class SimpleFormBuilder {

    public enum GrowModel {
        Resize, DoubleSize, TrippleSize, None
    }

    private final FormLayout layout;
    private final JPanel panel;
    private final LinkedList<Integer> fills = new LinkedList<>();
    private final LinkedList<Integer> grows = new LinkedList<>();

    private Font font;
    private Color color;
    private boolean group = true;

    private final String spacer;

    public SimpleFormBuilder(JPanel panel, FormLayout layout, int space, boolean outerSpace) {
        if (space < 0) {
            throw new IllegalArgumentException("space must be at least 0.");
        }
        spacer = space + "dlu";
        if (layout == null) {
            int intend = 4;
            if (space > 0) {
                intend = space * 4;
            }
            layout = new FormLayout(
                    (outerSpace ? spacer : "0dlu") + ",fill:default," + spacer + "," + intend + "dlu,"
                            + "fill:0px:grow," + (outerSpace ? spacer : "0dlu"),
                    (outerSpace ? spacer : "0dlu"));
        }
        if (panel == null) {
            panel = new JPanel();
        } else {
            panel.removeAll();
        }
        this.layout = layout;
        this.panel = panel;
        panel.setLayout(layout);
    }

    public SimpleFormBuilder(FormLayout layout) {
        this(null, layout, 4, true);
    }

    public SimpleFormBuilder(JPanel panel) {
        this(panel, null, 4, true);
    }

    public SimpleFormBuilder() {
        this(null, null, 4, true);
    }

    public SimpleFormBuilder(boolean outerSpace) {
        this(null, null, 4, outerSpace);
    }

    public SimpleFormBuilder(boolean opaque, boolean group) {
        this(opaque, group, 4);
    }

    public SimpleFormBuilder(int space) {
        this(null, null, space, true);
    }

    public SimpleFormBuilder(boolean opaque, boolean group, int space) {
        this(null, null, space, true);
        panel.setOpaque(opaque);
        this.group = group;
    }

    public void setBorder(Border b) {
        panel.setBorder(b);
    }

    public void addButton(JButton button) {
        addRow();
        fills.removeLast();
        int index = layout.getRowCount();
        addEmptyRow();

        if (font != null) {
            button.setFont(font);
        }
        panel.add(button, CC.xyw(4, index, 2, "right,fill"));
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addSeparator(String name) {
        addRow();
        int index = layout.getRowCount();
        if (index == 2) {
            fills.removeLast();
        }
        addEmptyRow();
        if ((name == null) || name.isEmpty()) {
            panel.add(new JSeparator(), CC.xyw(2, index, 4, "fill,bottom"));
        } else {
            panel.add(new JLabelSeparator(name, font), CC.xyw(2, index, 4, "fill,bottom"));
        }
    }

    public void addText(String name) {
        addRow();
        int index = layout.getRowCount();
        addEmptyRow();
        if ((name == null) || name.isEmpty()) {
            panel.add(createLabel(""), CC.xyw(2, index, 4, "fill,bottom"));
        } else {
            panel.add(createLabel(name), CC.xyw(2, index, 4, "fill,bottom"));
        }
    }

    public void add(Component c) {
        add(c, false);
    }

    public void add(String c) {
        add(createLabel(c), false);
    }

    public void add(Component c, boolean indent) {
        add((Component) null, c, indent);
    }

    public void add(String name, Component c) {
        add(name, c, false);
    }

    public void add(String name, long text) {
        add(name, "" + text);
    }

    public void add(String name, short text) {
        add(name, "" + text);
    }

    public void add(String name, int text) {
        add(name, "" + text);
    }

    public void add(String name, double text) {
        add(name, NumberFormat.getNumberInstance().format(text));
    }

    public void add(String name, float text) {
        add(name, NumberFormat.getNumberInstance().format(text));
    }

    private JLabel createLabel(String value) {
        JLabel l = new JLabel(value);
        if (font != null) {
            l.setFont(font);
        }
        if (color != null) {
            l.setForeground(color);
        }
        return l;
    }

    public void add(String name, String value) {
        add(name, (value == null ? null : createLabel(value)));
    }

    public void add(String name, String value, boolean intend) {
        add(name, (value == null ? null : createLabel(value)), intend);
    }

    public void add(String name, Component c, boolean indent) {
        add(name == null ? null : createLabel(name), c, indent);
    }

    public void add(Component name, Component c) {
        add(name, c, false);
    }

    public void add(Component name, Component c, boolean indent) {
        add(name, c, indent, null, null);
    }

    public void add(String name, Component c, String cc1, String cc2) {
        add(name == null ? null : createLabel(name), c, false, cc1, cc2);
    }

    public void add(String name, Component c, boolean indent, String cc1, String cc2) {
        add(name == null ? null : createLabel(name), c, indent, cc1, cc2);
    }

    public void addSpanning(Component c) {
        addRow();
        int index = layout.getRowCount();
        addEmptyRow();
        if (c != null) {
            if (font != null) {
                c.setFont(font);
            }
            panel.add(c, CC.xyw(2, index, 4));
        }
    }

    public void add(Component name, Component c, boolean indent, String cc1, String cc2) {
        if (cc1 == null) {
            cc1 = "fill,fill";
        }
        if (cc2 == null) {
            cc2 = "fill,fill";
        }
        addRow();
        int index = layout.getRowCount();
        addEmptyRow();
        if (name != null) {
            panel.add(name, CC.xy(2, index, cc1));
        }
        if (c != null) {
            if (font != null) {
                c.setFont(font);
            }
            if (indent) {
                panel.add(c, CC.xy(5, index, cc2));
            } else {
                panel.add(c, CC.xyw(4, index, 2, cc2));
            }
        }
    }

    public void add(String name, Component c, GrowModel grow) {
        add(name == null ? null : createLabel(name), c, grow);
    }

    public void add(Component name, Component c, GrowModel grow) {
        addRow(false, grow != GrowModel.None);
        int index = layout.getRowCount();
        int height = 2;
        String contraint = "fill,default";
        switch (grow) {
        case TrippleSize:
            height = 3;
            addRow(false);
            addRow(false);
            break;
        case DoubleSize:
            addRow(false);
            break;
        case None:
            // addRow(false, false);
            contraint = "fill,top";
            break;
        case Resize:
        default:
            addRow(true);
        }
        addEmptyRow();

        if (name != null) {
            panel.add(name, CC.xy(2, index));
        }
        if (c != null) {
            if (font != null) {
                c.setFont(font);
            }
            panel.add(c, CC.xywh(4, index, 2, height, contraint));
        }
    }

    private void addEmptyRow() {
        addRow(spacer);
    }

    public void addRow() {
        addRow(false);
    }

    private void addRow(boolean grow) {
        addRow(grow, true);
    }

    private void addRow(boolean grow, boolean doGroup) {
        addRow("fill:default" + (grow ? ":grow" : ""));
        if (doGroup) {
            if (grow) {
                grows.addLast(layout.getRowCount());
            } else {
                fills.addLast(layout.getRowCount());
            }
        }
    }

    private void addRow(String spec) {
        layout.appendRow(RowSpec.decode(spec));
    }

    private int[] getFilledRows() {
        Integer[] ints = fills.toArray(new Integer[fills.size()]);
        int[] result = new int[ints.length];
        for (int x = 0; x < ints.length; x++) {
            result[x] = ints[x];
        }
        return result;
    }

    private int[] getGrowingRows() {
        Integer[] ints = grows.toArray(new Integer[grows.size()]);
        int[] result = new int[ints.length];
        for (int x = 0; x < ints.length; x++) {
            result[x] = ints[x];
        }
        return result;
    }

    public JPanel getPanel() {
        if (group) {
            int[] filledRows = getFilledRows();
            int[] growingRows = getGrowingRows();

            if (filledRows.length <= 1 && growingRows.length <= 1) {
                // Nothing to do
            } else if (filledRows.length > 1 && growingRows.length > 1) {
                layout.setRowGroups(new int[][] { filledRows, growingRows });
            } else if (filledRows.length > 1) {
                layout.setRowGroups(new int[][] { filledRows });
            } else {
                layout.setRowGroups(new int[][] { growingRows });
            }
        }
        return panel;
    }

    public int getRowCount() {
        return layout.getRowCount();
    }
}
