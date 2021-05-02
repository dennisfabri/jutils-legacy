/*
 * Created on 16.01.2005
 */
package de.df.jutils.gui.autocomplete;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.util.ComboBoxUtil;

/**
 * @author Dennis Fabri
 * @date 16.01.2005 Validation based on:
 *       https://github.com/aterai/java-swing-tips/blob/master/ComboBoxEditorVerifier/src/java/example/MainPanel.java
 */
public class JCompletingComboBox<T extends Object> extends JComboBox<T> {

    final class UpdateListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateBackgroundColor();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateBackgroundColor();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateBackgroundColor();
        }
    }

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3257005466784445492L;

    private static final Color YELLOW = new Color(255, 255, 200);
    private static final Color RED = new Color(255, 200, 200);

    private Color enabled = null;
    private Color disabled = null;

    private boolean required = false;

    private boolean isWindowsLaF = false;

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

    private void checkWindowsLaF() {
        isWindowsLaF = SystemUtils.IS_OS_WINDOWS
                && UIManager.getLookAndFeel().getClass().getName().equals(UIManager.getSystemLookAndFeelClassName());
    }

    private void initialize() {
        checkWindowsLaF();
        JComboBoxAutoCompletion.enable(this);
        if (isWindowsLaF) {
            setInputVerifier(new LengthInputVerifier());
            setEditor(new BasicComboBoxEditor() {
                private JLayer<JTextComponent> editorComponent;

                @Override
                public Component getEditorComponent() {
                    editorComponent = Optional.ofNullable(editorComponent).orElseGet(() -> {
                        JLayer<JTextComponent> layer = new JLayer<>((JTextComponent) super.getEditorComponent(),
                                new ValidationLayerUI<>());
                        layer.setOpaque(false);
                        layer.getView().setOpaque(false);
                        layer.getView().setBorder(new EmptyBorder(0, 4, 0, 4));
                        return layer;
                    });
                    return editorComponent;
                }
            });
        } else {
            // Storing colors
            enabled = ComboBoxUtil.getEditorComponent(this).getBackground();
            setEnabled(false);
            disabled = ComboBoxUtil.getEditorComponent(this).getBackground();
            setEnabled(true);

            // Adding listener
            UpdateListener ul = new UpdateListener();
            JTextField jtf = ComboBoxUtil.getEditorComponent(this);
            if (jtf != null) {
                jtf.getDocument().addDocumentListener(ul);
            }
        }
    }

    public String getText() {
        Object o = getSelectedItem();
        if (o == null) {
            return "";
        }
        return o.toString();
    }

    void updateBackgroundColor() {
        if (isWindowsLaF) {
            return;
        }
        Color next = null;
        if (isEnabled()) {
            next = enabled;
        } else {
            next = disabled;
        }
        if (!isValidValue()) {
            Component c = ComboBoxUtil.getEditorComponent(this);
            if (c instanceof JTextField) {
                if (((JTextField) c).getText().length() == 0) {
                    next = YELLOW;
                } else {
                    next = RED;
                }
            } else {
                if (getText().length() == 0) {
                    next = YELLOW;
                } else {
                    next = RED;
                }
            }
        }
        if (next != null) {
            if (getEditor() != null) {
                for (Component c : getComponents()) {
                    if (!next.equals(c.getBackground())) {
                        c.setBackground(next);
                        if (c instanceof JComponent) {
                            ((JComponent) c).setOpaque(true);
                        }
                    }
                }
                Component c = ComboBoxUtil.getEditorComponent(this);
                if (!next.equals(c.getBackground())) {
                    c.setBackground(next);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        updateBackgroundColor();
        super.paintComponent(g);
    }

    @Override
    public void repaint() {
        updateBackgroundColor();
        super.repaint();
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