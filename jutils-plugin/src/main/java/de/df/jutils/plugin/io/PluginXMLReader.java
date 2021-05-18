/*
 * Created on 09.05.2004
 */
package de.df.jutils.plugin.io;

import java.io.File;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import de.df.jutils.plugin.data.PluginData;
import de.df.jutils.plugin.exception.FormatErrorException;

/**
 * @author Dennis Fabri
 * @date 09.05.2004
 */
public final class PluginXMLReader {

    private PluginXMLReader() {
        // private constructor is never called
    }

    private static LinkedList<XMLTag> parse(String dateiname) {
        if (dateiname == null) {
            throw new NullPointerException("Argument must not be null!");
        }
        if (dateiname.length() == 0) {
            throw new IllegalArgumentException("Argument must not have length 0!");
        }

        // Use an instance of ourselves as the SAX event handler
        DefaultHandler handler = new XMLTagHandler();
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new File(dateiname), handler);

            return ((XMLTagHandler) handler).getTagged();
        } catch (Exception e) {
            throw new RuntimeException("Could not load Plugin " + dateiname, e);
        }
    }

    public static PluginData readPluginXML(String file) throws FormatErrorException {
        if (!file.toLowerCase().endsWith(".plugin")) {
            return null;
        }
        LinkedList<XMLTag> tags = parse(file);
        return Converter.generate(tags);
    }
}