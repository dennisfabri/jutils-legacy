package de.df.jutils.gui;

import java.awt.Color;

import org.pushingpixels.substance.api.colorscheme.BaseLightColorScheme;

import de.df.jutils.graphics.ColorUtils;

public class SimpleColorScheme extends BaseLightColorScheme {

    private final Color base;
    private final Color foreground;

    private Color       light1;
    private Color       light2;
    private Color       light3;

    private Color       dark1;
    private Color       dark2;

    public SimpleColorScheme(Color base, Color foreground) {
        super("");
        this.base = base;
        this.foreground = foreground;

        dark2 = base.darker();
        dark1 = ColorUtils.calculateColor(base, dark2, 0.5);

        light2 = base.brighter();
        light1 = ColorUtils.calculateColor(base, light2, 0.5);
        // light2 = ColorUtils.calculateColor(base, light3, 0.67);
        light3 = light2.brighter();
    }

    @Override
    public Color getDarkColor() {
        return dark1;
    }

    @Override
    public Color getExtraLightColor() {
        return light2;
    }

    @Override
    public Color getForegroundColor() {
        return foreground;
    }

    @Override
    public Color getLightColor() {
        return light1;
    }

    @Override
    public Color getMidColor() {
        return base;
    }

    @Override
    public Color getUltraDarkColor() {
        return dark2;
    }

    @Override
    public Color getUltraLightColor() {
        return light3;
    }
}