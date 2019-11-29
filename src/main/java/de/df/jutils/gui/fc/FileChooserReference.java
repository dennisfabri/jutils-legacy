package de.df.jutils.gui.fc;

/**
 * @author Dennis Fabri
 * @version 1.0
 */

import java.awt.Window;
import java.io.File;
import java.util.LinkedList;

import javax.swing.*;

import com.l2fprod.common.swing.JDirectoryChooser;

import de.df.jutils.gui.filefilter.SimpleFileFilter;

/**
 * Bietet Dialoge zum Auswaehlen von Dateien und Verzeichnissen.
 * 
 * @author Dennis Mueller
 */
public class FileChooserReference implements FileChooser {

    private String dir = "";

    public FileChooserReference() {
        String directory = System.getProperty("de.dm.jutils.directory", "");
        setBaseDir(directory);
    }

    protected String getCurrentDirectory() {
        return dir;
    }

    @Override
    public synchronized boolean setBaseDir(String directory) {
        File d = new File(directory);
        if (!d.exists()) {
            return false;
        }
        if (!d.isDirectory()) {
            return false;
        }
        dir = directory;
        return true;
    }

    @Override
    public synchronized String[] chooseFiles(final String title, final String button, SimpleFileFilter[] ff,
            final Window parent, boolean singleSelection) {
        JFileChooser jFC = new JFileChooser();
        if (!dir.equals("")) {
            jFC.setCurrentDirectory(new File(dir));
        }
        if (ff != null && ff.length == 0) {
            ff = null;
        }
        if (ff != null) {
            for (SimpleFileFilter aFf : ff) {
                if (aFf != null) {
                    jFC.addChoosableFileFilter(aFf);
                }
            }
            jFC.setFileFilter(ff[0]);
        }
        jFC.setAcceptAllFileFilterUsed(ff == null);
        jFC.setMultiSelectionEnabled(!singleSelection);
        jFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFC.setDialogTitle(title);
        int wert = jFC.showDialog(parent, button);
        if (wert == JFileChooser.APPROVE_OPTION) {
            File[] files = null;
            if (singleSelection) {
                File f = jFC.getSelectedFile();
                if (f == null) {
                    return null;
                }
                files = new File[] { f };
            } else {
                files = jFC.getSelectedFiles();
            }

            if ((files == null) || (files.length == 0)) {
                return null;
            }
            String[] names = new String[files.length];
            for (int x = 0; x < files.length; x++) {
                dir = jFC.getCurrentDirectory().getPath();
                names[x] = jFC.getCurrentDirectory().getPath() + java.io.File.separator + files[x].getName();
                if (ff != null) {
                    boolean ends = false;
                    String[] suffixes = null;
                    try {
                        suffixes = ((SimpleFileFilter) jFC.getFileFilter()).getSuffixes();
                    } catch (NullPointerException npe) {
                        LinkedList<String> suf = new LinkedList<String>();
                        for (SimpleFileFilter aFf : ff) {
                            String[] temp = aFf.getSuffixes();
                            for (String aTemp : temp) {
                                suf.addLast(aTemp);
                            }
                        }
                        suffixes = suf.toArray(new String[suf.size()]);
                    }
                    for (String suffixe : suffixes) {
                        if (names[x].toLowerCase().endsWith(suffixe.toLowerCase())) {
                            ends = true;
                        }
                    }
                    if (!ends) {
                        names[x] += suffixes[0];
                    }
                }
            }
            return names;
        }
        // Benutzer hat abgebrochen, also machen wir nichts...
        return null;
    }

    /**
     * Zeigt einen Verzeichnisrequester
     * 
     * @param title  Titel
     * @param button Buttonbeschriftung
     * @param parent UEbergeordnetes Fenster
     * @return Verzeichnisname
     */
    @Override
    public synchronized String chooseDirectory(final Window parent) {
        JDirectoryChooser chooser = new JDirectoryChooser(dir);
        int choice = chooser.showOpenDialog(parent);
        if (choice == JFileChooser.CANCEL_OPTION) {
            return null;
        }
        return chooser.getSelectedFile().getAbsolutePath();
    }
}