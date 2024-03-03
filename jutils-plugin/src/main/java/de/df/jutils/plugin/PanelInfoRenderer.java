package de.df.jutils.plugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.jlist.JHoverList;
import de.df.jutils.gui.util.GraphicsUtils;

final class PanelInfoRenderer implements ListCellRenderer<PanelInfo> {

    private final class PanelInfoRendererPanel extends JPanel {
        private static final long serialVersionUID = -236194141800485289L;

        public PanelInfoRendererPanel() {
            super(new FormLayout("center:default:grow", "1dlu,fill:default,fill:default,1dlu"));
        }

        @Override
        public void paint(Graphics g) {
            if (getBorder() == selected) {
                GraphicsUtils.paintGradient((Graphics2D) g, 1, 1, getWidth() - 2, getHeight() - 2, start, end);
            }
            super.paint(g);
        }
    }

    private final JPanel button = new PanelInfoRendererPanel();
    private final JLabel text = new JLabel();
    private final JLabel image = new JLabel();

    final Border unselected = new EmptyBorder(2, 2, 2, 2);
    final Border selected;

    final Color start;
    final Color end;

    public PanelInfoRenderer() {
        button.add(image, CC.xy(1, 2));
        button.add(text, CC.xy(1, 3));
        button.setOpaque(false);

        Color s = UIManager.getColor("Panel.background");
        Color e = UIManager.getColor("List.dropCellBackground");
        if (s == null) {
            s = UIManager.getColor("InternalFrame.borderColor");
        }
        if (e == null) {
            e = UIManager.getColor("List.selectionBackground");
        }
        Color base = UIManager.getColor("List.background");
        if (base == null) {
            base = Color.WHITE;
        }
        start = s;
        end = ColorUtils.calculateColor(base, e, 0.5);

        selected = new CompoundBorder(new EmptyBorder(1, 1, 1, 1), new LineBorder(end, 1));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends PanelInfo> list, PanelInfo pi, int index,
            boolean isSelected, boolean cellHasFocus) {
        if (!isSelected && (list instanceof JHoverList) && ((JHoverList<? extends PanelInfo>) list).getHoveredItem() == pi) {
            isSelected = true;
        }
        button.setBorder(isSelected ? selected : unselected);
        text.setText("");
        image.setIcon(null);
        text.setText(pi.getName());
        image.setIcon(pi.getIcon());
        return button;
    }
}
