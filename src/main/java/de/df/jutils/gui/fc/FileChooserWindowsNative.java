package de.df.jutils.gui.fc;

import java.awt.Window;
import java.io.File;
import java.util.Arrays;

import de.df.jutils.gui.filefilter.SimpleFileFilter;
import jnafilechooser.api.*;
import jnafilechooser.api.JnaFileChooser.Mode;

public class FileChooserWindowsNative extends FileChooserReference {

    public FileChooserWindowsNative() {
    }

    @Override
    public String[] chooseFiles(String title, String button, SimpleFileFilter[] ff, Window parent,
            boolean singleSelection) {
        try {
            if (!singleSelection) {
                return super.chooseFiles(title, button, ff, parent, false);
            }
            File currentDirectory = null;
            try {
                if (!getCurrentDirectory().isEmpty()) {
                    currentDirectory = new File(getCurrentDirectory());
                }
            } catch (Exception ex) {
                // Nothing to do
            }

            final JnaFileChooser fc = new JnaFileChooser(currentDirectory);
            fc.setMultiSelectionEnabled(!singleSelection);
            fc.setMode(Mode.Files);
            for (SimpleFileFilter f : ff) {
                fc.addFilter(f.getDescription(),
                        Arrays.stream(f.getSuffixes()).map(m -> m.substring(1)).toArray(String[]::new));
            }

            boolean ok = false;
            switch (button.toLowerCase()) {
            case "save":
            case "speichern":
            case "export":
            case "exportieren":
            case "exportieren ...":
                ok = fc.showSaveDialog(parent);
                break;
            default:
                ok = fc.showOpenDialog(parent);
            }

            if (ok) {
                File[] files = fc.getSelectedFiles();
                if (files == null || files.length == 0) {
                    return new String[0];
                }
                return Arrays.stream(files).map(f -> f.getAbsolutePath()).map(f -> fixSuffix(f, ff))
                        .toArray(String[]::new);
            }
            return null;
        } catch (Exception ex) {
            return super.chooseFiles(title, button, ff, parent, singleSelection);
        }
    }

    private String fixSuffix(String f, SimpleFileFilter[] ff) {
        if (ff == null || ff.length == 0) {
            return f;
        }
        for (SimpleFileFilter sf : ff) {
            for (String sfx : sf.getSuffixes()) {
                if (f.toLowerCase().endsWith(sfx.toLowerCase())) {
                    return f;
                }
            }
        }
        return f + ff[0].getSuffix();
    }

    @Override
    public String chooseDirectory(Window parent) {
        try {
            final WindowsFolderBrowser fb = new WindowsFolderBrowser();
            final File dir = fb.showDialog(parent);
            if (dir != null) {
                return dir.getAbsolutePath();
            }
            return null;
        } catch (Exception ex) {
            return super.chooseDirectory(parent);
        }
    }
}