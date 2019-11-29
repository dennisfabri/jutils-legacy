package de.df.jutils.gui.fc;

import java.awt.Window;

import de.df.jutils.gui.filefilter.SimpleFileFilter;

public interface FileChooser {

    boolean setBaseDir(String directory);

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
    String[] chooseFiles(String title, String button, SimpleFileFilter[] ff, Window parent, boolean singleSelection);

    /**
     * Zeigt einen Verzeichnisrequester
     * 
     * @param title  Titel
     * @param button Buttonbeschriftung
     * @param parent UEbergeordnetes Fenster
     * @return Verzeichnisname
     */
    String chooseDirectory(Window parent);
}