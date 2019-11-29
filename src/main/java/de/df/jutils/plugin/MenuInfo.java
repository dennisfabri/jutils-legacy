/*
 * Created on 21.11.2004
 */
package de.df.jutils.plugin;

import javax.swing.JMenuItem;

/**
 * @author Dennis Mueller
 * @date 21.11.2004
 */
public class MenuInfo implements Comparable<MenuInfo> {

    private String      menuname;
    private int         menuPriority;
    private int         priotity;
    private JMenuItem[] menu;

    public MenuInfo(String name, int menuPrio, JMenuItem[] m, int prio) {
        menuname = name;
        menu = m;
        menuPriority = menuPrio;
        priotity = prio;
    }

    public MenuInfo(String name, int menuPrio, JMenuItem m, int prio) {
        this(name, menuPrio, new JMenuItem[] { m }, prio);
    }

    public int getItemPriotity() {
        return priotity;
    }

    public JMenuItem[] getMenuItems() {
        return menu;
    }

    public String getName() {
        return menuname;
    }

    public int getMenuPriority() {
        return menuPriority;
    }

    @Override
    public int hashCode() {
        return priotity;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MenuInfo && compareTo((MenuInfo) obj) == 0;
    }

    @Override
    public int compareTo(MenuInfo o) {
        MenuInfo mi = o;
        if (menuPriority != mi.menuPriority) {
            return menuPriority - mi.menuPriority;
        }
        if (menuname.compareTo(mi.menuname) != 0) {
            return menuname.compareTo(mi.menuname);
        }
        return priotity - mi.priotity;
    }
}
