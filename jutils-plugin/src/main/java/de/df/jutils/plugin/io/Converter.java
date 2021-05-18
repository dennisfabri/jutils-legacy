/*
 * Generator.java Created on 26. March 2002, 19:27
 */

package de.df.jutils.plugin.io;

import java.util.LinkedList;
import java.util.ListIterator;

import de.df.jutils.plugin.data.PluginData;
import de.df.jutils.plugin.exception.FormatErrorException;

/**
 * @author dennis
 */
final class Converter {

    private Converter() {
        // Private constructor is never called
    }

    public static PluginData generate(LinkedList<XMLTag> tagged) throws FormatErrorException {
        if (tagged == null) {
            throw new NullPointerException();
        }
        if (tagged.size() < 2) {
            return null;
        }
        LinkedList<XMLTag> ergebnis = cleanUp(tagged);
        if (ergebnis == null) {
            return null;
        }
        ListIterator<XMLTag> daten = ergebnis.listIterator();
        XMLTag t = daten.next();
        if (t.getTagID() != XMLTag.TAG_ANFANG) {
            throw new FormatErrorException(1, "Kein Starttag am Anfang der Datei!");
        }
        if (!(t.getDaten() instanceof LinkedList)) {
            throw new FormatErrorException(2, "Keine Liste am Anfang der Datei!");
        }
        LinkedList<?> ll = (LinkedList<?>) t.getDaten();
        if (ll.size() == 0) {
            throw new FormatErrorException(3, "Liste am Anfang der Datei hat falsche Gr\u00F6\u00DF (" + ll.size() + "<1)!");
        }
        Object o = ll.getFirst();
        if (!(o instanceof String)) {
            throw new FormatErrorException(4, "Kein String am Anfang der Datei!");
        }
        String s = (String) o;
        if (!s.equals("plugin")) {
            throw new FormatErrorException(5, "Falsche ID (" + s + ") am Anfang der Datei");
        }
        PluginData plugin = new PluginData();
        if (ll.size() == 2) {
            String[][] objects = (String[][]) ll.get(1);
            extractPluginInfo(plugin, objects);
        }
        extractDependencies(daten, plugin);
        return plugin;
    }

    private static void extractDependencies(ListIterator<XMLTag> daten, PluginData plugin) throws FormatErrorException {
        while (daten.hasNext()) {
            Object next = daten.next();
            if (next instanceof XMLTag) {
                XMLTag tag = (XMLTag) next;
                if (tag.getTagID() == XMLTag.TAG_ANFANG) {
                    Object tagData = tag.getDaten();
                    if (tagData instanceof LinkedList) {
                        LinkedList<?> data = ((LinkedList<?>) tagData);
                        if ((data.size() == 2) && ("dependency".equals(data.get(0))) && (data.get(1) instanceof String[][])) {
                            String[][] innerData = (String[][]) data.get(1);
                            String id = null;
                            boolean optional = false;
                            for (String[] anInnerData : innerData) {
                                for (int x = 0; x < anInnerData.length; x += 2) {
                                    if (anInnerData[x].equals("id")) {
                                        id = anInnerData[x + 1];
                                    }
                                    if (anInnerData[x].equals("optional")) {
                                        optional = anInnerData[x + 1].equalsIgnoreCase("true");
                                    }
                                }
                            }
                            if (id != null) {
                                plugin.addDependency(new Dependency(id, optional));
                            }
                        }
                    } else {
                        throw new FormatErrorException(6, "Unerwarteter Wert");
                    }
                }
            } else {
                throw new FormatErrorException(7, "Unerwarteter Wert");
            }
        }
    }

    private static void extractPluginInfo(PluginData plugin, String[][] objects) {
        for (String[] object : objects) {
            String name = object[0];
            String data = object[1];
            if (name.equals("name")) {
                plugin.setName(data);
            }
            if (name.equals("description")) {
                plugin.setDescription(data);
            }
            if (name.equals("id")) {
                plugin.setId(data);
            }
            if (name.equals("classname")) {
                plugin.setClassname(data);
            }
        }
    }

    private static LinkedList<XMLTag> cleanUp(LinkedList<XMLTag> tagged) {
        if (tagged == null) {
            return null;
        }
        LinkedList<XMLTag> ergebnis = new LinkedList<XMLTag>();
        ListIterator<XMLTag> li = tagged.listIterator();
        while (li.hasNext()) {
            XMLTag t = li.next();
            if (t.getTagID() != XMLTag.TAG_OTHER) {
                ergebnis.addLast(t);
            } else {
                if (t.getDaten() instanceof String) {
                    String s = (String) t.getDaten();
                    if (s.trim().length() > 0) {
                        ergebnis.addLast(t);
                    }
                } else {
                    ergebnis.addLast(t);
                }
            }
        }
        if (ergebnis.size() == 0) {
            return null;
        }
        return ergebnis;
    }
}