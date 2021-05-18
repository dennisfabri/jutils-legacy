/*
 * Created on 23.05.2004
 */
package de.df.jutils.plugin;

/**
 * @author Dennis Fabri
 * @date 23.05.2004
 */
public interface IFeature extends IDataUpdateListener {

    void setController(IPluginManager controller, String uid);

    void shutDown();

    void sendDataUpdateEvent(String text, long event);
}
