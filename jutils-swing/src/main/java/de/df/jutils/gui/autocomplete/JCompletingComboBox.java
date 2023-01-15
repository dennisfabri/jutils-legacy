/*
 * Created on 16.01.2005
 */
package de.df.jutils.gui.autocomplete;

import java.awt.Component;
import java.util.Objects;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.util.ComboBoxUtil;

import de.df.jutils.gui.border.IconBorder;
import de.df.jutils.swing.icons.Icons;

/**
 * @author Dennis Fabri
 * @date 16.01.2005 Validation based on:
 *       https://github.com/aterai/java-swing-tips/blob/master/ComboBoxEditorVerifier/src/java/example/MainPanel.java
 */
public class JCompletingComboBox<T extends Object> extends JComboBox<T> {

    final class UpdateListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateStatus();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateStatus();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateStatus();
        }
    }

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3257005466784445492L;

    private boolean required;

    public JCompletingComboBox() {
        this(false);
    }

    public JCompletingComboBox(boolean required) {
        super();
        this.required = required;
        initialize();
    }

    /**
     * @param arg0
     */
    public JCompletingComboBox(T[] arg0) {
        super(arg0);
        initialize();
    }

    /**
     * @param arg0
     */
    public JCompletingComboBox(Vector<T> arg0) {
        super(arg0);
        initialize();
    }

    /**
     * @param arg0
     */
    public JCompletingComboBox(ComboBoxModel<T> arg0) {
        super(arg0);
        initialize();
    }

    private void initialize() {
        JComboBoxAutoCompletion.enable(this);
        UpdateListener ul = new UpdateListener();
        JTextField jtf = ComboBoxUtil.getEditorComponent(this);
        if (jtf != null) {
            jtf.getDocument().addDocumentListener(ul);
        }

        updateStatus();
    }

    public String getText() {
        Object o = getSelectedItem();
        if (o == null) {
            return "";
        }
        return o.toString();
    }

    private static final Icons icons = new Icons();

    private Border originalBorder;
    private Border warnBorder;
    private Border errorBorder;

    private void initializeBorders() {
        if (originalBorder == null) {
            originalBorder = getBorder();
            warnBorder = new IconBorder(icons.getWarningIcon(), originalBorder);
            errorBorder = new IconBorder(icons.getErrorIcon(), originalBorder);
        }
    }

    private Border getValidationBorder() {
        initializeBorders();
        if (isEnabled() && !isValidValue()) {
            String text = getText();
            Component c = ComboBoxUtil.getEditorComponent(this);
            if (c instanceof JTextComponent) {
                text = ((JTextComponent) c).getText();
            }
            if (text.length() == 0) {
                return warnBorder;
            } else {
                return errorBorder;
            }
        }
        return originalBorder;
    }

    private Status status;

    private void updateStatus() {
        Status newStatus = Status.Valid;
        if (!isValidValue()) {
            Component c = ComboBoxUtil.getEditorComponent(this);
            if (c instanceof JTextField) {
                if (((JTextField) c).getText().length() == 0) {
                    newStatus = Status.Empty;
                } else {
                    newStatus = Status.Invalid;
                }
            } else {
                if (getText().length() == 0) {
                    newStatus = Status.Empty;
                } else {
                    newStatus = Status.Invalid;
                }
            }
        }

        if (newStatus != status) {
            status = newStatus;
            setValidationBorder();
        }

    }

    private void setValidationBorder() {
        this.setBorder(getValidationBorder());
    }

    private enum Status {
        Valid, Empty, Invalid
    }

    public boolean isValidValue() {
        if (required) {
            Component c = ComboBoxUtil.getEditorComponent(this);
            if (c == null) {
                return false;
            }
            if (c instanceof JTextField) {
                JTextField jtf = (JTextField) c;
                return jtf.getText().length() > 0;
            }
            return (getSelectedItem() != null) && (getSelectedItem().toString().length() > 0);
        }
        return true;
    }

    class LengthInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent c) {
            if (c instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) c;
                String str = Objects.toString(cb.getEditor().getItem(), "");
                return str.length() > 0;
            }
            return false;
        }
    }
}
