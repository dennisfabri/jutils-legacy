/*
 * Created on 14.04.2005
 */
package de.df.jutils.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.util.GraphicsUtils;

public class JGradientLabel extends JPanel {

    private static final long serialVersionUID = 5455429979750091697L;

    private Color             left             = null;
    private Color             right            = null;
    private JLabel            label            = null;

    public JGradientLabel() {
        label = new JLabel();
        init();
    }

    public JGradientLabel(Icon image, int horizontalAlignment) {
        label = new JLabel(image, horizontalAlignment);
        init();
    }

    public JGradientLabel(Icon image) {
        label = new JLabel(image);
        init();
    }

    public JGradientLabel(String text, Icon icon, int horizontalAlignment) {
        label = new JLabel(text, icon, horizontalAlignment);
        init();
    }

    public JGradientLabel(String text, int horizontalAlignment) {
        label = new JLabel(text, horizontalAlignment);
        init();
    }

    public JGradientLabel(String text) {
        label = new JLabel(text);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        label.setBorder(new EmptyBorder(0, 5, 0, 5));
        // label.setForeground(UIManager.getColor("textHighlightText"));
        add(label, BorderLayout.CENTER);
        setOpaque(true);
        left = UIManager.getColor("textHighlight");
        right = getBackground();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // prepare colors
        Color start = left;
        if (start == null) {
            start = ColorUtils.calculateColor(UIManager.getColor("Panel.background"), Color.BLACK, 0.1);
        }
        Color end = right;
        if (right == null) {
            end = getBackground();
        }

        GraphicsUtils.paintGradient((Graphics2D) g, 0, 0, getWidth(), getHeight() - 1, start, end);
    }

    public Color getLeftColor() {
        return left;
    }

    public Color getRightColor() {
        return right;
    }

    public void setLeftColor(Color left) {
        this.left = left;
    }

    public void setRightColor(Color right) {
        this.right = right;
    }
}