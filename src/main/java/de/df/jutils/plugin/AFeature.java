/*
 * Created on 14.05.2004
 */
package de.df.jutils.plugin;

/**
 * @author Dennis Mueller
 * @date 14.05.2004
 */
public abstract class AFeature implements IFeature {

    private IPluginManager controller = null;
    private String         uid        = null;

    @Override
    public void setController(IPluginManager plugincontroller, String pluginuid) {
        if (plugincontroller == null) {
            throw new IllegalArgumentException("plugincontroller must not be null.");
        }
        if (pluginuid == null) {
            throw new IllegalArgumentException("pluginuid must not be null.");
        }
        this.uid = pluginuid;
        this.controller = plugincontroller;
    }

    /**
     * @return Returns the controller.
     */
    protected IPluginManager getController() {
        return controller;
    }

    /**
     * @return Returns the uid.
     */
    protected String getUid() {
        return uid;
    }

    @Override
    public void shutDown() {
        // Null-Implementation for convenience
    }

    @Override
    public void sendDataUpdateEvent(String text, long event) {
        getController().sendDataUpdateEvent(text, event, this);
    }
}