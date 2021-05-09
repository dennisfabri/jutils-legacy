/*
 * Created on 14.05.2004
 */
package de.df.jutils.plugin;

/**
 * @author Dennis Mueller
 * @date 14.05.2004
 */
public class ANullPlugin extends APlugin {

    private static final PanelInfo[]  NO_PANELS  = new PanelInfo[0];
    private static final MenuInfo[]   NO_MENUES  = new MenuInfo[0];
    private static final ButtonInfo[] NO_BUTTONS = new ButtonInfo[0];
    private static final ActionInfo[] NO_ACTIONS = new ActionInfo[0];

    @Override
    public MenuInfo[] getMenues() {
        return NO_MENUES;
    }

    /*
     * (non-Javadoc)
     * @see de.dm.auswertung.gui.beta.plugin.AuswertungPlugIn#getPanels()
     */
    @Override
    public PanelInfo[] getPanelInfos() {
        return NO_PANELS;
    }

    /*
     * (non-Javadoc)
     * @see de.dm.auswertung.gui.beta.plugin.AuswertungPlugIn#getQuickButtons()
     */
    @Override
    public ButtonInfo[] getQuickButtons() {
        return NO_BUTTONS;
    }

    @Override
    public ActionInfo[] getActions() {
        return NO_ACTIONS;
    }

    @Override
    public void dataUpdated(UpdateEvent due) {
        // Null-Implementation for convenience
    }
}