/*
 * Created on 21.11.2004
 */
package de.df.jutils.plugin;

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * @author Dennis Mueller
 * @date 21.11.2004
 */
public class PanelInfo implements Comparable<PanelInfo> {

    private final int     priotity;
    private final String  name;
    private final Icon    icon;
    private final boolean onTop;
    private final boolean scrollerNeeded;

    public PanelInfo(JPanel p, Icon i, boolean top, boolean scroller, int prio) {
        this(p.getName(), i, top, scroller, prio);
        panel = p;
    }

    public PanelInfo(JPanel p, Icon i, int prio) {
        this(p.getName(), i, prio);
        panel = p;
    }

    public PanelInfo(String n, Icon i, int prio) {
        this(n, i, true, true, prio);
    }

    public PanelInfo(String n, Icon i, boolean top, boolean scroller, int prio) {
        name = n;
        icon = i;
        priotity = prio;
        scrollerNeeded = scroller;
        this.onTop = top;
    }

    public final boolean isScrollerNeeded() {
        return scrollerNeeded;
    }

    public final int getPriotity() {
        return priotity;
    }

    private JPanel panel = null;

    public final JPanel getPanel() {
        if (panel == null) {
            panel = getPanelI();
        }
        return panel;
    }

    protected JPanel getPanelI() {
        return null;
    }

    public final Icon getIcon() {
        return icon;
    }

    public final boolean isOnTop() {
        return onTop;
    }

    @Override
    public int compareTo(PanelInfo pi) {
        return priotity - pi.priotity;
    }

    public final String getName() {
        return name;
    }
}
