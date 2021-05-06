/*
 * Created on 20.01.2005
 */
package de.df.jutils.gui.renderer;

import javax.swing.SwingConstants;

public class ConditionalCenterRenderer extends AlignmentCellRenderer {

    protected int index = 0;

    private static int[] leftArray(int i) {
        int[] result = new int[i];
        for (int x = 0; x < i; x++) {
            result[x] = SwingConstants.LEFT;
        }
        return result;
    }

    public ConditionalCenterRenderer(int number) {
        super(leftArray(number), SwingConstants.CENTER);
        index = number;
    }
}