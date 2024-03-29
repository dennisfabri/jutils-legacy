package de.df.jutils.plugin.data;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

import de.df.jutils.plugin.IFeature;
import de.df.jutils.plugin.io.Dependency;

public class PluginData extends DefaultHandler {

    private Map<String, Dependency> dependencies;
    private String name;
    private String description;
    private String classname;
    private String id;
    private IFeature api;
    private String uid;

    public PluginData() {
        super();
        dependencies = new Hashtable<>();
    }

    public void addDependency(Dependency dependency) {
        dependencies.put(dependency.getPluginname(), dependency);
    }

    public Dependency[] getDependencies() {
        return dependencies.values().toArray(new Dependency[dependencies.values().size()]);
    }

    public boolean dependsOn(String pluginname) {
        return dependencies.get(pluginname) != null;
    }

    public void setName(String pluginname) {
        this.name = pluginname;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String plugindescription) {
        this.description = plugindescription;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: \"");
        sb.append(name);
        sb.append("\" Description: \"");
        sb.append(description);
        sb.append("\"");
        Collection<Dependency> c = dependencies.values();
        if (c.size() > 0) {
            sb.append(" Dependencies: \"");
            Iterator<Dependency> i = c.iterator();
            sb.append(i.next().toString());
            sb.append("\"");
            while (i.hasNext()) {
                sb.append(", \"");
                sb.append(i.next());
                sb.append("\"");
            }
        } else {
            sb.append(" (This plugin has no dependencies)");
        }
        return sb.toString();
    }

    /**
     * @return Returns the source.
     */
    public String getClassname() {
        return classname;
    }

    /**
     * @param source The source to set.
     */
    public void setClassname(String source) {
        this.classname = source;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(String pluginid) {
        this.id = pluginid;
    }

    /**
     * @return Returns the api.
     */
    public IFeature getPlugin() {
        return api;
    }

    /**
     * @param api The api to set.
     */
    public void setPlugin(IFeature feature) {
        this.api = feature;
    }

    /**
     * @return Returns the uid.
     */
    public String getUID() {
        return uid;
    }

    /**
     * @param uid The uid to set.
     */
    public void setUID(String pluginuid) {
        this.uid = pluginuid;
    }

    public void removeDependency(String pluginname) {
        Dependency dep = dependencies.get(pluginname);
        if (dep.isOptional()) {
            dependencies.remove(pluginname);
        } else {
            throw new IllegalArgumentException("The plugin is not optional and therefore cannot be removed.");
        }
    }
}
