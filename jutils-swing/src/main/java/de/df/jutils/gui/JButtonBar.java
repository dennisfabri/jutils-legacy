package de.df.jutils.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class JButtonBar extends JPanel {

    private static final long serialVersionUID = 6139512086453122207L;

    static final int MARGIN = 6;
    private static final int GAP = 2;
    private static final int PADDING_VERTICAL = 4;
    private static final int PADDING_HORIZONTAL = 8;

    private static final String PLAIN_TEXT = "JButtonBar.plainText";
    private static final String WRAPPED_TEXT = "JButtonBar.wrappedText";
    private static final int DEFAULT_MAX_TEXT_WIDTH = 140;

    private final int orientation;
    private int maxTextWidth = DEFAULT_MAX_TEXT_WIDTH;
    private transient boolean wrapping;
    private final transient HoverHighlight highlight = new HoverHighlight();
    private transient AbstractButton hovered;

    public JButtonBar() {
        this(SwingConstants.HORIZONTAL);
    }

    public JButtonBar(int orientation) {
        super(null);
        if (orientation != SwingConstants.HORIZONTAL && orientation != SwingConstants.VERTICAL) {
            throw new IllegalArgumentException("orientation must be HORIZONTAL or VERTICAL");
        }
        this.orientation = orientation;
        setLayout(new UniformLayout());
        setBackground(HoverHighlight.barBackground());
        setOpaque(true);
    }

    public int getOrientation() {
        return orientation;
    }

    public int getMaxTextWidth() {
        return maxTextWidth;
    }

    public void setMaxTextWidth(int maxTextWidth) {
        this.maxTextWidth = maxTextWidth;
        for (Component child : getComponents()) {
            if (child instanceof AbstractButton button) {
                rewrap(button);
            }
        }
        revalidate();
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof AbstractButton button) {
            makeFlat(button);
        }
        super.addImpl(comp, constraints, index);
    }

    private void makeFlat(AbstractButton button) {
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setVerticalAlignment(SwingConstants.TOP);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(highlight.border(false),
                BorderFactory.createEmptyBorder(PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_VERTICAL,
                        PADDING_HORIZONTAL)));
        rewrap(button);
        button.addPropertyChangeListener(e -> {
            if (!wrapping && ("text".equals(e.getPropertyName()) || "font".equals(e.getPropertyName()))) {
                rewrap(button);
            }
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = button;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hovered == button) {
                    hovered = null;
                }
                repaint();
            }
        });
        button.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    private void rewrap(AbstractButton button) {
        String text = button.getText();
        String plain = text != null && text.equals(button.getClientProperty(WRAPPED_TEXT))
                ? (String) button.getClientProperty(PLAIN_TEXT)
                : text;
        if (plain == null || plain.regionMatches(true, 0, "<html>", 0, 6)) {
            button.putClientProperty(PLAIN_TEXT, null);
            button.putClientProperty(WRAPPED_TEXT, null);
            return;
        }
        List<String> lines = ButtonTextWrapper.wrap(plain, button.getFontMetrics(button.getFont()), maxTextWidth);
        String result = lines.size() > 1 ? ButtonTextWrapper.toHtml(lines) : plain;
        button.putClientProperty(PLAIN_TEXT, plain);
        button.putClientProperty(WRAPPED_TEXT, lines.size() > 1 ? result : null);
        if (!result.equals(text)) {
            wrapping = true;
            try {
                button.setText(result);
            } finally {
                wrapping = false;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            for (Component child : getComponents()) {
                if (child instanceof AbstractButton button && isHighlighted(button)) {
                    highlight.paint(g2d, child.getX(), child.getY(), child.getWidth(), child.getHeight());
                }
            }
        } finally {
            g2d.dispose();
        }
    }

    private boolean isHighlighted(AbstractButton button) {
        return button == hovered || button.isSelected() || button.hasFocus();
    }

    private final class UniformLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
            // Nothing to do
        }

        @Override
        public void removeLayoutComponent(Component comp) {
            // Nothing to do
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return minimumLayoutSize(parent);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            Dimension cell = cellSize(parent);
            int count = visibleCount(parent);
            if (count == 0) {
                return new Dimension(0, 0);
            }
            int gaps = (count - 1) * GAP;
            Insets insets = parent.getInsets();
            int extraWidth = insets.left + insets.right + 2 * MARGIN;
            int extraHeight = insets.top + insets.bottom + 2 * MARGIN;
            if (orientation == SwingConstants.HORIZONTAL) {
                return new Dimension(count * cell.width + gaps + extraWidth, cell.height + extraHeight);
            }
            return new Dimension(cell.width + extraWidth, count * cell.height + gaps + extraHeight);
        }

        @Override
        public void layoutContainer(Container parent) {
            Insets insets = parent.getInsets();
            int availableWidth = parent.getWidth() - insets.left - insets.right - 2 * MARGIN;
            int availableHeight = parent.getHeight() - insets.top - insets.bottom - 2 * MARGIN;
            Dimension natural = cellSize(parent);
            Dimension cell = orientation == SwingConstants.VERTICAL
                    ? new Dimension(Math.max(natural.width, availableWidth), natural.height)
                    : new Dimension(natural.width, Math.max(natural.height, availableHeight));
            int x = insets.left + MARGIN;
            int y = insets.top + MARGIN;
            for (Component child : parent.getComponents()) {
                if (!child.isVisible()) {
                    continue;
                }
                child.setBounds(x, y, cell.width, cell.height);
                if (orientation == SwingConstants.HORIZONTAL) {
                    x += cell.width + GAP;
                } else {
                    y += cell.height + GAP;
                }
            }
        }

        private Dimension cellSize(Container parent) {
            int width = 0;
            int height = 0;
            for (Component child : parent.getComponents()) {
                if (child.isVisible()) {
                    Dimension d = child.getPreferredSize();
                    width = Math.max(width, d.width);
                    height = Math.max(height, d.height);
                }
            }
            return new Dimension(width, height);
        }

        private int visibleCount(Container parent) {
            int count = 0;
            for (Component child : parent.getComponents()) {
                if (child.isVisible()) {
                    count++;
                }
            }
            return count;
        }
    }
}
