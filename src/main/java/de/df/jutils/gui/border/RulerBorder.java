/*
 * Created on 18.03.2007
 */
package de.df.jutils.gui.border;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

import de.df.jutils.print.PageSetup;

public class RulerBorder implements Border {

    private static final String NUMBERS = "1234567890";

    private JLabel              label   = new JLabel(NUMBERS);

    public RulerBorder() {
        JFrame f = new JFrame();
        f.add(label);
        f.pack();
    }

    @Override
    public Insets getBorderInsets(final Component arg0) {
        return new Insets(label.getHeight() + 3, label.getHeight() + 3, 0, 0);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics graphics, int x, int y, int width, int height) {

        Graphics2D g = (Graphics2D) graphics.create();

        int h = label.getHeight() + 2;

        g.setColor(Color.WHITE);
        g.fillRect(x, y, h, height);
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, h);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, h, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, h);

        FontMetrics fm = g.getFontMetrics();
        double textheight = fm.getLineMetrics(NUMBERS, g).getAscent();
        double offset = (textheight + h - 1) / 2.0;

        double pos = h;
        int counter = 0;
        double inch = 2.54;
        double stepsize = PageSetup.DPI / inch / 4;

        g.translate(1, 0);

        while (pos + x + stepsize < width) {
            pos += stepsize;
            counter++;
            switch (counter % 4) {
            default:
            case 0:
                String s = "" + (counter / 4);
                g.drawString(s, (int) Math.round(pos - 0.5 * fm.stringWidth(s) + x), (int) Math.round(offset + y));
                break;
            case 1:
            case 3:
                g.drawLine((int) Math.round(pos + x), (int) Math.round(h / 2.0 - 1 + y), (int) Math.round(pos + x), (int) Math.round(h / 2.0 + 1 + y));
                break;
            case 2:
                g.drawLine((int) Math.round(pos + y), (int) Math.round(h / 2.0 - 2 + y), (int) Math.round(pos + x), (int) Math.round(h / 2.0 + 2 + y));
                break;
            }
        }

        g.translate(-1, 1);

        pos = h;
        counter = 0;
        while (pos + y + stepsize < height) {
            pos += stepsize;
            counter++;
            switch (counter % 4) {
            default:
            case 0:
                String s = "" + (counter / 4);
                g.rotate(-Math.PI / 2);
                g.drawString(s, (int) -Math.round(pos + fm.stringWidth(s) / 2.0 + y), (int) Math.round(offset + x));
                g.rotate(Math.PI / 2);
                break;
            case 1:
            case 3:
                g.drawLine((int) Math.round(h / 2.0 - 1 + x), (int) Math.round(pos + y), (int) Math.round(h / 2.0 + 1 + x), (int) Math.round(pos + y));
                break;
            case 2:
                g.drawLine((int) Math.round(h / 2.0 - 2 + x), (int) Math.round(pos + y), (int) Math.round(h / 2.0 + 2 + x), (int) Math.round(pos + y));
                break;
            }
        }

        g.translate(0, -1);
    }
}