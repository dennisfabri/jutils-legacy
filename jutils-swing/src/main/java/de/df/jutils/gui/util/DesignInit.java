/*
 * Created on 08.09.2005
 */
package de.df.jutils.gui.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.l2fprod.common.shared.swing.LookAndFeelTweaks;

import de.df.jutils.util.OSUtils;
import net.java.swingfx.waitwithstyle.InfiniteProgressPanel;

public final class DesignInit {

    private static boolean initialized = false;

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

            applyRenderingSettings(uimode);
            applyLaFTweaks();
            initializeLaF(enableSystemLookAndFeel);
            applyUIColors();
        }
    }

    private static void applyUIColors() {
        InfiniteProgressPanel.setColorFocus(UIManager.getColor("Button.focusedBorderColor"));
        InfiniteProgressPanel.setColorNormal(UIManager.getColor("Label.disabledShadow"));
    }

    private static void applyLaFTweaks() {
        System.setProperty("swing.boldMetal", "false");

        boolean systemicons = true;
        if ("false".equals(System.getProperty("de.dm.utils.SystemIcons"))) {
            systemicons = false;
        }
        if (systemicons) {
            UIManager.put("FileChooser.useSystemIcons", Boolean.TRUE);
        }

        Toolkit.getDefaultToolkit().setDynamicLayout(true);

        LookAndFeelTweaks.tweak();
    }

    private static void applyRenderingSettings(UIPerformanceMode uimode) {
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
    }

    private static void initializeLaF(boolean enableSystemLookAndFeel) {
        Commands commands = new Commands();
        if (enableSystemLookAndFeel) {
            commands.add(() -> initSystemLaF());
        }
        commands.add(() -> setDefaultLaF());

        commands.executeUntilFirstSuccess();
    }

    private static interface Condition {
        public boolean isMet();
    }

    private static interface Command {
        public boolean execute() throws Exception;
    }

    private static class ConditionalCommand implements Command {
        private final Condition condition;
        private final Command command;

        public ConditionalCommand(Command command, Condition condition) {
            this.command = command;
            this.condition = condition;
        }

        @Override
        public boolean execute() throws Exception {
            if (condition.isMet()) {
                return command.execute();
            }
            return false;
        }
    }

    private static class Commands {
        private final List<Command> commands = new ArrayList<>();

        public Commands add(Command command) {
            commands.add(command);
            return this;
        }

        public void executeUntilFirstSuccess() {
            for (Command command : commands) {
                try {
                    if (command.execute()) {
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        public Commands add(Command command, Condition condition) {
            commands.add(new ConditionalCommand(command, condition));
            return this;
        }
    }

    private static boolean setDefaultLaF() throws UnsupportedLookAndFeelException {
        System.out.println("Initializing MetalLookAndFeel");
        UIManager.setLookAndFeel(new MetalLookAndFeel());
        return true;
    }

    private static boolean initSystemLaF() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException {
        System.out.println("Initializing SystemLookAndFeel");
        if (!UIManager.getSystemLookAndFeelClassName().toLowerCase().endsWith("metallookandfeel")) {
            String laf = UIManager.getSystemLookAndFeelClassName();
            System.out.println(" with class " + laf);
            UIManager.setLookAndFeel(laf);
            SwingUtilities.invokeLater(() -> patchWindowsLaF());
            return true;
        }
        return false;
    }

    private static void patchWindowsLaF() {
        try {
            if (OSUtils.isWindows()) {
                UIDefaults uiDefaults = UIManager.getDefaults();
                uiDefaults.remove("ScrollPane.border");
                uiDefaults.remove("Table.scrollPaneBorder");

                Font font = getWindowsStandardFont();

                for (Object key : uiDefaults.keySet()) {
                    String keyString = key.toString();
                    if (keyString.endsWith(".shadow")) {
                        String borderKey = keyString.replace("shadow", "border");
                        uiDefaults.remove(borderKey);
                        uiDefaults.put(borderKey, new LineBorder(uiDefaults.getColor(keyString), 1));
                    }
                    if (keyString.endsWith(".font")) {
                        Font f = uiDefaults.getFont(keyString);
                        if (font == null) {
                            font = f;
                        }
                        uiDefaults.remove(keyString);
                        uiDefaults.put(keyString, font.deriveFont(Font.PLAIN, f.getSize2D()));
                    }
                }
            }
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Font getWindowsStandardFont() {
        try {
            return new Font("Segoe UI", Font.PLAIN, 12);
        } catch (Exception ex) {
        }
        try {
            return new Font("Tahoma", Font.PLAIN, 12);
        } catch (Exception ex) {
        }
        return null;
    }
}