package de.df.jutils.gui.util;

public enum UIPerformanceMode {

    Default(0), Software(1), OpenGL(2);

    public final int value;

    private UIPerformanceMode(int v) {
        value = v;
    }
}
