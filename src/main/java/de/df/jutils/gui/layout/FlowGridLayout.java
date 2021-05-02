package de.df.jutils.gui.layout;

/*
 * @(#)SimpleTableLayout.java is based on FlowLayout.java 1.44 02/04/17
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved. SUN
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.io.Serializable;

public class FlowGridLayout implements LayoutManager, Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 8148299278887326043L;

    /**
     * The flow layout manager allows a seperation of components with gaps. The
     * vertical gap will specify the space between rows.
     * 
     * @serial
     * @see getVgap
     * @see setVgap
     */
    private int               vgap;
    private int               hgap;

    /** Maximal number of columns for prefered size calculation */
    private int               maximum;

    public FlowGridLayout() {
        this(5, 5);
    }

    /**
     * Constructs a new <code>LineLayout</code> with a centered alignment and a
     * default 5-unit horizontal and vertical gap.
     */
    public FlowGridLayout(int hegap, int vegap) {
        this(hegap, vegap, 3);
    }

    public FlowGridLayout(int hegap, int vegap, int max) {
        setHgap(hegap);
        setVgap(vegap);
        setMaximum(max);
    }

    public void setMaximum(int x) {
        if (x <= 0) {
            throw new IllegalArgumentException();
        }
        maximum = x;
    }

    /**
     * Gets the vertical gap between components.
     * 
     * @return the vertical gap between components
     * @see java.awt.LineLayout#setVgap
     */
    public int getVgap() {
        return vgap;
    }

    public int getHgap() {
        return hgap;
    }

    /**
     * Sets the vertical gap between components.
     * 
     * @param vgap
     *            the vertical gap between components
     * @see java.awt.LineLayout#getVgap
     */
    public void setVgap(int vegap) {
        if (vegap <= 0) {
            throw new IllegalArgumentException();
        }
        this.vgap = vegap;
    }

    public void setHgap(int h) {
        if (h <= 0) {
            throw new IllegalArgumentException();
        }
        hgap = h;
    }

    /**
     * Adds the specified component to the layout. Not used by this class.
     * 
     * @param name
     *            the name of the component
     * @param comp
     *            the component to be added
     */
    @Override
    public void addLayoutComponent(String name, Component comp) {
        // Not used
    }

    /**
     * Removes the specified component from the layout. Not used by this class.
     * 
     * @param comp
     *            the component to remove
     * @see java.awt.Container#removeAll
     */
    @Override
    public void removeLayoutComponent(Component comp) {
        // Not used
    }

    /**
     * Returns the preferred dimensions for this layout given the <i>visible
     * </i> components in the specified target container.
     * 
     * @param target
     *            the component which needs to be laid out
     * @return the preferred dimensions to lay out the subcomponents of the
     *         specified container
     * @see Container
     * @see #minimumLayoutSize
     * @see java.awt.Container#getPreferredSize
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension max = determineMinMaxWidthAndMaxHeight(target);

            Insets i = target.getInsets();

            int count = target.getComponentCount();
            int horizontal = maximum;
            if (horizontal > count) {
                horizontal = count;
            }
            int vertical = count / horizontal;
            if (count % horizontal > 0) {
                vertical++;
            }

            max.height = (max.height + vgap) * vertical + vgap + i.bottom + i.top;
            max.width = (max.width + hgap) * horizontal + hgap + i.left + i.right;

            return max;
        }
    }

    /**
     * Returns the minimum dimensions needed to layout the <i>visible </i>
     * components contained in the specified target container.
     * 
     * @param target
     *            the component which needs to be laid out
     * @return the minimum dimensions to lay out the subcomponents of the
     *         specified container
     * @see #preferredLayoutSize
     * @see java.awt.Container
     * @see java.awt.Container#doLayout
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        return preferredLayoutSize(target);
    }

    /**
     * Lays out the container. This method lets each component take its
     * preferred size by reshaping the components in the target container in
     * order to satisfy the alignment of this <code>LineLayout</code> object.
     * 
     * @param target
     *            the specified component being laid out
     * @see Container
     * @see java.awt.Container#doLayout
     */
    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Component[] components = target.getComponents();
            Dimension max = determineMinMaxWidthAndMaxHeight(target);

            Insets i = target.getInsets();

            int width = target.getWidth() - i.left - i.right;
            int posx = i.left + hgap;
            int posy = i.top + vgap;

            int horizontal = (width - hgap) / (max.width + hgap);
            if (horizontal < 1) {
                horizontal = 1;
            }
            int vertical = components.length / horizontal;
            if (components.length % horizontal > 0) {
                vertical++;
            }

            int index = 0;
            for (int y = 0; y < vertical; y++) {
                for (int x = 0; x < horizontal; x++) {
                    if (index >= components.length) {
                        break;
                    }
                    Point p = new Point(posx + x * (max.width + hgap), posy + y * (max.height + hgap));
                    components[index].setSize(max.width, max.height);
                    components[index].setLocation(p);

                    index++;
                }
            }
        }
    }

    /**
     * @param sp
     * @param min
     * @param max
     * @return
     */
    private Dimension determineMinMaxWidthAndMaxHeight(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            Component[] components = target.getComponents();
            for (Component component : components) {
                Dimension size = component.getPreferredSize();
                dim.height = Math.max(dim.height, (int) size.getHeight());
                dim.width = Math.max(dim.width, (int) size.getWidth());
            }
            return dim;
        }
    }

    /**
     * Returns a string representation of this <code>LineLayout</code> object
     * and its values.
     * 
     * @return a string representation of this layout
     */
    @Override
    public String toString() {
        return getClass().getName() + "[hgap=" + hgap + ", vgap=" + vgap + "]";
    }
}