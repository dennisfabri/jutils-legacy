/*
 * Created on 14.10.2005
 */
package de.df.jutils.gui.border;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import com.jgoodies.forms.factories.Paddings;

public final class BorderUtils {

    private BorderUtils() {
        // Hide constructor
    }

    public static Border createSpaceBorder(int space) {
        space = Math.max(1, (int) Math.round(0.8 * space));
        StringBuilder sb = new StringBuilder();
        sb.append(space);
        sb.append("dlu,");
        sb.append(space);
        sb.append("dlu,");
        sb.append(space);
        sb.append("dlu,");
        sb.append(space);
        sb.append("dlu");
        return Paddings.createPadding(sb.toString());
    }

    public static Border createSpaceBorder() {
        return createSpaceBorder(5);
    }

    public static Border createLabeledBorder(String title) {
        return createLabeledBorder(title, false, false, true);
    }

    public static Border createLabeledBorder(String title, boolean inner) {
        return createLabeledBorder(title, inner, false, true);
    }

    public static Border createLineLabeledBorder(String title) {
        return createLabeledBorder(title, false, false, false);
    }

    public static Border createLabeledBorder(String title, boolean innergap, boolean outergap, boolean shadow) {
        if ((title == null) || (title.length() == 0)) {
            title = " ";
        }
        Border result = null;
        result = new LabeledBorder(title);
        if (shadow) {
            result = new CompoundBorder(new ShadowBorder(), result);
        } else {
            result = new CompoundBorder(new LineBorder(UIManager.getColor("ToolBar.shadow"), 1), result);
        }
        if (innergap) {
            result = new CompoundBorder(result, createSpaceBorder());
        }
        if (outergap) {
            result = new CompoundBorder(createSpaceBorder(), result);
        }
        return result;
    }
}
