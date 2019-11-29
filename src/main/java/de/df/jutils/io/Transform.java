package de.df.jutils.io;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public final class Transform {

    private Transform() {
        // Never used
    }

    public static void transformDocument2XML(final OutputStream out, final String dtd, final Document xmldoc) throws TransformerException {
        // Serialisation through Tranform.
        DOMSource domSource = new DOMSource(xmldoc);
        StreamResult streamResult = new StreamResult(out);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        if (dtd != null) {
            serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtd);
        }
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.transform(domSource, streamResult);
    }

    public static void transformDocument2XML(final Writer out, final String dtd, final Document xmldoc) throws TransformerException {
        // Serialisation through Tranform.
        DOMSource domSource = new DOMSource(xmldoc);
        StreamResult streamResult = new StreamResult(out);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        if (dtd != null) {
            serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtd);
        }
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.transform(domSource, streamResult);
    }

    public static void transformXML2HTML(final InputStream in, final String xsl, final OutputStream out) throws TransformerException {
        TransformerFactory tFactory = TransformerFactory.newInstance();

        Transformer transformer = tFactory.newTransformer(new javax.xml.transform.stream.StreamSource(xsl));

        transformer.transform(new javax.xml.transform.stream.StreamSource(new BufferedInputStream(in)), new javax.xml.transform.stream.StreamResult(out));
    }

    public static void transformXML2HTML(final InputStream in, final String xsl, final Writer out) throws TransformerException {
        TransformerFactory tFactory = TransformerFactory.newInstance();

        Transformer transformer = tFactory.newTransformer(new javax.xml.transform.stream.StreamSource(xsl));

        transformer.transform(new javax.xml.transform.stream.StreamSource(new BufferedInputStream(in)), new javax.xml.transform.stream.StreamResult(out));
    }

    public static String getHtmlDocument(String stylesheet, Document doc) throws IOException, TransformerException {
        StringWriter out = new StringWriter();
        Transform.transformXML2HTML(new ByteArrayInputStream(documentToBytes(doc)), stylesheet, out);
        return out.toString();
    }

    public static String getXMLDocument(Document doc) throws IOException {
        StringWriter sw = new StringWriter();
        documentToWriter(doc, sw);
        return sw.toString();
    }

    public static void writeHtmlDocument(String name, String stylesheet, Document doc) throws IOException, TransformerException {
        FileOutputStream out = new FileOutputStream(name);
        writeHtmlDocument(out, stylesheet, doc);
        out.close();
    }

    public static void writeHtmlDocument(OutputStream out, String stylesheet, Document doc) throws IOException, TransformerException {
        byte[] data = documentToBytes(doc);
        transformXML2HTML(new ByteArrayInputStream(data), stylesheet, out);
    }

    private static byte[] documentToBytes(Document doc) throws IOException {
        BytesOutputStream bos = new BytesOutputStream();
        try {
            Transform.transformDocument2XML(bos, null, doc);
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new IOException();
        }
        return bos.getData();
    }

    private static void documentToWriter(Document doc, Writer os) throws IOException {
        try {
            Transform.transformDocument2XML(os, null, doc);
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }
}