package de.df.jutils.plugin.data;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.df.jutils.plugin.io.Dependency;
import de.df.jutils.plugin.topsort.Node;
import de.df.jutils.plugin.topsort.TopSort;
import de.df.jutils.util.TimeMeasurement;

public class PluginContainer {

    private static final Logger log = LoggerFactory.getLogger(PluginContainer.class);

    private List<PluginData> plugins = new ArrayList<>();

    public void add(PluginData plugin) {
        plugins.add(plugin);
    }

    private boolean checkAndCleanDependencies() {
        Set<String> keys = plugins.stream().map(p -> p.getId()).collect(Collectors.toSet());

        for (PluginData pd : plugins) {
            for (Dependency aDep : pd.getDependencies()) {
                if (!keys.contains(aDep.getPluginname())) {
                    if (aDep.isOptional()) {
                        log.info("Removing unsatisfied optional dependency {} for plugin {}.", aDep, pd.getId());
                        pd.removeDependency(aDep.getPluginname());
                    } else {
                        log.warn("Unsatisfied dependency {} for plugin {}.", aDep, pd.getId());
                        return false;
                    }
                }
            }
        }
        log.info("Cleaned Plugindata");

        return true;
    }
    
    public List<PluginData> topsort(TimeMeasurement tm) {
        checkAndCleanDependencies();
        
        Hashtable<String, Node<PluginData>> nodes = initializeNodes();
        
        final List<Node<PluginData>> nodeList = new ArrayList<>();
        plugins.forEach(pd -> {
            Node<PluginData> node = nodes.get(pd.getId());
            nodeList.add(node);
            for (Dependency aDep : pd.getDependencies()) {
                Node<PluginData> n = nodes.get(aDep.getPluginname());
                node.addEdge(n);
            }            
        });

        tm.finish("Topsort");
        return TopSort.sort(nodeList).stream().map(n -> n.getData()).collect(Collectors.toList());
    }

    private Hashtable<String, Node<PluginData>> initializeNodes() {
        Hashtable<String, Node<PluginData>> nodes = new Hashtable<>();
        if (!plugins.isEmpty()) {
            for (PluginData plugin : plugins) {
                Node<PluginData> node = new Node<>(plugin);
                nodes.put(plugin.getId(), node);
            }
        }
        return nodes;
    }
}
