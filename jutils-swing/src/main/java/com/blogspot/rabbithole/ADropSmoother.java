package com.blogspot.rabbithole;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * This decorator completely over-paints the target JList, optionally
 * painting a dragged item and animating creation of a space for the dragged
 * item to be dropped.
 */
abstract class ADropSmoother<T> extends AbstractComponentDecorator {
    /**
     * Animation repaint interval. Make this larger to slow down the
     * animation. Changed by Dennis Fabri: Rate doubled
     */
    private static final int INTERVAL = 1000 / 24 / 2;
    private Timer            timer    = new Timer(true);

    /** Simple decorator to provide the ghosted drag image. */
    private final class GhostedDragImage extends AbstractComponentDecorator {
        private int   index1;
        private int   index2;
        private Point location;
        private Point offset;

        public GhostedDragImage(int cellIndex1, int cellIndex2, Point origin) {
            super(list);
            this.index1 = cellIndex1;
            this.index2 = cellIndex2;
            Rectangle b = list.getCellBounds(index1, index2);
            location = origin;
            if ((origin != null) && (b != null)) {
                this.offset = new Point(0, origin.y - b.y);
            } else {
                int y = 0;
                if (origin != null) {
                    y += origin.y;
                }
                this.offset = new Point(0, y);
            }
        }

        public void setLocation(Point where) {
            this.location = where;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            if ((location != null) && (offset != null) && (list != null)) {
                Rectangle b = list.getCellBounds(index1, index2);
                if (b != null) {
                    Point porigin = new Point(0, location.y - offset.y);
                    porigin.y = Math.max(0, porigin.y);
                    porigin.y = Math.min(porigin.y, list.getHeight() - b.height);
                    g = g.create(porigin.x, porigin.y, b.width, b.height);
                    g.translate(-b.x, -b.y);
                    ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    list.paint(g);
                }
            }
        }
    }

    final class Counter extends TimerTask {
        @Override
        public void run() {
            synchronized (ADropSmoother.this) {
                if (reposition()) {
                    repaint();
                }
            }
        }
    }

    private Counter                 counter;
    /** Index of insertion point. */
    private int                     insertionIndex = -1;
    /** Index of object being dragged, if any. */
    private int                     draggedIndex1  = -1;
    private int                     draggedIndex2  = -1;
    JList<T>                        list;
    private Map<Integer, Rectangle> bounds         = new TreeMap<Integer, Rectangle>();
    private GhostedDragImage        dragImage;
    private Point                   origin;

    public ADropSmoother(final JList<T> list) {
        super(list);
        this.list = list;
        counter = new Counter();
        timer.schedule(counter, INTERVAL, INTERVAL);
    }

    protected Object getPlaceholder() {
        return "";
    }

    protected abstract void move(int fromIndex, int toIndex, int amount);

    protected void drop(Transferable t, int index) {
        // Nothing to do
    }

    private void initialize(Point where) {
        insertionIndex = -1;
        draggedIndex1 = -1;
        draggedIndex2 = -1;
        origin = where;
        int size = list.getModel().getSize();
        for (int i = 0; i < size; i++) {
            bounds.put(i, getCellBoundsAfterInsertion(i));
        }
    }

    /** Track a drag which originated somewhere else. */
    public synchronized void startDragOver(Point where) {
        initialize(where);
        insertionIndex = getIndex(where, false);
    }

    /** Stop tracking an external drag. */
    public synchronized void endDragOver(Point where, Transferable t) {
        int idx = getIndex(where, false);
        if (idx != -1) {
            drop(t, idx);
        }
    }

    /** Start an internal drag. */
    public synchronized void startDrag(Point where) {
        initialize(where);
        int[] indizes = list.getSelectedIndices();
        draggedIndex1 = Integer.MAX_VALUE;
        draggedIndex2 = Integer.MIN_VALUE;
        for (int i : indizes) {
            if (draggedIndex1 > i) {
                draggedIndex1 = i;
            }
            if (draggedIndex2 < i) {
                draggedIndex2 = i;
            }
        }

        // draggedIndex1 = getIndex(where, true);
        // draggedIndex2 = .length;
        insertionIndex = draggedIndex1;
        dragImage = new GhostedDragImage(draggedIndex1, draggedIndex2, origin);
    }

    /** End an internal drag. */
    public synchronized void endDrag(Point where) {
        if (dragImage == null) {
            return;
        }
        int toIndex = getIndex(where, true);
        int fromIndex1 = draggedIndex1;
        int fromIndex2 = draggedIndex2;
        dragImage.dispose();
        dragImage = null;
        draggedIndex1 = -1;
        draggedIndex2 = -1;
        insertionIndex = -1;
        if (toIndex != -1 && (toIndex < fromIndex1 || toIndex > fromIndex2)) {
            Map<Integer, Rectangle> newBounds = new TreeMap<Integer, Rectangle>();
            newBounds.put(toIndex, bounds.get(fromIndex1));
            if (fromIndex1 < toIndex) {
                for (int i = fromIndex1 + 1; i <= toIndex; i++) {
                    newBounds.put(i - 1, bounds.get(i));
                }
            } else {
                for (int i = toIndex; i < fromIndex1; i++) {
                    newBounds.put(i + 1, bounds.get(i));
                }
            }
            bounds.putAll(newBounds);
            move(fromIndex1, toIndex, fromIndex2 - fromIndex1 + 1);
            list.setSelectedIndex(toIndex);
        }
    }

    boolean reposition() {
        boolean changed = false;
        for (Object o : bounds.keySet()) {
            Integer x = (Integer) o;
            Rectangle current = getCurrentCellBounds(x);
            Rectangle end = getCellBoundsAfterInsertion(x);
            if ((current != null) && (end != null)) {
                if (current.x != end.x || current.y != end.y) {
                    int xdelta = (end.x - current.x) / 2;
                    int ydelta = (end.y - current.y) / 2;
                    if (xdelta == 0) {
                        current.x = end.x;
                    } else {
                        current.x += xdelta;
                    }
                    if (ydelta == 0) {
                        current.y = end.y;
                    } else {
                        current.y += ydelta;
                    }
                    bounds.put(x, current);
                    changed = true;
                }
            }
        }
        return changed;
    }

    private int getIndex(Point where, boolean restrict) {
        int idx = list.locationToIndex(where);
        if (!restrict) {
            int size = list.getModel().getSize();
            // Assumes the list considers points below the last item
            // be within last item
            Rectangle last = list.getCellBounds(size - 1, size - 1);
            if ((where != null) && (last != null)) {
                if ((idx == size - 1) && (where.y > last.y + last.height)) {
                    idx = size;
                }
            }
        }
        return idx;
    }

    public synchronized void setInsertionLocation(Point where) {
        if (dragImage == null) {
            return;
        }
        // Avoid painting focus and/or selection bgs, kind of a hack
        getPainter().requestFocus();
        list.clearSelection();
        setInsertionIndex(getIndex(where, draggedIndex1 != -1));
        dragImage.setLocation(where);
    }

    public synchronized void setInsertionIndex(int idx) {
        if (idx != insertionIndex) {
            insertionIndex = idx;
            repaint();
        }
    }

    private Rectangle getCellBoundsAfterInsertion(int index) {
        Rectangle r = list.getCellBounds(index, index);
        if (r != null) {
            if (draggedIndex1 != -1) {
                if (index > draggedIndex1) {
                    if (index <= insertionIndex) {
                        Rectangle r2 = list.getCellBounds(draggedIndex1, draggedIndex2);
                        if (r2 != null) {
                            r.y -= r2.height;
                        }
                    }
                } else if (index < draggedIndex1) {
                    if (index >= insertionIndex) {
                        Rectangle r2 = list.getCellBounds(draggedIndex1, draggedIndex2);
                        if (r2 != null) {
                            r.y += r2.height;
                        }
                    }
                } else {
                    Rectangle r2 = list.getCellBounds(insertionIndex, insertionIndex);
                    if (r2 != null) {
                        r.y = r2.y;
                    }
                }
            } else if (insertionIndex != -1 && index > insertionIndex) {
                @SuppressWarnings("rawtypes")
                ListCellRenderer rnd = list.getCellRenderer();
                @SuppressWarnings("unchecked")
                Component c = rnd.getListCellRendererComponent(list, getPlaceholder(), insertionIndex, false, false);
                r.y += c.getHeight();
            }
        }
        return r;
    }

    private Rectangle getCurrentCellBounds(int cellIndex) {
        Rectangle r = getCellBoundsAfterInsertion(cellIndex);
        Rectangle r2 = bounds.get(cellIndex);
        if (r == null) {
            r = r2;
        }
        if (r2 != null) {
            r.x = r2.x;
            r.y = r2.y;
        }
        return r;
    }

    @Override
    public synchronized void paint(Graphics g) {
        boolean db = list.isDoubleBuffered();
        list.setDoubleBuffered(false);
        try {
            Rectangle b = getDecorationBounds();
            g.setColor(list.getBackground());
            g.fillRect(b.x, b.y, b.width, b.height);
            for (int i = 0; i < list.getModel().getSize(); i++) {
                if (i >= draggedIndex1 && i <= draggedIndex2) {
                    continue;
                }
                Rectangle r = getCurrentCellBounds(i);
                Graphics g2 = g.create(r.x, r.y, r.width, r.height);
                Rectangle r2 = list.getCellBounds(i, i);
                g2.translate(0, -r2.y);
                list.paint(g2);
            }
        } finally {
            list.setDoubleBuffered(db);
        }
    }

}