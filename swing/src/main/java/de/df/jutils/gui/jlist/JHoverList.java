package de.df.jutils.gui.jlist;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

public class JHoverList<T> extends JList<T> {

    Point point = null;

    public JHoverList() {
        super();
        initialize();
    }

    public JHoverList(ListModel<T> dataModel) {
        super(dataModel);
        initialize();
    }

    public JHoverList(T[] listData) {
        super(listData);
        initialize();
    }

    public JHoverList(Vector<T> listData) {
        super(listData);
        initialize();
    }

    public boolean isHovering() {
        return point != null;
    }

    private boolean calculatingIndex = false;

    public int getHoveredIndex() {
        try {
            if (calculatingIndex) {
                return -1;
            }
            calculatingIndex = true;

            if (point == null) {
                return -1;
            }
            return locationToIndex(point);
        } finally {
            calculatingIndex = false;
        }
    }

    public Object getHoveredItem() {
        int index = getHoveredIndex();
        if (index < 0) {
            return null;
        }
        return getModel().getElementAt(index);
    }

    private void initialize() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                point = e.getPoint();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                point = null;
                repaint();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }
}
