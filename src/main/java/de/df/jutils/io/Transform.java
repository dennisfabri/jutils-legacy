package de.df.jutils.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public final class Transform {

    private Transform() {
        // Never used
    }

    public static void transformDocument2XML(final OutputStream out, final String dtd, final Document xmldoc)
            throws TransformerException {
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

    private static void transformXML2HTML(final InputStream in, final String xsl, final OutputStream out)
            throws TransformerException {
        TransformerFactory tFactory = TransformerFactory.newInstance();

        Transformer transformer = tFactory.newTransformer(new javax.xml.transform.stream.StreamSource(xsl));

        transformer.transform(new javax.xml.transform.stream.StreamSource(new BufferedInputStream(in)),
                new javax.xml.transform.stream.StreamResult(out));
    }

    public static void writeHtmlDocument(OutputStream out, String stylesheet, Document doc)
            throws IOException, TransformerException {
        byte[] data = documentToBytes(doc);
        transformXML2HTML(new ByteArrayInputStream(data), stylesheet, out);
    }

    private static byte[] documentToBytes(Document doc) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Transform.transformDocument2XML(bos, null, doc);
            return bos.toByteArray();
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }
}