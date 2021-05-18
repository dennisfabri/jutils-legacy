package de.df.jutils.swing.icons;

import javax.swing.ImageIcon;

public class Icons {

    private final ResourceManager resourceManager = new ResourceManager(Icons.class.getPackageName() + ".icons");

    public ImageIcon getErrorIcon() {
        return resourceManager.readImage("ErrorProvider.ErrorIcon");
    }

    public ImageIcon getWarningIcon() {
        return resourceManager.readImage("ErrorProvider.WarningIcon");
    }

    public ImageIcon getInfoIcon() {
        return resourceManager.readImage("ErrorProvider.InfoIcon");
    }
}
