/*
 * JTimeField.java Created on 5. Februar 2003, 18:18
 */

package de.df.jutils.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Dennis Mueller
 */
public final class JTimeField extends javax.swing.JPanel {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3257282517894771768L;

    public static final int MAX_TIME = 9996000;

    private static final String ZERO = "0";
    private static final String ZEROZERO = "00";

    private Color color;

    private JIntegerField source = null;
    private JLabel minuten;
    private JLabel sekunden;
    private JLabel hundertstel;
    private int value = 0;
    private Insets insets = new Insets(2, 2, 2, 2);

    /** Creates new form JTimeField */
    public JTimeField(final JIntegerField jnf) {
        source = jnf;
        setBorder(new LineBorder(SystemColor.controlShadow, 1));
        meinInit();
    }

    @Override
    public Insets getInsets() {
        Insets i = new Insets(insets.top, insets.left, insets.bottom, insets.right);
        return i;
    }

    public boolean isValidValue() {
        if (source.getText().length() == 0) {
            return true;
        }
        int x = source.getInt();
        if (x >= MAX_TIME) {
            return false;
        }
        x = (x / 100) % 100;
        return (x < 60) && source.isValidInt();
    }

    public void setTimeAsInt(int value) {
        if (this.value == value) {
            return;
        }
        this.value = value;

        int temp = value;
        int result = temp % 100;
        temp = temp / 100;
        result += (temp % 60) * 100;
        temp = temp / 60;
        result += temp * 10000;
        source.setInt(result);
    }

    @Override
    public Insets getInsets(final Insets i) {
        i.top = insets.top;
        i.left = insets.left;
        i.bottom = insets.bottom;
        i.right = insets.right;
        return i;
    }

    private void meinInit() {
        FormLayout layout = new FormLayout(
                "pref:grow,right:pref,1dlu,fill:pref,1dlu,fill:pref,1dlu" + ",fill:pref,1dlu,fill:pref",
                "center:pref:grow");
        layout.setColumnGroups(new int[][] { { 6, 10 }, { 4, 8 } });
        setLayout(layout);

        minuten = new JLabel(ZERO);
        add(minuten, CC.xy(2, 1));

        JLabel zSpacer1 = new JLabel(":");
        add(zSpacer1, CC.xy(4, 1));

        sekunden = new JLabel(ZEROZERO);
        add(sekunden, CC.xy(6, 1));

        JLabel zSpacer2 = new JLabel(",");
        add(zSpacer2, CC.xy(8, 1));

        hundertstel = new JLabel(ZEROZERO);
        hundertstel.setBorder(null);
        add(hundertstel, CC.xy(10, 1));

        source.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                zeitUmwandeln();
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {
                zeitUmwandeln();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                zeitUmwandeln();
            }
        });

        zeitUmwandeln();
    }

    public int getTimeAsInt() {
        zeitUmwandeln();
        return value;
    }

    @Override
    public void paint(Graphics arg0) {
        color = getForeground();
        super.paint(arg0);
    }

    private void zeitUmwandeln() {
        int integer = source.getInt();
        if (integer > MAX_TIME) {
            integer = 0;
        }

        int m = (integer / 10000);
        int s = (integer / 100) % 100;
        int z = integer % 100;

        if (s > 59) {
            sekunden.setForeground(Color.RED);
        } else {
            if (color != null) {
                sekunden.setForeground(color);
            } else {
                sekunden.setForeground(Color.BLACK);
            }
        }
        minuten.setText("" + m);
        sekunden.setText("" + (s < 10 ? "0" : "") + s);
        hundertstel.setText("" + (z < 10 ? "0" : "") + z);

        value = (m * 6000) + (s * 100) + z;
    }
}