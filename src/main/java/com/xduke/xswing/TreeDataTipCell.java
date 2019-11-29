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
import java.awt.Rectangle;

import javax.swing.JTree;
import javax.swing.tree.*;

class TreeDataTipCell implements DataTipCell {
    private final JTree tree;
    private final int   rowIndex;

    public TreeDataTipCell(JTree tree, int rowIndex) {
        this.tree = tree;
        this.rowIndex = rowIndex;
    }

    @Override
    public boolean isSet() {
        return rowIndex >= 0;
    }

    @Override
    public Rectangle getCellBounds() {
        TreePath treePath = tree.getPathForRow(rowIndex);
        Rectangle cellRect = tree.getPathBounds(treePath);
        return cellRect;
    }

    @Override
    public Component getRendererComponent() {
        TreeModel treeModel = tree.getModel();
        TreePath treePath = tree.getPathForRow(rowIndex);
        TreeCellRenderer renderer = tree.getCellRenderer();
        boolean isSelected = tree.isPathSelected(treePath);
        boolean isExpanded = tree.isExpanded(treePath);
        boolean hasFocus = tree.hasFocus() && rowIndex == tree.getLeadSelectionRow();
        Object item = treePath.getLastPathComponent();
        boolean isLeaf = treeModel.isLeaf(item);
        Component component = renderer.getTreeCellRendererComponent(tree, item, isSelected, isExpanded, isLeaf, rowIndex, hasFocus);
        component.setFont(tree.getFont());
        return component;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TreeDataTipCell treeDataTipCell = (TreeDataTipCell) o;

        if (rowIndex != treeDataTipCell.rowIndex) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return rowIndex;
    }
}
