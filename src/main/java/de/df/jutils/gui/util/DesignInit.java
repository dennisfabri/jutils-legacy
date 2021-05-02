/*
 * Created on 08.09.2005
 */
package de.df.jutils.gui.util;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel;

import de.df.jutils.util.OSUtils;

public final class DesignInit {

    private static boolean      initialized = false;

    public static final boolean ANTIALISED_TEXT;

    static {
        boolean result = false;
        String s = System.getProperty("swing.aatext", "false");
        if ((s != null) && s.equals("true")) {
            result = true;
        }
        ANTIALISED_TEXT = result;
    }

    private DesignInit() {
        // Hide
    }

    public static void init() {
        init(true, UIPerformanceMode.Default);
    }

    public static synchronized void init(boolean enableSystemLookAndFeel, UIPerformanceMode uimode) {
        if (!initialized) {
            initialized = true;

            if ("false".equals(System.getProperty("de.dm.utils.DesignInit"))) {
                return;
            }

            if (OSUtils.isWindows()) {
                switch (uimode) {
                case Default:
                    break;
                case OpenGL:
                    System.setProperty("sun.java2d.opengl", "true");
                    break;
                case Software:
                    System.setProperty("sun.java2d.opengl", "false");
                    System.setProperty("sun.java2d.d3d", "false");
                    break;
                default:
                    break;
                }
            }

            // System.setProperty("sun.java2d.opengl", "true");
            // System.setProperty("sun.java2d.noddraw", "false");
            // System.setProperty("sun.awt.noerasebackground", "true");
            // System.setProperty("awt.nativeDoubleBuffering", "true");
            System.setProperty("swing.boldMetal", "false");
            // System.setProperty("sun.java2d.ddoffscreen", "true");

            boolean systemicons = true;
            if ("false".equals(System.getProperty("de.dm.utils.SystemIcons"))) {
                systemicons = false;
            }
            if (systemicons) {
                UIManager.put("FileChooser.useSystemIcons", Boolean.TRUE);
            }

            Toolkit.getDefaultToolkit().setDynamicLayout(true);

            boolean lafSet = false;
            if (enableSystemLookAndFeel) {
                lafSet = initMacOSX(lafSet);
                lafSet = initSystemLaF(lafSet);
            }
            lafSet = setLaF(lafSet);
        }
    }

    private static boolean setLaF(boolean lafSet) {
        if (!lafSet) {
            try {
                JFrame.setDefaultLookAndFeelDecorated(true);
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UIManager.setLookAndFeel(new SubstanceBusinessLookAndFeel());
                        } catch (UnsupportedLookAndFeelException e) {
                            System.out.println("Substance LaF failed to initialize");
                            try {
                                UIManager.setLookAndFeel(new MetalLookAndFeel());
                            } catch (UnsupportedLookAndFeelException ex) {
                                System.out.println("Metal LaF failed to initialize");
                            }
                        }
                    }
                });
                //
                lafSet = true;
            } catch (Exception e) {
                // Nothing to do
            }
        }
        return lafSet;
    }

    private static boolean initSystemLaF(boolean lafSet) {
        if (!lafSet) {
            try {
                if (!UIManager.getSystemLookAndFeelClassName().toLowerCase().endsWith("metallookandfeel")) {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    lafSet = true;
                }
            } catch (Exception e) {
                // Nothing to do
            }
        }
        return lafSet;
    }

    private static boolean initMacOSX(boolean lafSet) {
        if (System.getProperty("os.name").toLowerCase().indexOf("mac os x") >= 0) {
            try {
                // System.setProperty("apple.laf.useScreenMenuBar", "true");
                // System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                // "JAuswertung");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // System.setProperty("Quaqua.tabLayoutPolicy", "wrap");

                // set the Quaqua Look and Feel in the UIManager -> Now VAqua is used
                // UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");

                // set UI manager properties here that affect Quaqua
                lafSet = true;
            } catch (Exception e) {
                // Nothing to do
            }
        }
        return lafSet;
    }
}