package de.df.jutils.gui.layout;

/*
 * @(#)SimpleTableLayout.java is based on FlowLayout.java 1.44 02/04/17
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved. SUN
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.io.Serializable;

public class CenterLayout implements LayoutManager, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3546358444190937657L;

    /**
     * The flow layout manager allows a seperation of components with gaps. The
     * horizontal gap will specify the space between components.
     * 
     * @serial
     * @see getHgap
     * @see setHgap
     */
    private int               hgap;

    /**
     * The flow layout manager allows a seperation of components with gaps. The
     * vertical gap will specify the space between rows.
     * 
     * @serial
     * @see getVgap
     * @see setVgap
     */
    private int               vgap;

    public CenterLayout() {
        this(5, 5);
    }

    public CenterLayout(int hogap, int vegap) {
        hgap = hogap;
        vgap = vegap;
    }

    /**
     * Gets the horizontal gap between components.
     * 
     * @return the horizontal gap between components
     * @see java.awt.LineLayout#setHgap
     * @since JDK1.1
     */
    public int getHgap() {
        return hgap;
    }

    /**
     * Sets the horizontal gap between components.
     * 
     * @param hgap
     *            the horizontal gap between components
     * @see java.awt.LineLayout#getHgap
     * @since JDK1.1
     */
    public void setHgap(int hogap) {
        this.hgap = hogap;
    }

    /**
     * Gets the vertical gap between components.
     * 
     * @return the vertical gap between components
     * @see java.awt.LineLayout#setVgap
     * @since JDK1.1
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
     * @since JDK1.1
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
            if (target.getComponentCount() == 0) {
                return new Dimension(2 * hgap, 2 * vgap);
            }
            Dimension x = target.getComponent(0).getPreferredSize();
            return new Dimension((int) x.getWidth() + 2 * hgap, (int) x.getHeight() + 2 * vgap);
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
        synchronized (target.getTreeLock()) {
            if (target.getComponentCount() == 0) {
                return new Dimension(2 * hgap, 2 * vgap);
            }
            Dimension x = target.getComponent(0).getMinimumSize();
            return new Dimension((int) x.getWidth() + 2 * hgap, (int) x.getHeight() + 2 * vgap);
        }
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
            Component[] c = target.getComponents();
            if (c.length == 0) {
                return;
            }

            for (int x = 1; x < c.length; x++) {
                c[x].setLocation(target.getWidth() + 1000, target.getHeight() + 1000);
            }

            Dimension s1 = c[0].getPreferredSize();
            Dimension s2 = target.getSize();

            int width = (int) s1.getWidth();
            int height = (int) s1.getHeight();
            if (width > s2.getWidth() - 2 * hgap) {
                width = (int) s2.getWidth() - 2 * hgap;
            }
            if (height > s2.getHeight() - 2 * vgap) {
                height = (int) s2.getHeight() - 2 * vgap;
            }
            int x = (int) (s2.getWidth() - width) / 2;
            int y = (int) (s2.getHeight() - height) / 2;
            c[0].setLocation(x, y);
            c[0].setSize(width, height);
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
        return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
