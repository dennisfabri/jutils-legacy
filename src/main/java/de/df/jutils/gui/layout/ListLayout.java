package de.df.jutils.gui.layout;

/*
 * @(#)SimpleTableLayout.java is based on FlowLayout.java 1.44 02/04/17
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved. SUN
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.*;
import java.io.Serializable;

public class ListLayout implements LayoutManager, Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3762532325866419509L;

    /**
     * The flow layout manager allows a seperation of components with gaps. The
     * vertical gap will specify the space between rows.
     * 
     * @serial
     * @see getVgap
     * @see setVgap
     */
    private int               vgap;

    public ListLayout() {
        this(5);
    }

    /**
     * Constructs a new <code>LineLayout</code> with a centered alignment and a
     * default 5-unit horizontal and vertical gap.
     */
    public ListLayout(int vegap) {
        this.vgap = vegap;
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

    /**
     * Sets the vertical gap between components.
     * 
     * @param vgap
     *            the vertical gap between components
     * @see java.awt.LineLayout#getVgap
     */
    public void setVgap(int vegap) {
        this.vgap = vegap;
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

            max.height = (max.height + vgap) * target.getComponentCount() - vgap + i.bottom + i.top;
            max.width += i.left + i.right;

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
            int posx = i.left;
            int posy = i.top;

            for (Component component : components) {
                component.setSize(width, max.height);
                component.setLocation(posx, posy);
                posy += max.height + vgap;
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
        return getClass().getName() + "[vgap=" + vgap + "]";
    }
}