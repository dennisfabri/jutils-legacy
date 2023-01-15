/*
 * Created on 23.10.2005
 */
package de.df.jutils.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.df.jutils.gui.jlist.ModifiableListModel;
import de.df.jutils.gui.util.EDTUtils;
import de.df.jutils.i18n.util.JUtilsI18n;

public class JUndoButton<T> extends JPanel {

    private static final long serialVersionUID = 4682235122685742658L;

    private static Icon createArrow() {
        BufferedImage i = new BufferedImage(11, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics g = i.getGraphics();

        g.setColor(Color.BLACK);
        g.fillPolygon(new int[] { 2, 8, 5 }, new int[] { 0, 0, 3 }, 3);

        return new ImageIcon(i);
    }

    private static final Icon ARROW_ICON = createArrow();

    JButton button;
    JToggleButton arrow;
    protected JPopupMenu menu;
    JLabel status;
    private int index;
    protected JList<T> list;
    private int maxsize;

    private ModifiableListModel<T> data = new ModifiableListModel<>();

    private LinkedList<UndoListener<T>> listeners = new LinkedList<>();

    public JUndoButton(String name, Icon icon, int size) {
        super(new BorderLayout(0, 0));
        button = new JButton(name, icon);
        arrow = new JToggleButton(ARROW_ICON);
        maxsize = size;

        arrow.addActionListener(new ArrowActionListener());

        add(button, BorderLayout.CENTER);
        add(arrow, BorderLayout.EAST);

        button.addChangeListener(new ButtonChangeListener());
        arrow.addChangeListener(new ButtonChangeListener());

        // button.setMargin(new Insets(1, 1, 1, 1));
        Insets i = arrow.getMargin();
        if (i != null) {
            i.left = 1;
            i.right = 0;
        } else {
            i = new Insets(1, 1, 1, 0);
        }
        arrow.setMargin(i);

        data = new ModifiableListModel<>();
        list = new JIntervalList<>(data);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                undo();
            }
        });
        list.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getY() >= 0) {
                    int row = list.locationToIndex(e.getPoint());
                    if (row < 0) {
                        list.setSelectedIndex(-1);
                    } else {
                        list.setSelectionInterval(0, row);
                    }
                    setStatus(row + 1);
                }
            }

        });

        button.addActionListener(e -> {
            undo(0);
        });

        JScrollPane scroll = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);

        status = new JLabel("100 Actions");
        status.setBorder(new EmptyBorder(3, 0, 0, 0));
        // status.setBorder(LineBorder.createGrayLineBorder());
        status.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                undo();
            }
        });

        menu = new JUndoPopup();
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getY() <= 0) {
                    list.setSelectedIndex(-1);
                    setStatus(0);
                }
            }
        });
        menu.setLayout(new BorderLayout());
        menu.add(scroll, BorderLayout.CENTER);
        menu.add(status, BorderLayout.SOUTH);

        arrow.setEnabled(false);
        button.setEnabled(false);
    }

    protected void undo() {
        menu.setVisible(false);

        for (UndoListener<T> ul : listeners) {
            try {
                ul.undo(this, index);
            } catch (Exception t) {
                t.printStackTrace();
            }
        }
    }

    protected void undo(int i) {
        index = i;
        undo();
    }

    public void addStep(T o) {
        data.addFirst(o);
        if ((maxsize > 0) && (data.size() > maxsize)) {
            data.remove(maxsize);
        }
        checkButtonStatus();
    }

    protected void setStatus(int index) {
        this.index = index - 1;
        status.setText(JUtilsI18n.get("de.dm.undo.status", index));
    }

    private void checkButtonStatus() {
        boolean b = data.size() > 0;
        EDTUtils.setEnabled(button, b);
        EDTUtils.setEnabled(arrow, b);
    }

    public void removeSteps(int amount) {
        while ((data.size() > 0) && (amount > 0)) {
            data.remove(0);
            amount--;
        }
        checkButtonStatus();
    }

    public boolean isUndoEnabled() {
        return button.isEnabled();
    }

    public void removeAllSteps() {
        data.clear();
        checkButtonStatus();
    }

    public T getStep(int i) {
        return data.getElementAt(i);
    }

    public void addUndoListener(UndoListener<T> ul) {
        listeners.addLast(ul);
    }

    public void removeUndoListener(UndoListener<T> ul) {
        listeners.remove(ul);
    }

    public JButton getMainButton() {
        return button;
    }

    public JToggleButton getArrowButton() {
        return arrow;
    }

    @Override
    public void setToolTipText(String arg0) {
        button.setToolTipText(arg0);
        arrow.setToolTipText(arg0);
    }

    final class ArrowActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (arrow.isSelected()) {
                menu.show(button, 0, button.getHeight());
            } else {
                menu.setVisible(false);
            }
        }
    }

    final class JUndoPopup extends JPopupMenu {

        private static final long serialVersionUID = -5287113681381679927L;

        @Override
        public void setVisible(boolean b) {
            if (b) {
                list.setSelectedIndex(-1);
                setStatus(0);
            }
            super.setVisible(b);
            status.setBackground(getBackground());
            if (!b) {
                boolean toggle = false;
                StackTraceElement[] ste = Thread.currentThread().getStackTrace();
                for (StackTraceElement aSte : ste) {
                    if (aSte.getMethodName().contains("actionPerformed")) {
                        toggle = true;
                    }
                }
                if (!toggle) {
                    arrow.setSelected(false);
                }
            }
        }
    }

    private final class JIntervalList<TX> extends JList<TX> {

        private static final long serialVersionUID = -3334556131129707395L;

        public JIntervalList(ListModel<TX> model) {
            super(model);
        }

        @Override
        public void setSelectedIndex(int index) {
            if (index >= 0) {
                super.setSelectionInterval(0, index);
            }
            list.getSelectionModel().clearSelection();
        }

        @Override
        public void setSelectionInterval(int anchor, int lead) {
            if ((anchor == -1) || (lead == -1)) {
                list.getSelectionModel().clearSelection();
            }
            super.setSelectionInterval(0, lead);
        }

        @Override
        public void setSelectedIndices(int[] indices) {
            int max = 0;
            for (int indice : indices) {
                if (indice > max) {
                    max = indice;
                }
            }
            super.setSelectionInterval(0, max);
        }
    }

    /**
     * This class is based on parts of DropDownButton.
     * 
     * @author santhosh kumar - santhosh@in.fiorano.com
     * @since May 28 2005, 02:36:20
     */
    class ButtonChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() == button) {
                arrow.getModel().setRollover(button.getModel().isRollover());
            } else {
                button.getModel().setRollover(arrow.getModel().isRollover() || arrow.isSelected());
            }
        }
    }

    public static interface UndoListener<T> {
        void undo(JUndoButton<T> source, int index);
    }
}
