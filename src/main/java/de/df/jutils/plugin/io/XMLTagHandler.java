package de.df.jutils.plugin.io;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class XMLTagHandler extends DefaultHandler {

    private LinkedList<XMLTag> ergebnis = new LinkedList<XMLTag>();

    @Override
    public void startDocument() throws SAXException {
        ergebnis = new LinkedList<XMLTag>();
    }

    @Override
    public void endDocument() throws SAXException {
        // Nothing to be done here
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
        String eName = localName; // element name
        if ("".equals(eName)) {
            eName = qualifiedName; // namespaceAware = false
        }
        if ((attrs == null) || (attrs.getLength() == 0)) {
            erg(new XMLTag(XMLTag.TAG_ANFANG, eName));
            return;
        }

        LinkedList<Object> daten = new LinkedList<Object>();
        daten.addLast(eName);
        String[][] o = new String[attrs.getLength()][2];
        for (int i = 0; i < attrs.getLength(); i++) {
            String aName = attrs.getLocalName(i); // Attr name
            if ("".equals(aName)) {
                aName = attrs.getQName(i);
            }
            o[i][0] = aName;
            o[i][1] = attrs.getValue(i);
        }
        daten.addLast(o);
        erg(new XMLTag(XMLTag.TAG_ANFANG, daten));
    }

    @Override
    public void endElement(String namespaceURI, String simpleName, String qualifiedName) throws SAXException {
        if ((simpleName == null) || (simpleName.length() == 0)) {
            simpleName = qualifiedName;
        }
        erg(new XMLTag(XMLTag.TAG_ENDE, simpleName));
    }

    @Override
    public void characters(char[] buf, int offset, int len) throws SAXException {
        erg(new XMLTag(XMLTag.TAG_OTHER, new String(buf, offset, len)));
    }

    // **************************************************************

    private void erg(XMLTag o) {
        ergebnis.addLast(o);
    }

    public LinkedList<XMLTag> getTagged() {
        return ergebnis;
    }
}