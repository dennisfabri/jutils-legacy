/*
 * Dialoge.java Created on 22. August 2001, 12:45
 */

package de.df.jutils.gui.util;

/**
 * @author Dennis Fabri
 * @version 1.0
 */

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.oxbow.swingbits.dialog.task.TaskDialog;
import org.oxbow.swingbits.dialog.task.TaskDialog.StandardCommand;
import org.oxbow.swingbits.dialog.task.TaskDialogs;
import org.oxbow.swingbits.util.Strings;

import de.df.jutils.gui.window.JIntegerDialog;
import de.df.jutils.gui.window.JTextDialog;
import de.df.jutils.i18n.util.JUtilsI18n;

/**
 * Zeigt Java-Standard-Dialoge mit vereinfachter Syntax an.
 */
public final class DialogUtils {

    private DialogUtils() {
        // Never used
    }

    public static String showTextDialog(final JFrame parent, final String title, final String angabe,
            final String text) {
        return showTextDialog(parent, title, angabe, text, 10);
    }

    public static String showTextDialog(final JFrame parent, final String title, final String angabe, final String text,
            final int size) {
        JTextDialog jtd = new JTextDialog(parent, title, angabe, size);
        if (text != null) {
            jtd.setText(text.trim());
        }
        jtd.setVisible(true);
        if (jtd.getReturnStatus() == JTextDialog.RET_CANCEL) {
            return null;
        }
        return jtd.getText();
    }

    public static boolean ask(final Window parent, final String question, final String note) {
        return TaskDialogs.ask(parent, question, note != null ? note : "");
    }

    public static boolean askAndWarn(final Window parent, final String question, final String note) {
        return TaskDialogs.isConfirmed(parent, question, note != null ? note : "");
    }

    public static boolean askAndWarn(final Window parent, final String title, final String question,
            final String note) {
        return askAndWarn(parent, question, note);
    }

    public static void inform(final Window parent, final String title, final String information, final String note) {
        inform(parent, information, note);
    }

    public static void inform(final Window parent, final String information, final String note) {
        TaskDialogs.inform(parent, information, note != null ? note : "");
    }

    public static void warn(final Window parent, final String information, final String note) {
        TaskDialogs.inform(parent, information, note != null ? note : "");
    }

    public static void warn(final Window parent, final String title, final String information, final String note) {
        warn(parent, information, note);
    }

    public static void error(final Window parent, final String title, final String information, final String note) {
        TaskDialogs.error(parent, information, note);
    }

    public static void wichtigeMeldung(final Window parent, final String meldung) {
        warn(parent, meldung, null);
    }

    public static void showException(final Window parent, final String title, final String meldung, final String note,
            Exception ex) {
        TaskDialog dlg = new TaskDialog(parent, title == null ? TaskDialog.makeKey("Exception") : title);

        String msg = ex.getMessage();
        String className = ex.getClass().getName();
        boolean noMessage = Strings.isEmpty(msg);

        dlg.setInstruction(noMessage ? className : msg);
        dlg.setText(noMessage ? "" : className);

        dlg.setIcon(TaskDialog.StandardIcon.ERROR);
        dlg.setCommands(StandardCommand.CANCEL.derive(TaskDialog.makeKey("Close")));

        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setFont(UIManager.getFont("Label.font"));
        text.setText(Strings.stackStraceAsString(ex));
        text.setCaretPosition(0);

        JScrollPane scroller = new JScrollPane(text);
        scroller.setPreferredSize(new Dimension(400, 200));
        dlg.getDetails().setExpandableComponent(scroller);
        dlg.getDetails().setExpanded(noMessage);

        dlg.setResizable(true);
        dlg.setVisible(true);
    }

    public static int askForNumber(JFrame parent, String question, String note, int min, int max) {
        JIntegerDialog id = new JIntegerDialog(parent, JUtilsI18n.get("de.dm.gui.window.EnterNumber"), question, note,
                min, min, max);
        if (min < 0) {
            throw new IllegalArgumentException("Min must not be lower than 0.");
        }
        id.setVisible(true);
        if (id.getReturnStatus() == JIntegerDialog.RET_CANCEL) {
            return -1;
        }
        return id.getInt();
    }
}
