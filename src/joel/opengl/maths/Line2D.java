package joel.opengl.maths;

public class Line2D {

    public Vec2f point, direction;

    public Line2D(Vec2f point, Vec2f direction) {
        this.point = point;
        this.direction = (Vec2f) direction.clone().normalize();
    }

    public Line2D(float x, float y, float dx, float dy) {
        this(new Vec2f(x, y), new Vec2f(dx, dy));
    }

    public Line2D(Vec2f point, float theta) {
        this(point, Vec2f.getDirectionVector(theta));
    }

    public Vec2f getIntersection(Line2D otherLine) {
        float det = direction.x() * otherLine.direction.y() - direction.y() * otherLine.direction.x();
        if (det == 0.0f) return null; // Lines parallel, intersection is not unique if it exists.
        float lambda = (otherLine.point.x() * otherLine.direction.y() - otherLine.point.y() * otherLine.direction.x() - point.x() * direction.y() + point.y() * otherLine.direction.x()) / det;
//        float mew = (point.x() * direction.y() - otherLine.point.x() * direction.y() - point.y() * direction.x() + otherLine.point.y() * direction.x()) / -det;
        return (Vec2f) direction.clone().multiply(lambda).add(point);
//        return (Vec2f) otherLine.direction.clone().multiply(mew).add(otherLine.point);
    }

}
