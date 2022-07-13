package joel.opengl.maths;

public class BoundingBoxAA2D {

    public float left, right, top, bottom;

    public BoundingBoxAA2D(float left, float right, float top, float bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public BoundingBoxAA2D(Vec2f corner1, Vec2f corner2) {
        if (corner1.x() > corner2.x()) {
            right = corner1.x();
            left = corner2.x();
        } else {
            right = corner2.x();
            left = corner1.x();
        }
        if (corner1.y() > corner2.y()) {
            top = corner1.y();
            bottom = corner2.y();
        } else {
            top = corner2.y();
            bottom = corner1.y();
        }
    }

    public BoundingBoxAA2D(Vec2f center, float width, float height) {
        this.left = center.x() - width/2.0f;
        this.right = left + width;
        this.bottom = center.y() - height/2.0f;
        this.top = bottom + height;
    }

    // Array formatted as x_0, y_0, x_1, y_1, x_2, y_2, ...
    public static BoundingBoxAA2D fromVerticesArray(float[] vertices) {
        if (vertices.length < 4) {
            if (vertices.length < 2) { // Given no points.
                return null;
            } else { // Given 1 point.
                return new BoundingBoxAA2D(vertices[0], vertices[0], vertices[1], vertices[1]);
            }
        }
        float minX, maxX, minY, maxY;
        minX = maxX = vertices[0];
        minY = maxY = vertices[1];
        for (int i = 1; i < vertices.length / 2; i++) { // Iterate through each point.
            float x = vertices[2*i], y = vertices[2*i + 1];
            if (x > maxX) maxX = x;
            if (x < minX) minX = x;
            if (y > maxY) maxY = y;
            if (y < minY) minY = y;
        }
        return new BoundingBoxAA2D(minX, maxX, maxY, minY);
    }

    public Vec2f getCenter() {
        return new Vec2f(left + (right-left) / 2.0f, bottom + (top-bottom) / 2.0f);
    }

    public float getWidth() {
        return right - left;
    }

    public float getHeight() {
        return top - bottom;
    }

    public Vec2f getSize() {
        return new Vec2f(getWidth(), getHeight());
    }

    public boolean intersectsX(BoundingBoxAA2D other) {
        return Maths.max(this.left, other.left) <= Maths.min(this.right, other.right);
    }

    public boolean intersectsY(BoundingBoxAA2D other) {
        return Maths.max(this.bottom, other.bottom) <= Maths.min(this.top, other.top);
    }

    public boolean intersects(BoundingBoxAA2D other) {
        return intersectsX(other) && intersectsY(other);
    }

}
