/*
 * Created on 04.04.2005
 */
package de.df.jutils.gui.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.border.ExtendedLineBorder;
import de.df.jutils.gui.jtable.JTableUtils;
import de.df.jutils.gui.jtable.SimpleTableModel;
import de.df.jutils.gui.util.UIUtils;
import de.df.jutils.i18n.util.JUtilsI18n;

public class JAboutDialog extends JDialog {

    private static final long serialVersionUID = 3256441408645576248L;

    private final JFrame      parent;

    public JAboutDialog(JFrame parent, String title, Image image, JComponent[] components) {
        this(parent, title, (image != null ? new ImageIcon(image) : null), components);
    }

    public JAboutDialog(JFrame parent, String title, Icon image, JComponent[] components) {
        super(parent, true);
        this.parent = parent;
        setTitle(title);
        addActions();
        init(image, components);
        setSize(Math.max(300, getWidth()), Math.max(400, getHeight()));
        setSize(Math.min(800, getWidth()), Math.min(600, getHeight()));
    }

    @Override
    public void setVisible(boolean b) {
        if (parent != null) {
            parent.setEnabled(!b);
        }
        super.setVisible(b);
    }

    private void init(Icon image, JComponent[] components) {
        int count = 0;

        JLabel imageLabel = null;
        JComponent info = null;

        if (image != null) {
            count++;

            imageLabel = new JLabel(image);
            imageLabel.setBackground(UIUtils.HEADER_BACKGROUND);
            imageLabel.setOpaque(true);
            imageLabel.setBorder(
                    new CompoundBorder(new ExtendedLineBorder(UIUtils.HEADER_BORDERCOLOR, 0, 0, 1, 0), new ExtendedLineBorder(Color.WHITE, 4, 0, 8, 0)));
        }
        if ((components != null) && (components.length > 0)) {
            count++;

            JTabbedPane tabs = new JTabbedPane();
            for (JComponent component1 : components) {
                JScrollPane pane = new JScrollPane(component1);
                pane.setBorder(null);
                tabs.add(pane, component1.getName());
            }
            info = tabs;
        }
        if (count == 0) {
            throw new IllegalStateException();
        }

        StringBuilder vertical = new StringBuilder();
        if (imageLabel != null) {
            vertical.append("0dlu,fill:default,");
        }
        if (info != null) {
            vertical.append("4dlu,fill:default:grow,");
        }
        vertical.append("4dlu,fill:default,4dlu");

        FormLayout layout = new FormLayout("4dlu,fill:default:grow,4dlu", vertical.toString());
        setLayout(layout);
        int pos = 2;
        if (imageLabel != null) {
            add(imageLabel, CC.xyw(1, 2, 3, "fill,center"));
            pos += 2;
        }
        if (info != null) {
            add(info, CC.xy(2, pos));
            pos += 2;
        }
        JButton close = new JButton(JUtilsI18n.get("Close"));
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });
        add(close, CC.xy(2, pos, "right,fill"));
        pack();
    }

    private static Object[][] getSystemInfos() {
        Enumeration<Object> props = System.getProperties().keys();
        LinkedList<String> keys = new LinkedList<String>();
        while (props.hasMoreElements()) {
            String key = (String) props.nextElement();
            keys.addLast(key);
        }
        Collections.sort(keys);
        LinkedList<String> values = new LinkedList<String>();
        ListIterator<String> li = keys.listIterator();
        while (li.hasNext()) {
            values.addLast(System.getProperty(li.next()));
        }

        Object[][] result = new String[keys.size()][2];
        ListIterator<String> key = keys.listIterator();
        ListIterator<String> value = values.listIterator();

        for (int x = 0; x < result.length; x++) {
            result[x][0] = key.next();
            result[x][1] = value.next();
        }
        return result;
    }

    private static Object[][] getUISettings() {
        UIDefaults uiDefaults = UIManager.getDefaults();
        Enumeration<Object> props = uiDefaults.keys();
        LinkedList<String> keys = new LinkedList<String>();
        LinkedList<Object> values = new LinkedList<Object>();
        while (props.hasMoreElements()) {
            Object key = props.nextElement();
            String k = key.toString();
            if ((!k.startsWith("java.lang.Object")) && (!k.endsWith(".icon"))) {
                try {
                    Object o = uiDefaults.get(k);
                    if (o != null) {
                        keys.addLast(k);
                    }
                } catch (RuntimeException re) {
                    // Nothing to do
                }
            }
        }
        Collections.sort(keys);
        ListIterator<String> li = keys.listIterator();
        while (li.hasNext()) {
            String key = li.next();
            Object o = uiDefaults.get(key);
            values.addLast(o);
        }
        Object[][] result = new Object[keys.size()][2];
        ListIterator<String> key = keys.listIterator();
        ListIterator<Object> value = values.listIterator();

        for (int x = 0; x < result.length; x++) {
            result[x][0] = key.next();
            result[x][1] = value.next();
        }
        return result;
    }

    public static JTable getSystemInformation() {
        return getSystemInformation(new String[] { "Systeminformation", "Property", "Value" });
    }

    public static JTable getSystemInformation(String[] names) {
        if ((names == null) || (names.length < 3)) {
            return getSystemInformation();
        }
        JTable result = new JTable(new SimpleTableModel(getSystemInfos(), new String[] { names[1], names[2] }));
        JTableUtils.setAlternatingTableCellRenderer(result);
        result.setName(names[0]);
        return result;
    }

    public static JTable getUIInformation() {
        return getUIInformation(new String[] { "Property", "Value" });
    }

    public static JTable getUIInformation(String[] names) {
        if ((names == null) || (names.length < 3)) {
            return getUIInformation();
        }
        JTable result = new JTable(new SimpleTableModel(getUISettings(), new String[] { names[1], names[2] }));
        TableCellRenderer crenderer = new UITableCellRenderer();
        result.setDefaultRenderer(Object.class, crenderer);
        JTableUtils.setAlternatingTableCellRenderer(result);
        result.setName(names[0]);
        return result;
    }

    private void addActions() {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 3257572818995525944L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    static final class UITableCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = -1755780817170708793L;
        private JButton           b                = new JButton();

        private boolean checkFontUI(JLabel l, Object value) {
            if (value instanceof FontUIResource) {
                FontUIResource cui = (FontUIResource) value;
                String s = cui.getName() + " (" + cui.getSize() + ", ";

                switch (cui.getStyle()) {
                case Font.BOLD:
                    s += "bold";
                    break;
                case Font.PLAIN:
                    s += "plain";
                    break;
                case Font.ITALIC:
                    s += "italic";
                    break;
                case Font.BOLD + Font.ITALIC:
                    s += "bold italic";
                    break;
                default:
                    s += cui.getStyle();
                    break;
                }
                s += ")";
                l.setText(s);
                return true;
            }
            return false;
        }

        private boolean checkInsetsUI(JLabel l, Object value) {
            if (value instanceof InsetsUIResource) {
                InsetsUIResource cui = (InsetsUIResource) value;
                String s = "InsetsUI ";
                s += "[top = " + cui.top;
                s += ", right = " + cui.right;
                s += ", bottom = " + cui.bottom;
                s += ", left = " + cui.left;
                s += "]";
                l.setText(s);
                return true;
            }
            if (value instanceof Insets) {
                Insets cui = (Insets) value;
                String s = "Insets ";
                s += "[top = " + cui.top;
                s += ", right = " + cui.right;
                s += ", bottom = " + cui.bottom;
                s += ", left = " + cui.left;
                s += "]";
                l.setText(s);
                return true;
            }
            return false;
        }

        private boolean checkIcon(JLabel l, Object value) {
            if (value instanceof Icon) {
                try {
                    Icon cui = (Icon) value;
                    BufferedImage i = new BufferedImage(cui.getIconWidth(), cui.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics g = i.getGraphics();
                    cui.paintIcon(b, g, 0, 0);
                    l.setIcon(new ImageIcon(i));
                } catch (RuntimeException re) {
                    // Nothing to do
                }
                return true;
            }
            return false;
        }

        private boolean checkColorUI(JLabel l, Object value) {
            Color color = null;
            if (value instanceof Color) {
                color = (Color) value;
            }
            if (value instanceof ColorUIResource) {
                ColorUIResource cui = (ColorUIResource) value;
                int rgb = cui.getRGB();
                color = new Color(rgb);
            }
            if (color == null) {
                return false;
            }
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();

            BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            g.setColor(color);
            g.fillRect(0, 0, 10, 10);
            ImageIcon icon = new ImageIcon(image);

            l.setText("[" + red + ", " + green + ", " + blue + "]");
            l.setIcon(icon);
            return true;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            JLabel l = null;
            if (c instanceof JLabel) {
                l = (JLabel) c;
                l.setIcon(null);
            } else {
                l = new JLabel();
                l.setForeground(c.getForeground());
                l.setBackground(c.getBackground());
            }
            if (checkColorUI(l, value)) {
                return l;
            }
            if (checkFontUI(l, value)) {
                return l;
            }
            if (checkInsetsUI(l, value)) {
                return l;
            }
            if (checkIcon(l, value)) {
                return l;
            }

            return l;
        }
    }
}