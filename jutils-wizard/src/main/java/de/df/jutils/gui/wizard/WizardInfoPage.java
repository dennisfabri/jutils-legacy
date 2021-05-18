/*
 * Created on 11.07.2004
 */
package de.df.jutils.gui.wizard;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Dennis Fabri
 * @date 11.07.2004
 */
public class WizardInfoPage extends AWizardPage {

    private JLabel[] texts  = null;
    private JLabel[] values = null;

    private JPanel   panel  = new JPanel();

    public WizardInfoPage(String title, String note, String[] text, String[] value) {
        super(title, note);
        if (text == null) {
            throw new NullPointerException("String[] text must not be null!");
        }
        StringBuilder topdown = new StringBuilder("4dlu:grow,fill:default");
        for (int x = 0; x < text.length; x++) {
            topdown.append(",4dlu,fill:default");
        }
        topdown.append(",4dlu:grow");

        panel.setLayout(new FormLayout("4dlu,fill:default,4dlu,fill:default:grow,4dlu", topdown.toString()));
        texts = new JLabel[text.length];
        values = new JLabel[text.length];
        for (int x = 0; x < text.length; x++) {
            texts[x] = new JLabel(text[x]);
            values[x] = new JLabel();
            if (value != null) {
                values[x].setText(value[x]);
            }
            panel.add(texts[x], CC.xy(2, 2 + 2 * x));
            panel.add(values[x], CC.xy(4, 2 + 2 * x));
        }
    }

    public final void setText(int index, String name) {
        texts[index].setText(name);
    }

    public final void setValue(int index, String name) {
        values[index].setText(name);
    }

    @Override
    public JComponent getPage() {
        return panel;
    }
}