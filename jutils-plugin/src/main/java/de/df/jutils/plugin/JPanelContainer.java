/*
 * Created on 24.03.2005
 */
package de.df.jutils.plugin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.JInvisibleSplitPane;
import de.df.jutils.gui.border.BorderUtils;
import de.df.jutils.gui.border.ShadowBorder;
import de.df.jutils.gui.jlist.JHoverList;
import de.df.jutils.gui.jlist.ModifiableListModel;
import de.df.jutils.gui.util.EDTUtils;
import de.df.jutils.gui.util.GraphicsUtils;

public class JPanelContainer extends JComponent {

    private static final long                    serialVersionUID = 3761973748206745394L;

    public static final boolean                  SINGLE           = true;
    public static final boolean                  DOUBLE           = false;

    private final Hashtable<String, JPanel>      topPanels;
    private final Hashtable<String, JPanel>      bottomPanels;
    private final Hashtable<String, PanelInfo>   panelinfos;
    private final HashSet<String>                panelIsEnabled;

    private final CardLayout                     bottomLayout;
    private final CardLayout                     topLayout;
    private final JPanel                         topPanel;
    private final JPanel                         bottomPanel;
    private final JHoverList<PanelInfo>          topElements;
    private final JHoverList<PanelInfo>          bottomElements;
    private final ModifiableListModel<PanelInfo> topModel;
    private final ModifiableListModel<PanelInfo> bottomModel;
    private JSplitPane                           sp               = null;

    private boolean                              single           = false;

    public JPanelContainer(LinkedList<PanelInfo> panels, boolean singleMode) {
        single = singleMode;

        topPanels = new Hashtable<String, JPanel>();
        bottomPanels = new Hashtable<String, JPanel>();

        panelinfos = new Hashtable<String, PanelInfo>();
        panelIsEnabled = new HashSet<String>();

        bottomLayout = new CardLayout();
        topLayout = new CardLayout();
        topPanel = new JPanel(topLayout);
        bottomPanel = new JPanel(bottomLayout);

        topModel = new ModifiableListModel<PanelInfo>();
        bottomModel = new ModifiableListModel<PanelInfo>();

        topElements = new JHoverList<PanelInfo>(topModel);
        bottomElements = new JHoverList<PanelInfo>(bottomModel);
        topElements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bottomElements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topElements.setCellRenderer(new PanelInfoRenderer());
        bottomElements.setCellRenderer(new PanelInfoRenderer());

        setLayout(new BorderLayout(5, 5));
        topElements.setBorder(BorderUtils.createSpaceBorder());
        bottomElements.setBorder(BorderUtils.createSpaceBorder());

        addPanels(panels);

        JPanel panel1 = topPanel;
        if (topModel.getSize() + bottomModel.getSize() > 1) {
            FormLayout layout = new FormLayout("0dlu,fill:MAX(70dlu;default),4dlu,fill:0px:grow,0dlu", "0dlu,fill:0dlu:grow,0dlu");

            panel1 = new JPanel(layout);
            JScrollPane scroller2 = new JScrollPane(topElements, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroller2.getVerticalScrollBar().setUnitIncrement(10);
            scroller2.getHorizontalScrollBar().setUnitIncrement(10);
            scroller2.setBorder(new ShadowBorder());
            panel1.add(scroller2, CC.xy(2, 2));
            panel1.add(topPanel, CC.xy(4, 2));
        }
        JPanel panel2 = bottomPanel;
        if (topModel.getSize() + bottomModel.getSize() > 1) {
            FormLayout layout = new FormLayout("0dlu,fill:MAX(70dlu;default),4dlu,fill:0px:grow,0dlu", "0dlu,fill:default:grow,0dlu");

            panel2 = new JPanel(layout);
            JScrollPane scroller2 = new JScrollPane(bottomElements, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroller2.getVerticalScrollBar().setUnitIncrement(10);
            scroller2.getHorizontalScrollBar().setUnitIncrement(10);
            scroller2.setBorder(new ShadowBorder());
            panel2.add(scroller2, CC.xy(2, 2));
            panel2.add(bottomPanel, CC.xy(4, 2));
        }

        if (topModel.getSize() > 0 && bottomModel.getSize() > 0) {
            panel1.setMinimumSize(new Dimension(panel1.getMinimumSize().width, 150));
            panel2.setMinimumSize(new Dimension(panel2.getMinimumSize().width, 150));

            sp = new JInvisibleSplitPane(JSplitPane.VERTICAL_SPLIT);
            sp.setContinuousLayout(true);
            sp.setLeftComponent(panel1);
            sp.setRightComponent(panel2);

            add(sp, BorderLayout.CENTER);
        } else {
            if (topModel.getSize() > 0) {
                add(panel1, BorderLayout.CENTER);
            } else {
                add(panel2, BorderLayout.CENTER);
            }
        }

        topElements.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedListitem(true);
            }
        });
        bottomElements.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedListitem(false);
            }
        });

        if (panelinfos.size() > 1) {
            setBorder(BorderUtils.createSpaceBorder());
        } else {
            setBorder(null);
        }

        if (!single) {
            LinkedList<PanelInfo> elements = bottomModel.getAllElements();
            if (elements.size() > 0) {
                PanelInfo pi = elements.getFirst();
                showPanel(pi.getName());
            }
        }
    }

    public boolean getMode() {
        return single;
    }

    /**
     * @param panelinfos
     */
    private void addPanels(LinkedList<PanelInfo> panels) {
        Collections.sort(panels);
        ListIterator<PanelInfo> li = panels.listIterator();
        while (li.hasNext()) {
            PanelInfo pi = li.next();
            panelinfos.put(pi.getName(), pi);
            panelIsEnabled.add(pi.getName());
            addPanel(pi);
        }

        Collections.sort(panels, new Comparator<PanelInfo>() {
            @Override
            public int compare(PanelInfo o1, PanelInfo o2) {
                return -o1.compareTo(o2);
            }
        });
        li = panels.listIterator();
        while (li.hasNext()) {
            PanelInfo pi = li.next();
            buttonPressed(pi.getName(), true);
        }
    }

    public final void addPanel(PanelInfo pi) {
        if (isOnTop(pi)) {
            topModel.addLast(pi);
            if (topModel.getSize() == 1) {
                topElements.setSelectedIndex(0);
            }
        } else {
            bottomModel.addLast(pi);
            if (bottomModel.getSize() == 1) {
                bottomElements.setSelectedIndex(0);
            }
        }
    }

    private boolean isOnTop(PanelInfo pi) {
        return (pi.isOnTop() || single);
    }

    public void removePanel(PanelInfo pi) {
        removePanel(pi, true);
    }

    private void removePanel(PanelInfo pi, boolean full) {
        JPanel panel = isOnTop(pi) ? topPanel : bottomPanel;
        ModifiableListModel<PanelInfo> buttons = (pi.isOnTop() ? topModel : bottomModel);

        if (buttons.contains(pi)) {
            panel.remove(pi.getPanel());
            buttons.remove(pi);
            panel.updateUI();
        }
    }

    private JComponent createPanel(String id) {
        if ((topPanels.get(id) != null) || (bottomPanels.get(id) != null)) {
            return null;
        }
        PanelInfo pi = panelinfos.get(id);
        JPanel p = pi.getPanel();
        JComponent result = p;
        if (pi.isScrollerNeeded()) {
            JScrollPane scroller = new JScrollPane(p, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroller.getVerticalScrollBar().setUnitIncrement(20);
            scroller.getHorizontalScrollBar().setUnitIncrement(20);
            result = scroller;
        }
        if (panelinfos.size() > 1) {
            result.setBorder(BorderUtils.createLabeledBorder(id));
        } else {
            result.setBorder(null);
        }

        if (isOnTop(pi)) {
            topPanels.put(id, p);
            topPanel.add(result, id);
        } else {
            bottomPanels.put(id, p);
            bottomPanel.add(result, id);
        }
        return result;
    }

    void showPanel(String id) {
        showPanel(id, true);
    }

    void showPanel(String id, boolean resize) {
        PanelInfo pi = panelinfos.get(id);
        if (pi == null) {
            return;
        }

        boolean ontop = isOnTop(pi);

        buttonPressed(id, ontop);
        JComponent panel = null;
        if (isOnTop(pi)) {
            panel = topPanels.get(id);
        } else {
            panel = bottomPanels.get(id);
        }

        if (resize) {
            int height = panel.getPreferredSize().height;
            if ((panel.getParent() instanceof JViewport) && (panel.getParent().getParent() instanceof JScrollPane)) {
                JScrollPane jsp = (JScrollPane) panel.getParent().getParent();
                height = jsp.getHeight();
            }
            if (height < 200) {
                height = Math.min(200, sp.getHeight() - sp.getDividerSize());
                if (ontop) {
                    sp.setDividerLocation(Math.min(height, sp.getMaximumDividerLocation()));
                } else {
                    sp.setDividerLocation(Math.max(sp.getHeight() - sp.getDividerSize() - height, sp.getMinimumDividerLocation()));
                }
            }
        }

        EDTUtils.repaintLater(panel);
    }

    void buttonPressed(String id, boolean top) {
        if (top) {
            PanelInfo pi = null;
            for (PanelInfo pim : topModel.getAllElements()) {
                if (pim.getName().equals(id)) {
                    pi = pim;
                    break;
                }
            }
            if ((pi != null) && (pi != topElements.getSelectedValue())) {
                topElements.setSelectedValue(pi, false);
            }
            if (topPanels.get(id) == null) {
                createPanel(id);
            }
            topLayout.show(topPanel, id);
        } else {
            PanelInfo pi = null;
            for (PanelInfo pim : bottomModel.getAllElements()) {
                if (pim.getName().equals(id)) {
                    pi = pim;
                    break;
                }
            }
            if ((pi != null) && (pi != bottomElements.getSelectedValue())) {
                bottomElements.setSelectedValue(pi, false);
            }
            if (bottomPanels.get(id) == null) {
                createPanel(id);
            }
            bottomLayout.show(bottomPanel, id);
        }
    }

    void selectedListitem(boolean top) {
        JList<PanelInfo> list;
        if (top) {
            list = topElements;
        } else {
            list = bottomElements;
        }
        PanelInfo pi = list.getSelectedValue();
        if (pi != null) {
            buttonPressed(pi.getName(), top);
        }
    }

    public void resetSplitPane() {
        if (sp != null) {
            int height = Math.max(topElements.getPreferredSize().height, topPanel.getPreferredSize().height);
            for (JPanel p : topPanels.values()) {
                height = Math.max(height, p.getPreferredSize().height);
            }
            sp.setDividerLocation(height);
        }
    }

    final static class PanelInfoRenderer implements ListCellRenderer<PanelInfo> {

        final class PanelInfoRendererPanel extends JPanel {
            public PanelInfoRendererPanel() {
                super(new FormLayout("center:default:grow", "1dlu,fill:default,fill:default,1dlu"));
            }

            @Override
            public void paint(Graphics g) {
                if (getBorder() == selected) {
                    GraphicsUtils.paintGradient((Graphics2D) g, 0, 0, getWidth(), getHeight(), start, end);
                }
                super.paint(g);
            }
        }

        private final JPanel button     = new PanelInfoRendererPanel();
        private final JLabel text       = new JLabel();
        private final JLabel image      = new JLabel();

        final Border         unselected = new EmptyBorder(2, 2, 2, 2);
        final Border         selected   = new CompoundBorder(new EmptyBorder(1, 1, 1, 1),
                new LineBorder(UIManager.getColor("InternalFrame.activeTitleGradient"), 1));

        final Color          start;
        final Color          end;

        public PanelInfoRenderer() {
            button.add(image, CC.xy(1, 2));
            button.add(text, CC.xy(1, 3));
            button.setOpaque(false);

            Color s = UIManager.getColor("InternalFrame.activeTitleBackground");
            Color e = UIManager.getColor("InternalFrame.activeTitleGradient");
            if (s == null) {
                s = UIManager.getColor("InternalFrame.borderColor");
            }
            if (e == null) {
                e = UIManager.getColor("InternalFrame.borderShadow");
            }
            Color base = UIManager.getColor("List.background");
            if (base == null) {
                base = Color.WHITE;
            }
            start = ColorUtils.calculateColor(s, base, 0.75);
            end = ColorUtils.calculateColor(e, base, 0.75);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends PanelInfo> list, PanelInfo pi, int index, boolean isSelected, boolean cellHasFocus) {
            if ((list instanceof JHoverList) && ((JHoverList<? extends PanelInfo>) list).getHoveredItem() == pi) {
                isSelected = true;
            }
            button.setBorder(isSelected ? selected : unselected);
            text.setText("");
            image.setIcon(null);
            text.setText(pi.getName());
            image.setIcon(pi.getIcon());
            return button;
        }
    }

    public boolean isPanelEnabled(String id) {
        return panelIsEnabled.contains(id);
    }

    public void setPanelEnabled(String id, boolean isEnabled) {
        PanelInfo pi = panelinfos.get(id);
        if (pi == null) {
            return;
        }
        if (isPanelEnabled(id) == isEnabled) {
            // Nothing to do
            return;
        }

        boolean ontop = isOnTop(pi);

        if (isEnabled) {
            if (ontop) {
                int pos = 0;
                for (PanelInfo api : topModel.getAllElements()) {
                    if (api.getPriotity() > pi.getPriotity()) {
                        break;
                    }
                    pos++;
                }
                topModel.add(pi, pos);
                // topPanel.add(pi.getPanel());
            } else {
                int pos = 0;
                for (PanelInfo api : topModel.getAllElements()) {
                    if (api.getPriotity() > pi.getPriotity()) {
                        break;
                    }
                    pos++;
                }
                bottomModel.add(pi, pos);
                // bottomPanel.add(pi.getPanel());
            }
            panelIsEnabled.add(id);
        } else {
            if (ontop) {
                boolean changeSelection = topElements.getSelectedValue() == pi;
                removePanel(pi, false);
                topModel.remove(pi);
                JPanel panel = topPanels.get(pi.getName());
                if (panel != null) {
                    topPanel.remove(panel);
                    topLayout.removeLayoutComponent(panel);
                }
                topPanels.remove(pi.getName());
                if (changeSelection && topModel.size() > 0) {
                    showPanel(topModel.getElementAt(0).getName());
                }
            } else {
                boolean changeSelection = bottomElements.getSelectedValue() == pi;
                bottomModel.remove(pi);
                JPanel panel = bottomPanels.get(pi.getName());
                if (panel != null) {
                    bottomPanel.remove(panel);
                    bottomLayout.removeLayoutComponent(panel);
                }
                bottomPanels.remove(pi.getName());
                if (changeSelection && bottomModel.size() > 0) {
                    showPanel(bottomModel.getElementAt(0).getName());
                }
            }
            panelIsEnabled.remove(id);
        }
    }
}