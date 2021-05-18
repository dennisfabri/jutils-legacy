package de.df.jutils.plugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.l2fprod.common.shared.swing.StatusBar;

import de.df.jutils.gui.JGlassFrame;
import de.df.jutils.gui.border.ExtendedLineBorder;
import de.df.jutils.gui.layout.RowLayout;
import de.df.jutils.gui.util.WindowUtils;

/**
 * @author Dennis Fabri
 * @since 28.03.2004
 */
public class JPFrame extends JGlassFrame {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3258125869083210547L;

    protected PluginManager controller = null;

    private JMenuBar menues = new JMenuBar();
    private JToolBar quickbuttons = new JToolBar();
    private JPanelContainer panelcontainer = null;

    private StatusBar statusbar = new StatusBar();
    private JLabel statustext = new JLabel();

    JPFrame(String title, List<Image> icons, PluginManager c, IPlugin[] plugins, boolean singleMode) {
        super(title);
        setIconImages(icons);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent arg0) {
                controller.quit();
            }
        });

        Hashtable<String, Boolean> actions = new Hashtable<>();
        int counter = 0;

        // set the controller
        controller = c;

        // init some gui-parts
        quickbuttons.setFloatable(false);
        quickbuttons.setLayout(new RowLayout(0, 5));
        quickbuttons.setRollover(true);
        quickbuttons.setBorder(
                new CompoundBorder(new ExtendedLineBorder(0, 0, 1, 0), new EmptyBorder(new Insets(0, 5, 0, 5))));

        statusbar.addZone("info", statustext, "*");
        statusbar.setVisible(false);

        boolean quickButtonsNeeded = false;
        boolean menuesNeeded = false;

        LinkedList<PanelInfo> panelinfos = new LinkedList<>();
        LinkedList<MenuInfo> menuinfos = new LinkedList<>();
        LinkedList<ButtonInfo> buttoninfos = new LinkedList<>();

        for (IPlugin plugin : plugins) {
            // Panels
            PanelInfo[] panels = plugin.getPanelInfos();
            if ((panels != null) && (panels.length > 0)) {
                for (PanelInfo panel : panels) {
                    panelinfos.addLast(panel);
                }
            }

            // Menues
            MenuInfo[] menus = plugin.getMenues();
            if ((menus != null) && (menus.length > 0)) {
                menuesNeeded = true;
                for (int y = 0; y < menus.length; y++) {
                    if (menus[y] != null) {
                        menuinfos.addLast(menus[y]);
                    } else {
                        System.err.println("Menu " + y + " in " + Arrays.toString(menus) + " is null.");
                    }
                }
            }

            // buttons
            ButtonInfo[] qbuttons = plugin.getQuickButtons();
            if ((qbuttons != null) && (qbuttons.length > 0)) {
                for (int y = 0; y < qbuttons.length; y++) {
                    if (qbuttons[y] != null) {
                        buttoninfos.addLast(qbuttons[y]);
                    } else {
                        System.err.println("Button " + y + " in " + Arrays.toString(qbuttons) + " is null.");
                    }
                }
                quickButtonsNeeded = true;
            }

            ActionInfo[] acts = plugin.getActions();
            if (acts != null) {
                for (ActionInfo act : acts) {
                    String key = "" + act.getKey() + "x" + act.getModifiers();
                    if (actions.get(key) == null) {
                        actions.put(key, true);
                        WindowUtils.addAction(this, act.getAction(), act.getKey(), act.getModifiers(),
                                "action" + counter);
                        counter++;
                    } else {
                        System.err.println("Key used twice: " + act.getKey() + " / " + act.getModifiers());

                    }
                }
            }
        }

        panelcontainer = new JPanelContainer(panelinfos, singleMode);
        addMenus(menuinfos);
        addButtons(buttoninfos);

        if (menuesNeeded) {
            setJMenuBar(menues);
        }
        if (quickButtonsNeeded) {
            add(quickbuttons, BorderLayout.NORTH);
        }

        add(panelcontainer, BorderLayout.CENTER);
        add(statusbar, BorderLayout.SOUTH);

        // pack
        WindowUtils.setSize(this, 1000, 700);
        WindowUtils.center(this);
        setExtendedState(Frame.MAXIMIZED_BOTH);

        panelcontainer.resetSplitPane();
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            panelcontainer.resetSplitPane();
        }
        super.setVisible(b);
    }

    void showPanel(String name) {
        panelcontainer.showPanel(name);
    }

    void setPanelEnabled(String name, boolean isEnabled) {
        panelcontainer.setPanelEnabled(name, isEnabled);
    }

    public void setStatusBarVisible(boolean v) {
        statusbar.setVisible(v);
    }

    public boolean isStatusBarVisible() {
        return statusbar.isVisible();
    }

    public void setStatusBarText(String text) {
        statusbar.setVisible(text != null);
        statustext.setText(text);
    }

    @SuppressWarnings("serial")
    private void addButtons(LinkedList<ButtonInfo> buttoninfos) {
        int prio = Integer.MIN_VALUE;
        Collections.sort(buttoninfos);
        ListIterator<ButtonInfo> li = buttoninfos.listIterator();
        while (li.hasNext()) {
            ButtonInfo bi = li.next();
            JComponent[] qbuttons = bi.getButtons();
            if ((qbuttons != null) && (qbuttons.length > 0)) {
                if ((prio + 100 <= bi.getPriotity()) && (quickbuttons.getComponentCount() > 0)) {
                    prio = bi.getPriotity();
                    JSeparator js = new JSeparator(SwingConstants.VERTICAL);
                    JComponent c1 = new JComponent() {
                        // Nothing to do
                    };
                    c1.setPreferredSize(new Dimension(5, 0));
                    JComponent c2 = new JComponent() {
                        // Nothing to do
                    };
                    c2.setPreferredSize(new Dimension(5, 0));
                    quickbuttons.add(c1);
                    quickbuttons.add(js);
                    quickbuttons.add(c2);
                }
                for (JComponent qbutton : qbuttons) {
                    if (qbutton instanceof JButton) {
                        JButton button = ((JButton) qbutton);
                        button.setRolloverEnabled(true);
                        // button.setMargin(new Insets(1, 1, 1, 1));
                    }
                    quickbuttons.add(qbutton);
                }
            }
        }
    }

    @SuppressWarnings("null")
    private void addMenus(LinkedList<MenuInfo> menuinfos2) {
        LinkedList<MenuInfo> menuinfos = new LinkedList<>(menuinfos2);
        Collections.sort(menuinfos);

        Hashtable<String, LinkedList<MenuInfo>> table = new Hashtable<>();
        LinkedList<String> names = new LinkedList<>();

        ListIterator<MenuInfo> li = menuinfos.listIterator();
        while (li.hasNext()) {
            MenuInfo mi = li.next();
            LinkedList<MenuInfo> ll = table.get(mi.getName());
            if (ll == null) {
                names.addLast(mi.getName());
                ll = new LinkedList<>();
                table.put(mi.getName(), ll);
            }
            ll.addLast(mi);
        }

        menuinfos = new LinkedList<>();
        ListIterator<String> n = names.listIterator();
        Comparator<MenuInfo> sorter = new Comparator<>() {
            @Override
            public int compare(MenuInfo m1, MenuInfo m2) {
                return m1.getItemPriotity() - m2.getItemPriotity();
            }
        };
        while (n.hasNext()) {
            LinkedList<MenuInfo> mi = table.get(n.next());
            Collections.sort(mi, sorter);
            menuinfos.addAll(mi);
        }

        String name = "";
        int prio = 0;
        JMenu menu = null;
        li = menuinfos.listIterator();
        while (li.hasNext()) {
            MenuInfo mi = li.next();
            if (!name.equals(mi.getName())) {
                name = mi.getName();
                prio = mi.getItemPriotity();
                menu = new JMenu(mi.getName());
                menues.add(menu);
            } else {
                if (prio + 50 <= mi.getItemPriotity()) {
                    prio = mi.getItemPriotity();
                    menu.add(new JSeparator());
                }
            }
            JMenuItem[] items = mi.getMenuItems();
            for (JMenuItem item : items) {
                menu.add(item);
            }
        }
    }
}