package de.df.jutils.gui.util;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Based on code from "Santhosh Kumar T" posten on jroller. Modified by Dennis Fabri.
 * 
 * @author Santhosh Kumar T
 * @author Dennis Fabri
 * @email santhosh@in.fiorano.com
 * @url
 *      http://jroller.com/page/santhosh/20050625#are_you_missing_maximize_button
 */
public final class ModalFrameUtil {

    private ModalFrameUtil() {
        // Hide constructor
    }

    /**
     * show the given frame as modal to the specified owner. NOTE: this method
     * returns only after the modal frame is closed.
     */
    public static void showAsModal(final Frame frame, final Frame owner) {
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                owner.setEnabled(true);
                owner.toFront();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                owner.setEnabled(false);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                owner.setEnabled(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                owner.removeWindowListener(this);
                owner.setEnabled(true);

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        owner.toFront();
                    }
                });
            }
        });

        owner.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                if (frame.isShowing()) {
                    frame.toFront();
                } else {
                    owner.removeWindowListener(this);
                }
            }
        });

        frame.setVisible(true);
    }

    /**
     * show the given frame as modal to the specified owner. NOTE: this method
     * returns only after the modal frame is closed.
     */
    public static void showAsModal(final Frame frame, final Dialog owner) {
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                owner.setEnabled(true);
                owner.toFront();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                owner.setEnabled(false);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                owner.setEnabled(true);
                owner.removeWindowListener(this);

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        owner.toFront();
                    }
                });
            }
        });

        owner.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                if (frame.isShowing()) {
                    frame.toFront();
                } else {
                    owner.removeWindowListener(this);
                }
            }
        });

        frame.setVisible(true);
    }
}