/*
 * fileSelector.java Created on 4. Juli 2001, 12:40
 */

package de.df.jutils.gui.util;

/**
 * @author Dennis Mueller
 * @version 1.0
 */

import java.awt.Window;

import javax.swing.JFrame;

import de.df.jutils.gui.fc.*;
import de.df.jutils.gui.filefilter.SimpleFileFilter;
import de.df.jutils.util.OSUtils;

/**
 * Bietet Dialoge zum Auswaehlen von Dateien und Verzeichnissen.
 * 
 * @author Dennis Mueller
 */
public final class FileChooserUtils {

    private static FileChooser instance;

    static {
        if (OSUtils.isWindows()) {
            instance = new FileChooserWindowsNative();
        } else {
            instance = new FileChooserReference();
        }
    }

    /**
     * Constructor is not needed. Hide it!
     */
    private FileChooserUtils() {
        // Never used
    }

    public static synchronized boolean setBaseDir(String directory) {
        return instance.setBaseDir(directory);
    }

    /**
     * Erzeugt einen Dateirequester
     * 
     * @param title  Titel
     * @param button Buttonname
     * @param ff     Dateiendungsfilter
     * @param parent UEbergeordnetes Fenster
     * @return Dateiname oder null
     */
    public static synchronized String chooseFile(final String title, final String button, final SimpleFileFilter ff,
            final JFrame parent) {
        return chooseFile(title, button, new SimpleFileFilter[] { ff }, parent);
    }

    public static synchronized String chooseFile(final String title, final String button, final SimpleFileFilter[] ff,
            final Window parent) {
        String[] names = chooseFiles(title, button, ff, parent, true);
        if (names != null) {
            return names[0];
        }
        return null;
    }

    /**
     * /** Erzeugt einen Dateirequester
     * 
     * @param title  Titel
     * @param button Buttonname
     * @param ff     Dateiendungsfilter
     * @param parent UEbergeordnetes Fenster
     * @return Dateiname oder null
     */
    public static synchronized String[] chooseFiles(final String title, final String button, final SimpleFileFilter ff,
            final JFrame parent) {
        return chooseFiles(title, button, new SimpleFileFilter[] { ff }, parent, false);
    }

    /**
     * /** Erzeugt einen Dateirequester
     * 
     * @param title  Titel
     * @param button Buttonname
     * @param ff     Dateiendungsfilter
     * @param parent UEbergeordnetes Fenster
     * @return Dateiname oder null
     */
    public static synchronized String[] chooseFiles(final String title, final String button, final SimpleFileFilter ff,
            final JFrame parent, boolean singleSelection) {
        return chooseFiles(title, button, new SimpleFileFilter[] { ff }, parent, singleSelection);
    }

    /*
     * Erzeugt einen Dateirequester
     * 
     * @param title Titel
     * 
     * @param button Buttonname
     * 
     * @param ff Dateiendungsfilter
     * 
     * @param parent UEbergeordnetes Fenster
     * 
     * @return Dateiname oder null
     */
    public static synchronized String[] chooseFiles(final String title, final String button, SimpleFileFilter[] ff,
            final Window parent, boolean singleSelection) {
        return instance.chooseFiles(title, button, ff, parent, singleSelection);
    }

    /**
     * Zeigt einen Verzeichnisrequester
     * 
     * @param title  Titel
     * @param button Buttonbeschriftung
     * @param parent UEbergeordnetes Fenster
     * @return Verzeichnisname
     */
    public static synchronized String chooseDirectory(final Window parent) {
        return instance.chooseDirectory(parent);
    }
}