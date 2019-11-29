package de.df.jutils.graphics;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class Graphics2DProxy extends Graphics2D {

    @Override
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        source.draw3DRect(x, y, width, height, raised);
    }

    @Override
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        // System.err.println("fill3DRect: " + x + "/" + y + " " + width + "/"
        // + height + " " + raised);
        // source.fill3DRect(x, y, width, height, raised);
    }

    @Override
    public Graphics create(int x, int y, int width, int height) {
        return new Graphics2DProxy(source.create(x, y, width, height));
    }

    @Override
    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        source.drawBytes(data, offset, length, x, y);
    }

    @Override
    public void drawChars(char[] data, int offset, int length, int x, int y) {
        source.drawChars(data, offset, length, x, y);
    }

    @Override
    public void drawPolygon(Polygon p) {
        source.drawPolygon(p);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        // System.err.println("drawRect: " + x + "/" + y + " " + width + "/"
        // + height);
        source.drawRect(x, y, width, height);
    }

    @Override
    public void fillPolygon(Polygon p) {
        // System.err.println("fillPolygon: " + p);
        source.fillPolygon(p);
    }

    @Override
    public Rectangle getClipBounds(Rectangle r) {
        return source.getClipBounds(r);
    }

    @Override
    @Deprecated
    public Rectangle getClipRect() {
        return source.getClipRect();
    }

    @Override
    public FontMetrics getFontMetrics() {
        return source.getFontMetrics();
    }

    @Override
    public boolean hitClip(int x, int y, int width, int height) {
        return source.hitClip(x, y, width, height);
    }

    private final Graphics2D source;

    public Graphics2DProxy(Graphics source) {
        this.source = (Graphics2D) source;
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        source.addRenderingHints(hints);
    }

    @Override
    public void clip(Shape s) {
        source.clip(s);
    }

    @Override
    public void draw(Shape s) {
        // System.err.println("draw: " + s);
        source.draw(s);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        source.drawGlyphVector(g, x, y);

    }

    @Override
    public String toString() {
        return "Proxy for " + source.toString();
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        System.err.println("drawImage: " + img);
        return source.drawImage(img, xform, obs);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        System.err.println("drawImage: " + img);
        source.drawImage(img, op, x, y);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        System.err.println("drawImage: " + img);
        source.drawRenderableImage(img, xform);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        System.err.println("drawImage: " + img);
        source.drawRenderedImage(img, xform);
    }

    @Override
    public void drawString(String str, int x, int y) {
        // System.err.println("drawString: " + str);
        source.drawString(str, x, y);
    }

    @Override
    public void drawString(String str, float x, float y) {
        // System.err.println("drawString: " + str);
        source.drawString(str, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        // System.err.println("drawString: " + iterator);
        source.drawString(iterator, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        // System.err.println("drawString: " + iterator);
        source.drawString(iterator, x, y);
    }

    @Override
    public void fill(Shape s) {
        System.err.println("fill: " + s);
        source.fill(s);
    }

    @Override
    public Color getBackground() {
        if (source.getBackground().equals(Color.WHITE)) {
            return Color.RED;
        }
        return source.getBackground();
    }

    @Override
    public Composite getComposite() {
        return source.getComposite();
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        System.err.println("getDeviceConfiguration");
        return source.getDeviceConfiguration();
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return source.getFontRenderContext();
    }

    @Override
    public Paint getPaint() {
        return source.getPaint();
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
        return source.getRenderingHint(hintKey);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        // System.err.println("clearRect: " + x + "/" + y + " " + width + "/"
        // + height);
        // if (width > 600) {
        // return;
        // }
        // if (getBackground().equals(Color.WHITE)) {
        // return;
        // }
        // source.clearRect(x, y, width, height);
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        source.clipRect(x, y, width, height);
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        System.err.println("copyArea: " + x + "/" + y + " " + width + "/" + height + " " + dx + "/" + dy);
        source.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public Graphics create() {
        return new Graphics2DProxy(source.create());
    }

    @Override
    public void dispose() {
        source.dispose();
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        source.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {

        return source.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return source.drawImage(img, x, y, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return source.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return source.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        return source.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return source.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        source.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        source.drawOval(x, y, width, height);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        source.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        source.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        source.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public boolean equals(Object obj) {
        return source.equals(obj);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        // System.err.println("fillArc: " + x + "/" + y + " " + width + "/"
        // + height);
        source.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        // System.err.println("fillOval: " + x + "/" + y + " " + width + "/"
        // + height);
        source.fillOval(x, y, width, height);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        // System.err.println("fillPolygon: " + xPoints + " " + yPoints);
        source.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        // System.err.println("fillRect: " + x + "/" + y + " " + width + "/"
        // + height);
        // System.err.println(" -> " + source.getColor());
        // if (width > 600) {
        // return;
        // }
        // if (source.getColor().equals(Color.WHITE)) {
        // return;
        // }
        // source.fillRect(x, y, width, height);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        // System.err.println("fillRoundRect: " + x + "/" + y + " " + width +
        // "/"
        // + height);
        // source.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public Shape getClip() {
        return source.getClip();
    }

    @Override
    public Rectangle getClipBounds() {
        return source.getClipBounds();
    }

    @Override
    public Color getColor() {
        if (source.getColor().equals(Color.WHITE)) {
            return Color.RED;
        }
        return source.getColor();
    }

    @Override
    public Font getFont() {
        return source.getFont();
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return source.getFontMetrics(f);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return source.getRenderingHints();
    }

    @Override
    public Stroke getStroke() {
        return source.getStroke();
    }

    @Override
    public AffineTransform getTransform() {
        return source.getTransform();
    }

    @Override
    public int hashCode() {
        return source.hashCode();
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return source.hit(rect, s, onStroke);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        source.rotate(theta, x, y);
    }

    @Override
    public void rotate(double theta) {
        source.rotate(theta);
    }

    @Override
    public void scale(double sx, double sy) {
        source.scale(sx, sy);
    }

    @Override
    public void setBackground(Color color) {
        source.setBackground(color);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        source.setClip(x, y, width, height);
    }

    @Override
    public void setClip(Shape clip) {
        source.setClip(clip);
    }

    @Override
    public void setColor(Color c) {
        source.setColor(c);
    }

    @Override
    public void setComposite(Composite comp) {
        source.setComposite(comp);
    }

    @Override
    public void setFont(Font font) {
        source.setFont(font);
    }

    @Override
    public void setPaint(Paint paint) {
        source.setPaint(paint);
    }

    @Override
    public void setPaintMode() {
        source.setPaintMode();
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
        source.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        source.setRenderingHints(hints);
    }

    @Override
    public void setStroke(Stroke s) {
        source.setStroke(s);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        source.setTransform(Tx);
    }

    @Override
    public void setXORMode(Color c1) {
        source.setXORMode(c1);
    }

    @Override
    public void shear(double shx, double shy) {
        source.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
        source.transform(Tx);
    }

    @Override
    public void translate(double tx, double ty) {
        source.translate(tx, ty);
    }

    @Override
    public void translate(int x, int y) {
        source.translate(x, y);
    }
}