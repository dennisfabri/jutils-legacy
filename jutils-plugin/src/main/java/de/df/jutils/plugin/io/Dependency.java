package de.df.jutils.plugin.io;

public class Dependency {

    private String  pluginname;
    private boolean optional;

    public Dependency(String pluginname, boolean optional) {
        this.setPluginname(pluginname);
        this.setOptional(optional);
    }

    public boolean isOptional() {
        return optional;
    }

    private void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getPluginname() {
        return pluginname;
    }

    private void setPluginname(String pluginname) {
        this.pluginname = pluginname;
    }

    @Override
    public String toString() {
        return getPluginname();
    }
}
