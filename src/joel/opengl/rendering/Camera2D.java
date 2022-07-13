package joel.opengl.rendering;

import joel.opengl.maths.BoundingBoxAA2D;
import joel.opengl.maths.Vec2f;

import static org.lwjgl.opengl.GL11.glViewport;

public class Camera2D {

    private static final double translationSpeed = 0.01f, zoomSpeed = 0.98f, discreteZoomSpeed = 0.9f;

    public int width, height;

    // "Coordinate length" * pixelScale = "number of pixels that length appears as on screen".
    // i.e. a 1x1 square in coordinate space is displayed as a pixelScale x pixelScale square in screen space (pixels).
    public double x, y, zoom, pixelScale;
    // stored as doubles to avoid reaching machine epsilon on big zooms
    // pretty much guaranteed to run into epsilon artefacts on rendering side before you do on the computation side

    public boolean translationChanged = true, zoomChanged = true, resolutionChanged = true;
    private boolean zoomIn = false, zoomOut = false;

    public Camera2D(int width, int height, double x, double y, double zoom) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.zoom = zoom;
        updatePixelScale();
    }

    public void updatePixelScale() {
        pixelScale = (double) height / (2.0 * zoom);
    }

    public float getPixelLength(float coordinateLength) {
        return coordinateLength * (float) pixelScale;
    }

    public float getCoordinateLength(float pixelLength) {
        return pixelLength / (float) pixelScale;
    }

    public void resetHasChanged() {
        translationChanged = false;
        zoomChanged = false;
        resolutionChanged = false;
    }

    public void update(boolean up, boolean down, boolean left, boolean right, boolean in, boolean out, boolean reset) {

        if (resolutionChanged) glViewport(0, 0, width, height);

        // If translation/zoom was changed outside of this function, don't want to overwrite the changed boolean before the change is handled.
        translationChanged = translationChanged || (up ^ down) || (left ^ right);
        zoomChanged = zoomChanged || (in ^ out) || (zoomIn ^ zoomOut);

        if (up) y += translationSpeed * zoom;
        if (down) y -= translationSpeed * zoom;
        if (left) x -= translationSpeed * zoom;
        if (right) x += translationSpeed * zoom;
        if (in) zoom *= zoomSpeed;
        if (out) zoom /= zoomSpeed;
        if (zoomIn) zoom *= discreteZoomSpeed;
        if (zoomOut) zoom /= discreteZoomSpeed;
        if (reset) {
            x = y = 0.0f;
            zoom = 1.0f;
            translationChanged = zoomChanged = true;
        }

        if (zoomChanged || resolutionChanged) updatePixelScale();
        zoomIn = zoomOut = false;

    }

    public void setTranslation(float x, float y) {
        this.x = x;
        this.y = y;
        translationChanged = true;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
        zoomChanged = true;
    }

    // Tells camera to zoom in/out when it next updates. Zoom amount is controlled by discreteZoomSpeed variable.
    public void doZoomIn() {
        this.zoomIn = true;
    }

    public void doZoomOut() {
        this.zoomOut = true;
    }

    public Vec2f getGridCoordinate(float screenX, float screenY) {
        float aspectRatio = (float) width / (float) height;

        Vec2f coord = new Vec2f(screenX, screenY);
        coord.divide(width, height).multiply(2.0f).subtract(1.0f, 1.0f).multiply(zoom);
        coord.data[0] *= aspectRatio;
        coord.add(x, y);

        return coord;
    }

    public Vec2f getGridCoordinate(Vec2f screenPosition) {
        return getGridCoordinate(screenPosition.x(), screenPosition.y());
    }

    public Vec2f getScreenCoordinate(float gridX, float gridY) {
        float aspectRatio = (float) width / (float) height;

        Vec2f coord = new Vec2f(gridX, gridY);
        coord.subtract(x, y);
        coord.data[0] /= aspectRatio;
        coord.divide(zoom).add(1.0f, 1.0f).multiply(0.5f).multiply(width, height);

        return coord;
    }

    public Vec2f getScreenCoordinate(Vec2f gridPosition) {
        return getScreenCoordinate(gridPosition.x(), gridPosition.y());
    }

    public BoundingBoxAA2D getViewport() {
        return new BoundingBoxAA2D(new Vec2f(x, y), getCoordinateLength(width), getCoordinateLength(height));
    }

}
