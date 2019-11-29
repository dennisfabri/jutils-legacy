package de.df.jutils.gui.plaf;

import static de.df.jutils.gui.util.GraphicsUtils.paintGradient;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ComponentUI;

import com.l2fprod.common.swing.JTaskPaneGroup;
import com.l2fprod.common.swing.plaf.basic.BasicTaskPaneGroupUI;

import de.df.jutils.graphics.ColorUtils;
import de.df.jutils.gui.border.ShadowBorder;

/**
 * Metal implementation of the <code>JTaskPaneGroup</code> UI. <br>
 * 
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class GradientTaskPaneGroupUI extends BasicTaskPaneGroupUI {

    public static ComponentUI createUI(JComponent c) {
        return new GradientTaskPaneGroupUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        LookAndFeel.installColorsAndFont(group, "control", "TaskPaneGroup.foreground", "TitledBorder.font");

        LookAndFeel.installColorsAndFont((JComponent) group.getContentPane(), "control", "TaskPaneGroup.foreground", "TitledBorder.font");
        group.setOpaque(false);
        group.setBackground(UIManager.getColor("control"));
    }

    @Override
    protected Border createContentPaneBorder() {
        return null;
    }

    @Override
    protected Border createPaneBorder() {
        return new CompoundBorder(new ShadowBorder(), new JAuswertungPaneBorder());
    }

    /**
     * The border of the taskpane group paints the "text", the "icon", the "expanded" status and the "special" type.
     */
    class JAuswertungPaneBorder extends PaneBorder {

        private final Color defaultTitleForeground;

        public JAuswertungPaneBorder() {
            super();

            borderColor = UIManager.getColor("controlShadow");

            titleForeground = UIManager.getColor("InternalFrame.activeTitleForeground");

            titleBackgroundGradientStart = UIManager.getColor("InternalFrame.activeTitleBackground");
            titleBackgroundGradientEnd = UIManager.getColor("InternalFrame.activeTitleGradient");

            titleOver = UIManager.getColor("InternalFrame.inactiveTitleForeground");

            if (titleBackgroundGradientStart == null) {
                titleBackgroundGradientStart = UIManager.getColor("InternalFrame.borderColor");
            }
            if (titleBackgroundGradientEnd == null) {
                titleBackgroundGradientEnd = UIManager.getColor("InternalFrame.borderShadow");
            }
            if (titleBackgroundGradientStart == null) {
                titleBackgroundGradientStart = Color.GRAY;
            }
            if (titleBackgroundGradientEnd == null) {
                titleBackgroundGradientEnd = Color.LIGHT_GRAY;
            }
            if (titleForeground == null) {
                Color medium = ColorUtils.calculateColor(titleBackgroundGradientStart, titleBackgroundGradientEnd, 0.5);
                titleForeground = ColorUtils.invert(medium);
            }

            if (titleOver == null) {
                titleOver = titleForeground.darker();
                if (titleOver.equals(titleForeground)) {
                    titleOver = specialTitleBackground.brighter();
                }
            }

            defaultTitleForeground = titleForeground;
        }

        @SuppressWarnings("synthetic-access")
        @Override
        protected void paintTitleBackground(JTaskPaneGroup jt, Graphics g) {
            Insets i = new Insets(0, 0, 0, 0);
            if (jt.getBorder() != null) {
                Border b = jt.getBorder();
                if (b instanceof CompoundBorder) {
                    CompoundBorder cb = (CompoundBorder) b;
                    b = cb.getOutsideBorder();
                }
                i = b.getBorderInsets(jt);
            }
            mix = paintGradient((Graphics2D) g.create(), i.left, i.top, jt.getWidth() - i.left - i.right, getTitleHeight() - i.top - i.bottom,
                    titleBackgroundGradientStart, titleBackgroundGradientEnd);
            titleForeground = ColorUtils.contrastColor(mix, defaultTitleForeground);
        }

        private Color mix;

        @Override
        protected void paintExpandedControls(JTaskPaneGroup group1, Graphics g, int x, int y, int width, int height) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(getPaintColor(group1));

            paintRectAroundControls(group1, g, x, y, width, height, g.getColor(), g.getColor());
            paintChevronControls(group1, g, x, y, width, height);

            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        @Override
        protected boolean isMouseOverBorder() {
            return true;
        }
    }

}