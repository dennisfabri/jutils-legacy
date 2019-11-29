package de.df.jutils.util;

public class DynamicField2D<T> {

    private Object[][] data;

    public DynamicField2D(int width, int height) {
        data = new Object[width][height];
    }

    public DynamicField2D() {
        this(0, 0);
    }

    private void fit(int x, int y) {
        int w = Math.max(data.length, x + 1);
        int h = Math.max(data[0].length, y + 1);
        if (w > data.length || h > data[0].length) {
            Object[][] d = new Object[w][h];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    d[i][j] = data[i][j];
                }
            }
            data = d;
        }
    }

    public void put(int x, int y, T value) {
        if (x >= data.length || y >= data[x].length) {
            fit(x, y);
        }
        data[x][y] = value;
    }

    public void remove(int x, int y) {
        if (x >= data.length || y >= data[x].length) {
            return;
        }
        data[x][y] = null;
    }

    @SuppressWarnings("unchecked")
    public T get(int x, int y) {
        if (x >= data.length || y >= data[x].length) {
            return null;
        }
        return (T) data[x][y];
    }
}
