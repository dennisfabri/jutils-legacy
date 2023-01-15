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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class SimpleTableLayout implements LayoutManager, Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3762532325866419509L;

    /**
     * The flow layout manager allows a seperation of components with gaps. The
     * horizontal gap will specify the space between components.
     * 
     * @serial
     * @see getHgap
     * @see setHgap
     */
    private int hgap;

    /**
     * The flow layout manager allows a seperation of components with gaps. The
     * vertical gap will specify the space between rows.
     * 
     * @serial
     * @see getVgap
     * @see setVgap
     */
    private int vgap;

    private int spalten;

    private int select;

    /**
     * Constructs a new <code>LineLayout</code> with a centered alignment and a
     * default 5-unit horizontal and vertical gap.
     */
    public SimpleTableLayout(int spaltenanzahl) {
        this(spaltenanzahl, spaltenanzahl - 1, 5, 5);
    }

    public SimpleTableLayout(int spaltenanzahl, int specialcolumn) {
        this(spaltenanzahl, specialcolumn, 5, 5);
    }

    /**
     * Creates a new flow layout manager with the indicated alignment and the
     * indicated horizontal and vertical gaps.
     * <p>
     * The value of the alignment argument must be one of
     * <code>LineLayout.LEFT</code>,<code>LineLayout.RIGHT</code>, or
     * <code>LineLayout.CENTER</code>.
     * 
     * @param align the alignment value
     * @param hgap  the horizontal gap between components
     * @param vgap  the vertical gap between components
     */
    public SimpleTableLayout(int spaltenanzahl, int hogap, int vegap) {
        this(spaltenanzahl, spaltenanzahl - 1, hogap, vegap);
    }

    public SimpleTableLayout(int spaltenanzahl, int specialRow, int hogap, int vegap) {
        this.spalten = spaltenanzahl;
        this.hgap = hogap;
        this.vgap = vegap;
        this.select = specialRow;
    }

    public void setSelectedRow(int row) {
        if (row >= spalten) {
            row = spalten - 1;
        }
        if (row < 0) {
            row = spalten - 1;
        }
        select = row;
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
     * @param hgap the horizontal gap between components
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
     * @param vgap the vertical gap between components
     * @see java.awt.LineLayout#getVgap
     * @since JDK1.1
     */
    public void setVgap(int vegap) {
        this.vgap = vegap;
    }

    /**
     * Adds the specified component to the layout. Not used by this class.
     * 
     * @param name the name of the component
     * @param comp the component to be added
     */
    @Override
    public void addLayoutComponent(String name, Component comp) {
        // Not used
    }

    /**
     * Removes the specified component from the layout. Not used by this class.
     * 
     * @param comp the component to remove
     * @see java.awt.Container#removeAll
     */
    @Override
    public void removeLayoutComponent(Component comp) {
        // Not used
    }

    /**
     * Returns the preferred dimensions for this layout given the <i>visible </i>
     * components in the specified target container.
     * 
     * @param target the component which needs to be laid out
     * @return the preferred dimensions to lay out the subcomponents of the
     *         specified container
     * @see Container
     * @see #minimumLayoutSize
     * @see java.awt.Container#getPreferredSize
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();

            LinkedList<Component>[] sp = separateIntoColumns(target, nmembers);

            @SuppressWarnings("unchecked")
            ListIterator<Component>[] li = new ListIterator[spalten];
            for (int x = 0; x < spalten; x++) {
                li[x] = sp[x].listIterator();
            }

            int[] max = new int[spalten];
            for (int x = 0; x < spalten; x++) {
                max[x] = 0;
            }

            Component[] m = new Component[spalten];
            Dimension[] d = new Dimension[spalten];

            int mheight = 0;
            while (li[spalten - 1].hasNext()) {
                // int mheight = 0;
                for (int x = 0; x < spalten; x++) {
                    m[x] = li[x].next();
                    d[x] = m[x].getPreferredSize();
                    max[x] = Math.max(d[x].width, max[x]);
                    mheight = Math.max(mheight, d[x].height);
                }
                // dim.height += mheight+vgap;
            }

            if (li[0].hasNext()) {
                // int mheight = 0;
                for (int x = 0; x < spalten; x++) {
                    if (li[x].hasNext()) {
                        m[x] = li[x].next();
                        d[x] = m[x].getPreferredSize();
                        max[x] = Math.max(d[x].width, max[x]);
                        mheight = Math.max(mheight, d[x].height);
                    }
                }
                // dim.height += mheight+vgap;
            }

            Insets insets = target.getInsets();
            dim.height = insets.top + insets.bottom + (sp[0].size() * (mheight + vgap)) - vgap;
            dim.width = insets.left + insets.right - hgap;
            for (int x = 0; x < spalten; x++) {
                dim.width += max[x] + hgap;
            }
            return dim;
        }
    }

    /**
     * Returns the minimum dimensions needed to layout the <i>visible </i>
     * components contained in the specified target container.
     * 
     * @param target the component which needs to be laid out
     * @return the minimum dimensions to lay out the subcomponents of the specified
     *         container
     * @see #preferredLayoutSize
     * @see java.awt.Container
     * @see java.awt.Container#doLayout
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        return preferredLayoutSize(target);
    }

    /**
     * Lays out the container. This method lets each component take its preferred
     * size by reshaping the components in the target container in order to satisfy
     * the alignment of this <code>LineLayout</code> object.
     * 
     * @param target the specified component being laid out
     * @see Container
     * @see java.awt.Container#doLayout
     */
    @SuppressWarnings("unchecked")
    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            int nmembers = target.getComponentCount();

            LinkedList<Component>[] sp = separateIntoColumns(target, nmembers);

            int[] min = new int[spalten];
            int[] max = new int[spalten];
            int height = determineMinMaxWidthAndMaxHeight(sp, min, max);

            int nmax = -hgap;
            for (int x = 0; x < spalten; x++) {
                nmax += max[x] + hgap;
            }

            if ((select < 0) || (select >= spalten)) {
                select = spalten - 1;
            }

            Insets insets = target.getInsets();
            int w = target.getWidth() - (insets.left + insets.right);
            if (nmax <= w) {
                max[select] += w - nmax;
            } else {
                int x = spalten - 1;
                while ((w - nmax < 0) && (x >= 0)) {
                    nmax += min[x] - max[x];
                    max[x] = min[x];
                    x--;
                }
            }
            int[] xpos = new int[spalten];
            xpos[0] = insets.left;
            for (int x = 1; x < spalten; x++) {
                xpos[x] = xpos[x - 1] + hgap + max[x - 1];
            }
            int y = insets.top;

            ListIterator<Component>[] ll = new ListIterator[spalten];
            for (int x = 0; x < spalten; x++) {
                ll[x] = sp[x].listIterator();
            }

            while (ll[0].hasNext()) {
                for (int x = 0; x < spalten; x++) {
                    if (ll[x].hasNext()) {
                        Component c = ll[x].next();
                        c.setLocation(xpos[x], y);
                        c.setSize(max[x], height);
                    }
                }
                y += height + vgap;
            }
        }
    }

    /**
     * @param sp
     * @param min
     * @param max
     * @return
     */
    private int determineMinMaxWidthAndMaxHeight(LinkedList<Component>[] sp, int[] min, int[] max) {
        for (int x = 0; x < spalten; x++) {
            max[x] = 0;
            min[x] = 10000;
        }
        @SuppressWarnings("unchecked")
        ListIterator<Component>[] li = new ListIterator[spalten];
        for (int x = 0; x < spalten; x++) {
            li[x] = sp[x].listIterator();
        }
        int height = 0;
        while (li[spalten - 1].hasNext()) {
            for (int x = 0; x < spalten; x++) {
                Component m = li[x].next();
                Dimension d = m.getPreferredSize();
                max[x] = Math.max(d.width, max[x]);
                height = Math.max(height, d.height);
                d = m.getMinimumSize();
                min[x] = Math.max(d.width, min[x]);
            }
        }
        if (li[0].hasNext()) {
            for (int x = 0; x < spalten; x++) {
                if (li[x].hasNext()) {
                    Component m = li[x].next();
                    Dimension d = m.getPreferredSize();
                    max[x] = Math.max(d.width, max[x]);
                    height = Math.max(height, d.height);
                    d = m.getMinimumSize();
                    min[x] = Math.max(d.width, min[x]);
                }
            }
        }
        return height;
    }

    /**
     * @param target
     * @param nmembers
     * @return
     */
    @SuppressWarnings({ "unchecked", "cast" })
    private LinkedList<Component>[] separateIntoColumns(Container target, int nmembers) {
        List<LinkedList<Component>> sp = new ArrayList<>(spalten);
        for (int x = 0; x < spalten; x++) {
            sp.add(new LinkedList<>());
        }
        int spalte = 0;
        for (int i = 0; i < nmembers; i++) {
            Component m = target.getComponent(i);
            if (m.isVisible()) {
                sp.get(spalte).addLast(m);
                spalte = (spalte + 1) % spalten;
            }
        }
        return (LinkedList<Component>[]) sp.toArray(new LinkedList[spalten]);
    }

    /**
     * Returns a string representation of this <code>LineLayout</code> object and
     * its values.
     * 
     * @return a string representation of this layout
     */
    @Override
    public String toString() {
        return getClass().getName() + "[columns=" + spalten + "hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
