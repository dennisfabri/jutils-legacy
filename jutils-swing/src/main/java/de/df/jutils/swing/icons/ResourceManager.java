
package de.df.jutils.swing.icons;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

/**
 * Original version from Naveed Quadri
 * 
 * Source: https://naveedmurtuza.blogspot.com/2010/11/simple-swing-validation-using.html
 * Visited: 09.05.2021
 *
 * @author Naveed Quadri, Dennis Fabri
 */
public class ResourceManager {

    private ResourceBundle properties;
    /**
     *
     * @param propertiesFile
     */
    public ResourceManager(String propertiesFile) {
        int index = propertiesFile.lastIndexOf(File.separator);
        if (index < 0) {
            index = propertiesFile.lastIndexOf("/");
        }
        
        properties = ResourceBundle.getBundle(propertiesFile, Locale.getDefault(), ResourceManager.class.getClassLoader());
    }

    /**
     * Reads int from the properties file
     * 
     * @param key
     * @return
     */
    public int readInteger(String key) {
        int val = 0;
        try {
            val = Integer.parseInt(properties.getString(key));

        } catch (Exception ex) {
            // ignore
        }
        return val;
    }

    public int readInteger(String key, int radix) {
        int val = 0;
        try {
            val = Integer.parseInt(properties.getString(key), radix);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return val;
    }

    /**
     * Reads long from the properties file
     * 
     * @param key
     * @return
     */
    public long readLong(String key) {
        long val = 0;
        try {
            val = Long.parseLong(properties.getString(key));
        } catch (Exception ex) {
            // ignore
        }
        return val;
    }

    /**
     * Reads the string from the properties file
     * 
     * @param key
     * @return
     */
    public String readString(String key) {
        return properties.getString(key);
    }

    public ImageIcon readImage(String key) {
        return new ImageIcon(getClass().getResource(readString(key)));
    }
}
