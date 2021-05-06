/*
 * Created on 28.03.2004
 */
package de.df.jutils.plugin;

/**
 * @author Dennis Mueller
 * @date 28.03.2004
 */
public interface IPlugin extends IFeature {

    PanelInfo[] getPanelInfos();

    MenuInfo[] getMenues();

    ButtonInfo[] getQuickButtons();

    ActionInfo[] getActions();
}