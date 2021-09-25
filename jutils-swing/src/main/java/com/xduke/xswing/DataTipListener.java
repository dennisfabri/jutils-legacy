/*
 * Copyright (c) 2002 - 2005, Stephen Kelvin Friedrich. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * - Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.xduke.xswing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

abstract class DataTipListener extends MouseInputAdapter implements ComponentListener {

    private static final int TolerancePixelCount = 1;

    private DataTipPopup dataTipPopup;

    DataTipListener() {
        // Nothing to do
    }

    /**
     * @return the cell that is at position 'point' in the component or
     *         DataTipCell.NONE if there isn't a cell at that point.
     */
    abstract DataTipCell getCell(JComponent component, Point point);

    /**
     * If the user presses a mouse button on a popup, Swing's behaviour depends on
     * the popup type: - lightweight popup: This case is handled here. Because the
     * TipComponent.contains(int x, int y) is overriden to always return false Swing
     * will dispath the event directly to the parent component. - heavyweight popup:
     * Swing will dispatch the event to the popup's window, which is handled in
     * DataTipPopup.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // hideTip(); Can't: Double click would not work. Click count of the
        // second click would be '1', because it would
        // go to a different window (for heavyweight datatips).
        updateDataTip();
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        updateDataTip();
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        checkShowOrHide(event);
    }

    @Override
    public void mouseExited(MouseEvent event) {
        checkShowOrHide(event);
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        checkShowOrHide(event);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        checkShowOrHide(event);
    }

    private void updateDataTip() {
        if (dataTipPopup != null) {
            dataTipPopup.update();
        }
    }

    private void checkShowOrHide(MouseEvent event) {
        JComponent component = (JComponent) event.getSource();
        Point mousePosition = event.getPoint();
        checkShowOrHide(component, mousePosition);
    }

    private void checkShowOrHide(JComponent component, Point mousePosition) {
        Window windowAncestor = SwingUtilities.getWindowAncestor(component);
        if (windowAncestor == null || !windowAncestor.isActive()) {
            hideTip();
            return;
        }

        DataTipCell dataTipCell = getCell(component, mousePosition);
        Rectangle visRect = component.getVisibleRect();

        if (!visRect.contains(mousePosition)) {
            dataTipCell = DataTipCell.NONE;
        }

        DataTipCell currentPopupCell = getCurrentPopupCell();
        if (dataTipCell.equals(currentPopupCell)) {
            return;
        }

        hideTip();
        if (!dataTipCell.isSet()) {
            return;
        }

        dataTipPopup = createPopup(component, mousePosition, dataTipCell);
    }

    private DataTipCell getCurrentPopupCell() {
        if (!isTipShown()) {
            return DataTipCell.NONE;
        }
        return dataTipPopup.getCell();
    }

    private DataTipPopup createPopup(JComponent component, Point mousePosition, DataTipCell dataTipCell) {
        if (!isMouseWithin(component, mousePosition, dataTipCell)) {
            return null;
        }
        if (!shouldShowToolTip(component, dataTipCell)) {
            return null;
        }

        Component rendererComponent = dataTipCell.getRendererComponent();
        Rectangle cellBounds = dataTipCell.getCellBounds();

        Dimension preferredSize = rendererComponent.getPreferredSize();
        Point tipPosition = cellBounds.getLocation();
        int width = Math.max(cellBounds.width, preferredSize.width);
        int height = Math.max(cellBounds.height, preferredSize.height);
        Dimension tipDimension = new Dimension(width, height);
        return new DataTipPopup(component, dataTipCell, tipPosition, tipDimension);
    }

    private boolean isMouseWithin(JComponent component, Point mousePosition, DataTipCell dataTipCell) {
        Rectangle cellBounds = dataTipCell.getCellBounds();

        Rectangle visRect = component.getVisibleRect();
        Rectangle visibleCellRectangle = cellBounds.intersection(visRect);
        return visibleCellRectangle.contains(mousePosition);
    }

    private boolean shouldShowToolTip(JComponent component, DataTipCell dataTipCell) {
        Component rendererComponent = dataTipCell.getRendererComponent();
        Rectangle cellBounds = dataTipCell.getCellBounds();
        Rectangle visRect = component.getVisibleRect();

        Dimension rendCompDim = rendererComponent.getMinimumSize();
        Rectangle rendCompBounds = new Rectangle(cellBounds.getLocation(), rendCompDim);
        
        // Do not render the popup because of one pixel
        Rectangle reducedRendCompBounds = new Rectangle((int) rendCompBounds.getX() + TolerancePixelCount,
                (int) rendCompBounds.getY() + TolerancePixelCount,
                (int) rendCompBounds.getWidth() - 2 * TolerancePixelCount,
                (int) rendCompBounds.getHeight() - 2 * TolerancePixelCount);
        return !cellBounds.contains(reducedRendCompBounds) || !visRect.contains(reducedRendCompBounds);
    }

    private boolean isTipShown() {
        return dataTipPopup != null && dataTipPopup.isTipShown();
    }

    private void hideTip() {
        if (dataTipPopup != null) {
            dataTipPopup.hideTip();
            dataTipPopup = null;
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        checkShowOrHide(e);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        checkShowOrHide(e);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        checkShowOrHide(e);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        hideTip();
    }

    private void checkShowOrHide(ComponentEvent e) {
        JComponent component = (JComponent) e.getSource();
        Point mousePosition = getCurrentMousePosition();
        if (mousePosition == null) {
            hideTip();
        } else {
            SwingUtilities.convertPointFromScreen(mousePosition, component);
            checkShowOrHide(component, mousePosition);
        }
    }

    private static Point getCurrentMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }
}