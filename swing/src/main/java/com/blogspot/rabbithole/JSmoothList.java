package com.blogspot.rabbithole;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JList;

import de.df.jutils.gui.jlist.ModifiableListModel;

/**
 * Based on code from
 * http://rabbit-hole.blogspot.com/2006/04/smooth-jlist-drop-target
 * -animation.html which is based on code from
 * http://jroller.com/page/swinguistuff?entry=animated_jlist
 * 
 * @author Dennis Mueller
 * @data 2006
 * @param <T>
 */
public class JSmoothList<T extends Object> extends JList<T> {

    private static final long serialVersionUID = 3630789186278640531L;

    public JSmoothList(T[] data) {
        this(new ModifiableListModel<T>(data));
    }

    public JSmoothList() {
        this(new ModifiableListModel<T>());
    }

    public JSmoothList(ModifiableListModel<T> m) {
        super(m);
        ADropSmoother<T> smoother = new ADropSmoother<T>(this) {
            @Override
            protected void move(int fromIndex, int toIndex, int amount) {
                ModifiableListModel<T> model = (ModifiableListModel<T>) getModel();
                if (fromIndex < toIndex) {
                    for (int x = 1; x <= amount; x++) {
                        model.move(fromIndex + amount - x, toIndex - x + 1);
                    }
                } else {
                    for (int x = 1; x <= amount; x++) {
                        model.move(fromIndex + amount - 1, toIndex);
                    }
                }
                if ((getSelectedIndex() == 0) && (toIndex == 0) && (model.size() > 1)) {
                    setSelectedIndex(1);
                }
                setSelectedIndices(new int[] { toIndex, toIndex + amount - 1 });
            }
        };
        Listener<T> listener = new Listener<T>(smoother);
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setListData(Object[] listData) {
        super.setModel(new ModifiableListModel<T>((T[]) listData));
    }

    @SuppressWarnings({ "rawtypes", "cast", "unchecked" })
    @Override
    public void setListData(Vector listData) {
        super.setModel(new ModifiableListModel<T>((Vector<T>) listData));
    }

    public void setModel(ModifiableListModel<T> model) {
        super.setModel(model);
    }

    /**
     * Simple JList-local drag/drop handler. Invokes the smoother according to
     * user input. A similar method could be used to accept drags originating
     * outside of the JList.
     */
    static class Listener<T> extends MouseAdapter {
        private ADropSmoother<T> smoother;
        private boolean          dragActive;
        private Point            origin;

        public Listener(ADropSmoother<T> smoother) {
            this.smoother = smoother;
        }

        private boolean sufficientMove(Point where) {
            int dx = Math.abs(origin.x - where.x);
            int dy = Math.abs(origin.y - where.y);
            return dx > 3 || dy > 0;
            // return Math.sqrt(dx * dx + dy * dy) > 0;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            origin = e.getPoint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (dragActive) {
                smoother.endDrag(e.getPoint());
                dragActive = false;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!dragActive) {
                if (sufficientMove(e.getPoint())) {
                    smoother.startDrag(origin);
                    dragActive = true;
                }
            }
            if (dragActive) {
                smoother.setInsertionLocation(e.getPoint());
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (dragActive) {
                smoother.setInsertionIndex(-1);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (dragActive) {
                smoother.setInsertionLocation(e.getPoint());
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // Nothing to do
        }
    }
}
