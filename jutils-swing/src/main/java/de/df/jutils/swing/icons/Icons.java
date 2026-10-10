package de.df.jutils.swing.icons;

import javax.swing.ImageIcon;

public class Icons {

    public interface IconProvider {
        ImageIcon getErrorIcon();

        ImageIcon getWarningIcon();
    }

    private static class DefaultIconProvider implements IconProvider {

        private final ResourceManager resourceManager = new ResourceManager(Icons.class.getPackageName() + ".icons");

        @Override
        public ImageIcon getErrorIcon() {
            return resourceManager.readImage("ErrorProvider.ErrorIcon");
        }

        @Override
        public ImageIcon getWarningIcon() {
            return resourceManager.readImage("ErrorProvider.WarningIcon");
        }
    }

    private static IconProvider iconProvider = new DefaultIconProvider();

    public static ImageIcon getErrorIcon() {
        return iconProvider.getErrorIcon();
    }

    public static ImageIcon getWarningIcon() { return iconProvider.getWarningIcon(); }

    public static void setIconProvider(IconProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider must not be null");
        }
        iconProvider = provider;
    }
}
