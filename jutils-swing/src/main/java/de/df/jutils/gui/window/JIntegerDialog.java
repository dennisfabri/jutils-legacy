/*
 * JTextDialog.java Created on 30. Oktober 2002, 16:15
 */

package de.df.jutils.gui.window;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.JIntegerField;

/**
 * @author Dennis Fabri
 */
public final class JIntegerDialog extends JDialog {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3978985466349237809L;
    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;

    private JIntegerField name;
    private int returnStatus = RET_CANCEL;

    private int value;

    public JIntegerDialog(final Frame parent, final String title, final String question, String note, int value,
            int min, int max) {
        super(parent, title, true);
        this.value = value;
        initComponents(question, note, min, max);
        pack();
        setLocation(200, 200);
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    public int getInt() {
        if (returnStatus == RET_CANCEL) {
            return value;
        }
        return name.getInt();
    }

    private void initComponents(final String angabe, String note, int min, int max) {
        addWindowListener(new CancelWindowAdapter());

        FormLayout layout = new FormLayout("4dlu,fill:default,4dlu,fill:default:grow,4dlu",
                "4dlu,fill:default,4dlu,fill:default,4dlu,fill:default,4dlu");
        getContentPane().setLayout(layout);

        JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(new OkActionListener());

        JButton cancelButton = new JButton();
        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new CancelActionListener());

        JPanel buttonPanel = new JPanel();
        layout = new FormLayout("0px:grow,fill:default,4dlu,fill:default,0dlu", "0dlu,fill:default,0dlu");
        layout.setColumnGroups(new int[][] { { 2, 4 } });
        buttonPanel.setLayout(layout);
        buttonPanel.add(okButton, CC.xy(2, 2));
        buttonPanel.add(cancelButton, CC.xy(4, 2));

        JLabel bez = new JLabel(angabe);

        name = new JIntegerField(value, min, max, true, false);
        name.addKeyListener(new NameListener());

        getContentPane().add(new JLabel(note), CC.xyw(2, 2, 3));
        getContentPane().add(bez, CC.xy(2, 4));
        getContentPane().add(name, CC.xy(4, 4));
        getContentPane().add(buttonPanel, CC.xywh(2, 6, 3, 1));

        pack();
    }

    void doClose(final int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    final class NameListener implements KeyListener {

        @Override
        public void keyPressed(final KeyEvent evt) {
            switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                doClose(RET_OK);
                break;
            case KeyEvent.VK_ESCAPE:
                doClose(RET_CANCEL);
                break;
            default:
                break;
            }
        }

        @Override
        public void keyReleased(final KeyEvent arg0) {
            keyPressed(arg0);
        }

        @Override
        public void keyTyped(final KeyEvent arg0) {
            keyPressed(arg0);
        }
    }

    final class CancelActionListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent evt) {
            doClose(RET_CANCEL);
        }
    }

    final class OkActionListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent evt) {
            if (!name.isValidInt()) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                return;
            }
            doClose(RET_OK);
        }
    }

    final class CancelWindowAdapter extends WindowAdapter {

        @Override
        public void windowClosing(final WindowEvent evt) {
            doClose(RET_CANCEL);
        }
    }
}
