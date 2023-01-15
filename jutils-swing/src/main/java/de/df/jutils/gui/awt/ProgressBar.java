/*
 * Created on 27.09.2005
 */
package de.df.jutils.gui.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

class ProgressBar extends Component {

    private static final long serialVersionUID = 4960265360749246356L;

    private static final Color FOG = new Color(230, 230, 230);
    private static final Color GRAY1 = Color.GRAY;
    private static final Color GRAY2 = Color.GRAY;

    private int min;
    private int max = 1;
    private int value;

    public ProgressBar(int min, int max) {
        setBackground(Color.WHITE);
        setForeground(new Color(184, 207, 229));

        this.min = min;
        this.max = max;
        this.value = min;
    }

    public void setMinimum(int m) {
        if (m > max) {
            max = m;
        }
        if (m > value) {
            value = m;
        }
        max = m;
        repaint();
    }

    public void setMaximum(int m) {
        if (m < min) {
            min = m;
        }
        if (m < value) {
            value = m;
        }
        max = m;
        repaint();
    }

    public void setValue(int v) {
        value = v;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, 16);
    }

    @Override
    public void paint(Graphics g) {
        if (g == null) {
            return;
        }
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g.create();

        int amount = (getWidth() - 2) * (value - min) / (max - min);
        g2d.setColor(GRAY1);
        g2d.setBackground(GRAY2);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2d.setColor(FOG);
        g2d.fillRect(Math.max(1, amount - 2), 1, getWidth() - Math.max(1, amount - 2) - 1, getHeight() - 2);
        g2d.setColor(getForeground());
        g2d.fillRect(1, 1, amount - 2, getHeight() - 2);
    }
}
