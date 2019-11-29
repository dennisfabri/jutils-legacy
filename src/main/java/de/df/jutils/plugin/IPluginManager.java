/*
 * Created on 28.03.2004
 */
package de.df.jutils.plugin;

/**
 * @author Dennis Mueller
 * @date 28.03.2004
 */
public interface IPluginManager {

    JPFrame getWindow();

    void sendDataUpdateEvent(UpdateEvent due);

    void sendDataUpdateEvent(String title, long eventID, Object data, Object additionalInformation, IFeature source);

    void sendDataUpdateEvent(String title, long eventID, IFeature source);

    void setChanged(boolean changed);

    void setTitle(String titel);

    void quit();

    void showPanel(String name);

    void setPanelEnabled(String name, boolean isEnabled);

    boolean acceptWarning();

    IFeature getFeature(String name, String uid);

    IPlugin getPlugin(String name, String uid);

    void addRemoteActionListener(IRemoteActionListener ral);

    boolean sendRemoteAction(RemoteAction ra);
}