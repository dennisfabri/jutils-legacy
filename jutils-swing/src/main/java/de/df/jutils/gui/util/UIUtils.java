package de.df.jutils.gui.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.border.ExtendedLineBorder;

public final class UIUtils {

    public static final Font HEADER_FONT;
    public static final Color HEADER_BACKGROUND;
    public static final Color HEADER_FOREGROUND;
    public static final Color HEADER_BORDERCOLOR;

    static {
        HEADER_FOREGROUND = SystemColor.textHighlight.darker(); // ???
        Font font = UIManager.getFont("Label.font");
        HEADER_FONT = font.deriveFont(font.getSize() * 1.4f);
        Color b = UIManager.getColor("List.Background");
        if (b == null) {
            b = Color.WHITE;
        }
        HEADER_BACKGROUND = b;
        Color c = UIManager.getColor("Button.Shadow");
        if (c == null) {
            c = Color.GRAY;
        }
        HEADER_BORDERCOLOR = c;
    }

    public static JScrollPane surroundWithScroller(JComponent c) {
        return surroundWithScroller(c, true, true);
    }

    public static JScrollPane surroundWithScroller(JComponent c, boolean horizontal, boolean vertical) {
        return surroundWithScroller(c, 10, horizontal, vertical);
    }

    private static JScrollPane surroundWithScroller(JComponent c, int unitIncrement, boolean horizontal,
            boolean vertical) {
        JScrollPane scroller = new JScrollPane(c);
        if (unitIncrement > 0) {
            scroller.getHorizontalScrollBar().setUnitIncrement(unitIncrement);
            scroller.getVerticalScrollBar().setUnitIncrement(unitIncrement);
        }
        if (!vertical) {
            scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        }
        if (!horizontal) {
            scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        }
        scroller.setBorder(null);
        return scroller;
    }

    public static JPanel createHeaderPanel(String text, String note) {
        FormLayout layout = null;
        if (note == null || note.length() == 0) {
            note = null;
            layout = new FormLayout("4dlu,fill:default:grow,4dlu", "4dlu,fill:default,10dlu");
        } else {
            layout = new FormLayout("4dlu,fill:default:grow,4dlu", "4dlu,fill:default,4dlu,fill:default,8dlu");
        }
        JPanel info = new JPanel(layout);

        info.add(createHeaderLabel(text), CC.xy(2, 2));
        if (note != null) {
            info.add(new JLabel(note), CC.xy(2, 4));
        }

        info.setBackground(UIUtils.HEADER_BACKGROUND);
        info.setBorder(new ExtendedLineBorder(UIUtils.HEADER_BORDERCOLOR, 0, 0, 1, 0));
        return info;
    }

    public static JPanel createInfoPanel(String text, String note) {
        FormLayout layout = null;
        if (note == null || note.length() == 0) {
            note = null;
            layout = new FormLayout("4dlu,fill:default,4dlu,fill:default:grow,4dlu", "4dlu,fill:default,12dlu");
        } else {
            layout = new FormLayout("4dlu,fill:default,4dlu,fill:default:grow,4dlu",
                    "4dlu,fill:default,4dlu,fill:default:grow,12dlu");

        }
        JPanel info = new JPanel(layout);
        info.setBackground(UIUtils.HEADER_BACKGROUND);
        info.setBorder(new ExtendedLineBorder(UIUtils.HEADER_BORDERCOLOR, 0, 0, 1, 0));

        info.add(new JLabel(getInformationIcon()), CC.xywh(2, 2, 1, (note == null ? 1 : 3)));
        info.add(createHeaderLabel(text), CC.xy(4, 2));
        if (note != null) {
            info.add(new JLabel(note), CC.xy(4, 4));
        }
        return info;
    }

    public static JPanel createWarningPanel(String text, String note) {
        FormLayout layout = null;
        if (note == null || note.length() == 0) {
            note = null;
            layout = new FormLayout("4dlu,fill:default,4dlu,fill:default:grow,4dlu", "4dlu,fill:default,12dlu");
        } else {
            layout = new FormLayout("4dlu,fill:default,4dlu,fill:default:grow,4dlu",
                    "4dlu,fill:default,4dlu,fill:default:grow,12dlu");

        }
        JPanel info = new JPanel(layout);
        info.setBackground(UIUtils.HEADER_BACKGROUND);
        info.setBorder(new ExtendedLineBorder(UIUtils.HEADER_BORDERCOLOR, 0, 0, 1, 0));

        info.add(new JLabel(getWarningIcon()), CC.xywh(2, 2, 1, (note == null ? 1 : 3)));
        info.add(createHeaderLabel(text), CC.xy(4, 2));
        if (note != null) {
            info.add(new JLabel(note), CC.xy(4, 4));
        }
        return info;
    }

    private static JLabel createHeaderLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIUtils.HEADER_FONT);
        l.setForeground(UIUtils.HEADER_FOREGROUND);
        return l;
    }

    private static ImageIcon getInformationIcon() {
        Object o = UIManager.getDefaults().get("OptionPane.informationIcon");
        if (o instanceof ImageIcon) {
            return (ImageIcon) o;
        }
        return null;
    }

    private static ImageIcon getWarningIcon() {
        Object o = UIManager.getDefaults().get("OptionPane.warningIcon");
        if (o instanceof ImageIcon) {
            return (ImageIcon) o;
        }
        return null;
    }

    private UIUtils() {
    }
}
