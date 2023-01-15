/*
 * Created on 02.11.2003
 */
package de.df.jutils.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Dennis Fabri
 */
public class JLabelSeparator extends JPanel {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3762530101090662449L;

    private final JLabel label;
    JSeparator left = new JSeparator();
    JSeparator right = new JSeparator();

    public JLabelSeparator(final String name) {
        this(name, null, true);
    }

    public JLabelSeparator(final String name, boolean useleft) {
        this(name, null, useleft);
    }

    public JLabelSeparator(final String name, Font f) {
        this(name, f, true);
    }

    public JLabelSeparator(final String name, Font f, boolean useleft) {
        label = new JLabel(name);

        if (useleft) {
            FormLayout layout = new FormLayout("4dlu, 4dlu, fill:default, 4dlu, fill:default:grow",
                    "0px:grow,pref,0px:grow");
            setLayout(layout);
            add(left, CC.xy(1, 2));
            add(label, CC.xy(3, 2));
            add(right, CC.xy(5, 2));
        } else {
            FormLayout layout = new FormLayout("fill:default, 4dlu, fill:default:grow", "0px:grow,pref,0px:grow");
            setLayout(layout);
            add(label, CC.xy(1, 2));
            add(right, CC.xy(3, 2));
        }

        if (f != null) {
            setFont(f);
        }
        setOpaque(false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        label.setEnabled(enabled);
        left.setEnabled(enabled);
        right.setEnabled(enabled);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (label != null) {
            label.setForeground(fg);
            left.setForeground(fg);
            right.setForeground(fg);
        }
    }

    @Override
    public void setFont(Font font) {
        if (font != null) {
            super.setFont(font);
            if (label != null) {
                label.setFont(font);
            }
        }
    }

    /**
     * @return
     */
    public String getText() {
        return label.getText();
    }

    /**
     * @param text
     */
    public void setText(final String text) {
        label.setText(text);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g) {
        paintComponents(g);
        super.paint(g);
    }

}
