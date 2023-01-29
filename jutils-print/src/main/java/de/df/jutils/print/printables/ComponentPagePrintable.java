/*
 * Created on 02.01.2005
 */
package de.df.jutils.print.printables;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.RepaintManager;

import de.df.jutils.gui.util.EDTUtils;

/**
 * Generic printing utility.
 * <p>
 * The original source code for this class only was obtained from
 * http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html
 * without restrictions as specified on their web site. To quote the link above:
 * <p>
 * <i>All source code freely available for unrestricted use. </i>
 * <p>
 * However, additional changes have been made to the original.
 */
public class ComponentPagePrintable implements Printable {

    private final Component[] components;
    private final boolean scaleDown;

    public ComponentPagePrintable(Component... components) {
        this(false, components);
    }

    public ComponentPagePrintable(boolean scaleDown, Component... components) {
        this.scaleDown = scaleDown;
        this.components = components;
    }

    public static void printComponent(Component component, Graphics g, PageFormat pageFormat) {
        printComponent(component, g, pageFormat, false);
    }

    public static void printComponent(Component component, Graphics g, PageFormat pageFormat, boolean scaleDown) {

        double x = pageFormat.getImageableX();
        double y = pageFormat.getImageableY();
        int width = (int) pageFormat.getImageableWidth();
        int height = (int) pageFormat.getImageableHeight();

        Dimension d = new Dimension(width, height);

        component.setMinimumSize(d);

        double scale = 0;
        if (scaleDown) {
            Dimension preferred = component.getPreferredSize();
            double h = ((double) preferred.height) / (double) height;
            double w = ((double) preferred.width) / (double) width;

            scale = Math.max(h, w);
            if (scale > 1.0) {
                d.height *= scale;
                d.width *= scale;
                component.setMinimumSize(d);
            }
        }
        component.setPreferredSize(d);
        component.setMaximumSize(d);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(x, y);
        if (scale > 1.0) {
            g2d.scale(1.0 / scale, 1.0 / scale);
        }

        component.addNotify();
        component.setSize(d);
        // component.doLayout();
        component.validate();

        // Wait for all events in the EDT to be executed
        EDTUtils.sleep();

        disableDoubleBuffering(component);
        EDTUtils.print(component, g2d);
        enableDoubleBuffering(component);

        Runtime.getRuntime().gc();
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex >= components.length) {
            return NO_SUCH_PAGE;
        }
        printComponent(components[pageIndex], g, pageFormat, scaleDown);
        return PAGE_EXISTS;
    }

    private static void disableDoubleBuffering(Component c) {
        RepaintManager.currentManager(c).setDoubleBufferingEnabled(false);
    }

    private static void enableDoubleBuffering(Component c) {
        RepaintManager.currentManager(c).setDoubleBufferingEnabled(true);
    }
}
