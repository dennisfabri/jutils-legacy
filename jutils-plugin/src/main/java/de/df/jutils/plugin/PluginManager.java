package de.df.jutils.plugin;

import java.awt.Image;
import java.io.File;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.df.jutils.gui.awt.ProgressSplashWindow;
import de.df.jutils.gui.util.DialogUtils;
import de.df.jutils.gui.util.EDTUtils;
import de.df.jutils.gui.util.ThreadUtils;
import de.df.jutils.gui.window.JProgressDialog;
import de.df.jutils.i18n.util.JUtilsI18n;
import de.df.jutils.plugin.data.PluginContainer;
import de.df.jutils.plugin.data.PluginData;
import de.df.jutils.plugin.exception.FormatErrorException;
import de.df.jutils.plugin.io.FileLock;
import de.df.jutils.plugin.io.PluginXMLReader;
import de.df.jutils.reflection.ReflectionUtils;
import de.df.jutils.util.TimeMeasurement;

/**
 * @author Dennis Fabri
 * @date 28.03.2004
 */
public class PluginManager implements IPluginManager {

    private static Logger log = LoggerFactory.getLogger(PluginManager.class);

    private JPFrame gui = null;

    private String info = "";
    private String name = "";
    private boolean changed = false;
    private Hashtable<String, PluginData> security;
    private Hashtable<String, IFeature> features;
    private LinkedList<IFeature> sortedPlugins;
    private final LinkedList<IRemoteActionListener> remotes;

    private FileLock lock;

    private boolean verbose = false;

    private void setSplashStatus(ProgressSplashWindow splash, String text) {
        if (splash != null) {
            splash.setStatus(text);
        }
    }

    public PluginManager(String title, List<Image> icons, TimeMeasurement tm, ProgressSplashWindow splash,
            boolean singleDisplayMode, boolean doLock, String pluginDirectory) {
        name = title;

        tm.start("Initializing");

        if (doLock) {
            lock = new FileLock(name);
            if (!lock.lock()) {
                if (splash != null) {
                    splash.setVisible(false);
                }
                DialogUtils.warn(null, JUtilsI18n.get("de.dm.plugin.AnotherInstanceAlreadyRunning", name),
                        JUtilsI18n.get("de.dm.plugin.AnotherInstanceAlreadyRunning.Note", name));
                EDTUtils.niceExit();
            }
        }

        tm.finish("Designs");
        security = new Hashtable<>();
        features = new Hashtable<>();
        sortedPlugins = new LinkedList<>();
        remotes = new LinkedList<>();
        tm.finish("Datastructures");
        tm.quit("Initialization finished");

        tm.start("Searching for Plugins");

        setSplashStatus(splash, JUtilsI18n.get("de.dm.plugin.SearchingPlugins"));
        List<PluginData> nodes = searchPlugIns(pluginDirectory == null ? "plugins" : pluginDirectory, tm);
        tm.quit(nodes.size() + " plugins found");

        tm.start("Loading Plugins");
        setSplashStatus(splash, JUtilsI18n.get("de.dm.plugin.LoadingPlugins"));
        loadPlugins(nodes, tm);
        tm.quit("Plugins loaded");

        tm.start("Loading GUI");
        setSplashStatus(splash, JUtilsI18n.get("de.dm.plugin.InitGUI"));
        EDTUtils.executeOnEDT(new UIFrameCreator(title, icons, singleDisplayMode));
        tm.quit("GUI loaded");
    }

    private class UIFrameCreator implements Runnable {

        private final String title;
        private final List<Image> icons;
        private final boolean mode;

        public UIFrameCreator(String title, List<Image> icons, boolean mode) {
            this.title = title;
            this.icons = icons;
            this.mode = mode;
        }

        @Override
        public void run() {
            initGuiVariable(title, icons, mode);
        }

    }

    void initGuiVariable(String title, List<Image> icons, boolean mode) {
        if (gui != null) {
            throw new IllegalStateException("Function must not be called twice.");
        }
        gui = new JPFrame(title, icons, PluginManager.this, getPlugIns(), mode);
    }

    private static List<PluginData> searchPlugIns(String path, TimeMeasurement tm) {
        PluginContainer pc = new PluginContainer();
        
        File dir = new File(System.getProperty("user.dir") + File.separator + path);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    PluginData plugin = loadPluginData(path, file.getName());
                    if (plugin != null) {
                        pc.add(plugin);
                    }
                }
            }
        }
        tm.finish("Loaded Plugindata");

        return pc.topsort(tm);
    }

    private void loadPlugins(List<PluginData> nodeList, TimeMeasurement tm) {
        ListIterator<PluginData> iter = nodeList.listIterator();
        while (iter.hasNext()) {
            PluginData pd = iter.next();
            boolean ok = loadPlugIn(pd);
            if (ok) {
                if (addPlugin(pd)) {
                    tm.finish("Loaded " + pd.getName());
                } else {
                    tm.finish("Load " + pd.getName() + " failed!");
                    iter.remove();
                }
            } else {
                tm.finish("Load " + pd.getName() + " failed!");
                iter.remove();
            }
        }
    }

    private static boolean loadPlugIn(PluginData plugin) {
        if (plugin == null) {
            return false;
        }
        try {
            IFeature api = (IFeature) ReflectionUtils.executeConstructor(plugin.getClassname());
            plugin.setPlugin(api);
            return true;
        } catch (Exception e) {
            System.err.println("Could not load Plugin: " + plugin.getName());
            e.printStackTrace();
        }
        return false;
    }

    private static PluginData loadPluginData(String path, String name) {
        if ((name == null) || (name.length() == 0)) {
            return null;
        }
        if (!name.endsWith(".plugin")) {
            return null;
        }
        if (name.startsWith("#")) {
            return null;
        }
        try {
            String filename = path + File.separator + name;
            System.out.println("Checking: " + filename);
            return PluginXMLReader.readPluginXML(filename);
        } catch (FormatErrorException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String generateUID(Class<?> klasse) {
        return "" + klasse + "--" + Math.random();
    }

    private boolean addPlugin(PluginData pluginData) {
        if (pluginData.getPlugin() == null) {
            return false;
        }
        if (features.containsKey(pluginData.getId())) {
            System.err.println("Plugin with same id already present: " + pluginData.getId());
            return false;
        }
        pluginData.setUID(generateUID(pluginData.getPlugin().getClass()));
        security.put(pluginData.getUID(), pluginData);
        features.put(pluginData.getId(), pluginData.getPlugin());
        if (sortedPlugins != null) {
            sortedPlugins.addLast(pluginData.getPlugin());
        }
        try {
            pluginData.getPlugin().setController(this, pluginData.getUID());
        } catch (Exception t) {
            t.printStackTrace();
        }
        return true;
    }

    IPlugin[] getPlugIns() {
        LinkedList<IPlugin> plugins = new LinkedList<IPlugin>();
        ListIterator<IFeature> li = sortedPlugins.listIterator();
        while (li.hasNext()) {
            IFeature feature = li.next();
            if (feature instanceof IPlugin) {
                plugins.addLast((IPlugin) feature);
            }
        }
        return plugins.toArray(new IPlugin[plugins.size()]);
    }

    @Override
    public IFeature getFeature(String id, String uid) {
        PluginData source = getPluginData(uid);
        if (source == null) {
            throw new SecurityException("Plugin lookup not allowed: " + id);
        }
        if (!source.dependsOn(id)) {
            throw new SecurityException("Plugin lookup not allowed: " + id);
        }
        return getFeature(id);
    }

    @Override
    public IPlugin getPlugin(String id, String uid) {
        PluginData source = getPluginData(uid);
        if (source == null) {
            throw new SecurityException("Plugin lookup not allowed!: " + id);
        }
        if (!source.dependsOn(id)) {
            throw new SecurityException("Plugin lookup not allowed!: " + id);
        }
        return getPlugin(id);
    }

    private PluginData getPluginData(String uid) {
        return security.get(uid);
    }

    private IFeature getFeature(String id) {
        IFeature feature = features.get(id);
        if (feature != null) {
            return feature;
        }
        throw new IllegalStateException("Feature " + id + " not found.");
    }

    private IPlugin getPlugin(String id) {
        IFeature feature = features.get(id);
        if ((feature != null) && (feature instanceof IPlugin)) {
            return (IPlugin) feature;
        }
        throw new IllegalStateException("Plugin " + id + " not found.");
    }

    @Override
    public void sendDataUpdateEvent(String id, long eventID, IFeature source) {
        sendDataUpdateEvent(id, eventID, null, null, source);
    }

    @Override
    public void sendDataUpdateEvent(String id, long eventID, Object data, Object additionalInformation,
            IFeature source) {
        sendDataUpdateEvent(new UpdateEvent(id, eventID, data, additionalInformation, source));
    }

    @Override
    public void sendDataUpdateEvent(UpdateEvent due) {
        sendDataUpdateEventInternal(due);
    }

    private final LinkedList<UpdateEvent> events = new LinkedList<UpdateEvent>();

    /**
     * This method is more compilicated as it seems, but we have to make sure, that
     * all events are processed and we do not get into a recursion. These are mainly
     * swing-thread issues that we have to deal with.
     * 
     * @param due Update event
     */
    private void sendDataUpdateEventInternal(UpdateEvent due) {
        if (due == null) {
            throw new IllegalArgumentException("UpdateEvent must not be null.");
        }
        synchronized (events) {
            events.addLast(due);
        }
        if (ThreadUtils.isRecursion()) {
            return;
        }

        while (events.size() > 0) {
            synchronized (events) {
                if (events.size() == 0) {
                    return;
                }
                due = events.getFirst();
                events.removeFirst();
            }
            if (verbose) {
                System.out.println(due.getTitle());
            }
            synchronized (this) {
                for (IFeature plugin : sortedPlugins) {
                    long time = System.currentTimeMillis();
                    sendDataUpdateEventToPlugin(plugin, due);
                    time = System.currentTimeMillis() - time;
                    if (verbose && (time > 0)) {
                        System.out.println("  " + plugin.getClass().getName() + ": " + time);
                    }
                }
            }
        }
    }

    /**
     * @param plugin
     * @param due
     */
    private static void sendDataUpdateEventToPlugin(IFeature plugin, UpdateEvent due) {
        synchronized (UPDATER) {
            UPDATER.set(plugin, due);
            EDTUtils.executeOnEDT(UPDATER);
            UPDATER.set(null, null);
        }
    }

    private static final DataUpdateRunnable UPDATER = new DataUpdateRunnable();

    static class DataUpdateRunnable implements Runnable {

        private UpdateEvent due = null;
        private IFeature plugin = null;

        public void set(IFeature plugin, UpdateEvent due) {
            this.due = due;
            this.plugin = plugin;
        }

        @Override
        public void run() {
            try {
                plugin.dataUpdated(due);
            } catch (Exception t) {
                t.printStackTrace();
            }
        }
    }

    public void start(RemoteAction[] actions) {
        EDTUtils.sleep();
        EDTUtils.setVisible(getWindow(), true);
        for (RemoteAction action : actions) {
            try {
                sendRemoteAction(action);
            } catch (RuntimeException re) {
                // Nothing to do
            }
        }
        while (!getWindow().isVisible()) {
            EDTUtils.sleep();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.dm.auswertung.gui.beta.plugin.Controller#getParent()
     */
    @Override
    public JPFrame getWindow() {
        if (gui == null) {
            throw new IllegalStateException("Window does not yet exist!");
        }
        return gui;
    }

    @Override
    public void setChanged(boolean c) {
        changed = c;
        setTitle();
    }

    @Override
    public void setTitle(String info) {
        if (info == null) {
            this.info = "";
        } else {
            this.info = info;
        }
        setTitle();
    }

    private void setTitle() {
        getWindow().setTitle(name + (info.length() > 0 ? " - " + info : "") + (changed ? "*" : ""));
    }

    @Override
    public void quit() {
        if (accept(JUtilsI18n.get("de.dm.plugin.QuitProgramTitle", name),
                JUtilsI18n.get("de.dm.plugin.QuitProgram", name),
                JUtilsI18n.get("de.dm.plugin.QuitProgram.Note", name))) {
            PrintStream os = null;
            if (new File("devel.properties").exists()) {
                os = System.out;
            }
            TimeMeasurement tm = new TimeMeasurement(os, 5);
            tm.start("\nShutdown in progress");

            getWindow().setVisible(false);
            try {
                getWindow().dispose();
            } catch (Exception ex) {
                // Nothing to do
            }

            tm.finish("Window closed");

            tm.start("Shutting down plugins");
            String[] feats = features.keySet().toArray(new String[features.keySet().size()]);

            JProgressDialog jpg = new JProgressDialog(getWindow(), JUtilsI18n.get("de.dm.plugin.Close"));
            jpg.setMaximum(feats.length + 1);
            jpg.setEnabled(false);
            jpg.start();
            tm.finish("Preparations");

            for (int x = feats.length - 1; x >= 0; x--) {
                try {
                    getFeature(feats[x]).shutDown();
                    jpg.setValue(x + 1);
                } catch (Exception t) {
                    // Nothing to do
                }
                tm.finish(feats[x]);
            }
            tm.quit("All plugins are closed");

            jpg.finish();
            if (lock != null) {
                lock.unlock();
            }

            tm.quit("Program closed");
            EDTUtils.niceExit();
        }
    }

    @Override
    public boolean acceptWarning() {
        return accept(JUtilsI18n.get("de.dm.plugin.WarningTitle"), JUtilsI18n.get("de.dm.plugin.Warning"),
                JUtilsI18n.get("de.dm.plugin.Warning.Note"));
    }

    private boolean accept(String titel, String text, String note) {
        if (changed) {
            boolean result = DialogUtils.askAndWarn(getWindow(), text, note);
            return result;
        }
        return true;
    }

    @Override
    public void addRemoteActionListener(IRemoteActionListener ral) {
        if (!remotes.contains(ral)) {
            remotes.addLast(ral);
        }
    }

    @Override
    public boolean sendRemoteAction(RemoteAction ra) {
        synchronized (remotes) {
            ListIterator<IRemoteActionListener> li = remotes.listIterator();
            while (li.hasNext()) {
                boolean result = li.next().remoteAction(ra);
                if (result) {
                    ra.consume();
                    break;
                }
            }

            li = remotes.listIterator();
            while (li.hasNext()) {
                li.next().consumedRemoteAction(ra);
            }
        }
        return ra.isConsumed();
    }

    @Override
    public void showPanel(String panelname) {
        gui.showPanel(panelname);
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void setPanelEnabled(String name, boolean isEnabled) {
        if (gui == null) {
            return;
        }
        gui.setPanelEnabled(name, isEnabled);
    }
}