/*
 * Created on 08.09.2005
 */
package de.df.jutils.gui.util;

import java.awt.Color;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;

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

            System.setProperty("swing.boldMetal", "false");

            boolean systemicons = true;
            if ("false".equals(System.getProperty("de.dm.utils.SystemIcons"))) {
                systemicons = false;
            }
            if (systemicons) {
                UIManager.put("FileChooser.useSystemIcons", Boolean.TRUE);
            }

            Toolkit.getDefaultToolkit().setDynamicLayout(true);

            Commands commands = new Commands();

            if (enableSystemLookAndFeel) {
                commands.add(() -> initSystemLaF(), () -> OSUtils.isMacOSX());
            }
            commands.add(() -> setFlatLaF());
            commands.add(() -> setDefaultLaF());

            commands.executeUntilFirstSuccess();

            InfiniteProgressPanel.setColorFocus(UIManager.getColor("Button.focusedBorderColor"));
            InfiniteProgressPanel.setColorNormal(UIManager.getColor("Label.disabledShadow"));
        }
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
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            return true;
        }
        return false;
    }

    private static boolean setFlatLaF() {
        System.out.println("Initializing FlatLaF with FlatArcIJTheme");

        FlatArcIJTheme.install();

        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("ScrollBar.showButtons", true);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("TabbedPane.selectedBackground", Color.white);
        UIManager.put("TabbedPane.focusColor", Color.white);

        return true;
    }
}