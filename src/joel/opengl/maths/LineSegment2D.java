package joel.opengl.maths;

import javax.sound.sampled.Line;

public class LineSegment2D {

    public Vec2f start, end;

    public LineSegment2D(Vec2f start, Vec2f end) {
        this.start = start;
        this.end = end;
    }

    public LineSegment2D(float a, float b, float x, float y) {
        this(new Vec2f(a, b), new Vec2f(x, y));
    }

    public boolean containsPoint(Vec2f point) {
        Vec2f d1 =(Vec2f) end.clone().subtract(start), d2 = (Vec2f) point.clone().subtract(start);
        float dot = d1.dot(d2);
        Math.getExponent(1.0f);
        return dot >= 0.0f && d1.lengthSquared() + d2.lengthSquared() - dot*dot <= 0.00001f;

    }

    public boolean intersects(LineSegment2D otherLine) {
        Vec2f d1 = (Vec2f) end.clone().subtract(start), d2 = (Vec2f) otherLine.end.clone().subtract(otherLine.start);
        float det = d1.x() * d2.y() - d1.y() * d2.x();
        if (det == 0.0f) {
            float ld1 = d1.lengthSquared(), ld2 = d2.lengthSquared();
            return Vecf.lengthSquared(otherLine.end.clone().subtract(start)) < ld1 + ld2 + 2.0f * (float) Math.sqrt(ld1 * ld2);
        } else {
            float lambda = (otherLine.start.x() * d2.y() - otherLine.start.y() * d2.x() - start.x() * d1.y() + start.y() * d2.x()) / det;
            float mew = (start.x() * d1.y() - otherLine.start.x() * d1.y() - start.y() * d1.x() + otherLine.start.y() * d1.x()) / -det;
            return 0.0f <= lambda && lambda <= 1.0f && 0.0f <= mew && mew <= 1.0f;
        }
    }

    // Connects n points into n line segments s.t. L0 = p[0] -> p[1] , L1 = p[1] -> p[2], Ln-1 = p[n-1] -> p[0]
    public boolean intersectsConnectedPoints(Vec2f... points) {
        if (points.length == 0) return false;
        else if (points.length == 1) {
            return containsPoint(points[0]);
        } else if (points.length == 2) {
            return intersects(new LineSegment2D(points[0], points[1]));
        } else {
            for (int i = 0; i < points.length - 1; i++) if (intersects(new LineSegment2D(points[i], points[i+1]))) return true;
            return intersects(new LineSegment2D(points[points.length - 1], points[0]));
        }
    }

    public static LineSegment2D[] fromPointsCycle(Vec2f... points) {
        if (points.length < 2) return null;
        Vec2f[] arr = new Vec2f[points.length + 1];
        for (int i = 0; i < points.length; i++) arr[i] = points[i];
        arr[points.length] = points[0];
        return fromPoints(points);
    }

    public static LineSegment2D[] fromPoints(Vec2f... points) {
        if (points.length < 2) return null;

        LineSegment2D[] arr = new LineSegment2D[points.length - 1];
        for (int i = 0; i < arr.length; i++) arr[i] = new LineSegment2D(points[i], points[i+1]);

        return arr;
    }

}
