/*
 * Created on 15.04.2005
 */
package de.df.jutils.i18n;

import java.awt.Toolkit;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ResourceBundleUIProvider implements UIElementsProvider {

    private final ResourceBundle resource;

    private static ResourceBundle getBundle(String name) {
        try {
            return ResourceBundle.getBundle(name);
        } catch (RuntimeException re) {
            re.printStackTrace();
            return null;
        }
    }

    public ResourceBundleUIProvider(String name) {
        this(getBundle(name));
    }

    public ResourceBundleUIProvider(ResourceBundle rb) {
        resource = rb;
    }

    @Override
    public String getString(String id) {
        try {
            return resource.getString(id);
        } catch (RuntimeException re) {
            re.printStackTrace();
            return id.substring(id.lastIndexOf(".") + 1);
        }
    }

    @Override
    public Icon getIcon(String id) {
        try {
            return new ImageIcon(Toolkit.getDefaultToolkit().getImage(getString(id + ".icon")));
        } catch (RuntimeException re) {
            re.printStackTrace();
            return null;
        }
    }
}