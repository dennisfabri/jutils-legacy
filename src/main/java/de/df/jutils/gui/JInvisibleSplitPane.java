/*
 * Created on 09.06.2005
 */
package de.df.jutils.gui;

import java.awt.Component;

import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class JInvisibleSplitPane extends JSplitPane {

    private static final long serialVersionUID = -5210316410999567737L;

    public JInvisibleSplitPane() {
        super();
        makeSplitPaneInvisible(this);
    }

    public JInvisibleSplitPane(int newOrientation) {
        super(newOrientation);
        makeSplitPaneInvisible(this);
    }

    public JInvisibleSplitPane(int newOrientation, boolean newContinuousLayout) {
        super(newOrientation, newContinuousLayout);
        makeSplitPaneInvisible(this);
    }

    public JInvisibleSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
        super(newOrientation, newLeftComponent, newRightComponent);
        makeSplitPaneInvisible(this);
    }

    public JInvisibleSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent, Component newRightComponent) {
        super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
        makeSplitPaneInvisible(this);
    }

    public static void makeSplitPaneInvisible(JSplitPane pane) {
        pane.setBorder(null);
        SplitPaneUI splitPaneUI = pane.getUI();
        if (splitPaneUI instanceof BasicSplitPaneUI) {
            ((BasicSplitPaneUI) splitPaneUI).getDivider().setBorder(null);
        }
    }
}