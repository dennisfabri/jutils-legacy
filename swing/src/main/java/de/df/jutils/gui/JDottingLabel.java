/*
 * Created on 03.05.2005
 */
package de.df.jutils.gui;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JLabel;

public class JDottingLabel extends JLabel {

    private static final long serialVersionUID = 3258411742123013683L;

    private JLabel            full;
    private JLabel            min;
    private JLabel            draw;

    private boolean           disabled         = false;

    public JDottingLabel() {
        this("");
    }

    public JDottingLabel(String text) {
        super(text);
        full = new JLabel(text);
        min = new JLabel("...");
        draw = new JLabel("");
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (disabled) {
            super.paintComponent(g);
            return;
        }
        FontMetrics fm = g.getFontMetrics();
        int width = getWidth();
        String text = getText();
        String s = text;
        int length = s.length();
        while (fm.getStringBounds(s, g).getWidth() > width) {
            if (length == 0) {
                s = "...";
                break;
            }
            length--;
            s = text.substring(0, length) + "...";
        }

        draw.setText(s);
        draw.setLocation(getLocation());
        draw.setSize(getSize());
        draw.setFont(getFont());
        draw.setAlignmentX(getAlignmentX());
        draw.setAlignmentY(getAlignmentY());
        draw.setBackground(getBackground());
        draw.setForeground(getForeground());
        draw.setBorder(getBorder());
        draw.setBounds(getBounds());
        draw.setComponentOrientation(getComponentOrientation());
        draw.setDisabledIcon(getDisabledIcon());
        draw.setDisplayedMnemonic(draw.getDisplayedMnemonic());
        draw.setHorizontalAlignment(getHorizontalAlignment());
        draw.setHorizontalTextPosition(getHorizontalTextPosition());
        draw.setIcon(getIcon());
        draw.setIconTextGap(getIconTextGap());
        draw.setIgnoreRepaint(getIgnoreRepaint());
        draw.setLabelFor(getLabelFor());
        draw.setName(getName());
        draw.setOpaque(isOpaque());
        draw.setVerticalAlignment(getVerticalAlignment());
        draw.setVerticalTextPosition(getVerticalTextPosition());
        draw.setToolTipText(getToolTipText());

        draw.paint(g);
    }

    @Override
    public Dimension getMinimumSize() {
        return min.getMinimumSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return full.getMinimumSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return min.getPreferredSize();
    }
}