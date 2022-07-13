package joel.opengl.maths;

public class Line3D {

    public Vec3f point, direction;

    public Line3D(Vec3f point, Vec3f direction) {
        this.point = point;
        this.direction = (Vec3f) direction.clone().normalize();
    }

    public Line3D(float x, float y, float z, float dx, float dy, float dz) {
        this(new Vec3f(x, y, z), new Vec3f(dx, dy, dz));
    }

    public Line3D(Vec3f point, float yaw, float pitch) {
        this(point, Vec3f.getDirectionVector(yaw, pitch));
    }

    public Vec3f closestPointTo(Vec3f targetPoint) {
        float mew =
                direction.x() * (targetPoint.x() - point.x()) +
                direction.y() * (targetPoint.y() - point.y()) +
                direction.z() * (targetPoint.z() - point.z());
        return (Vec3f) direction.clone().multiply(mew).add(point);
    }

    public double closestDistanceSquaredTo(Vec3f targetPoint) {
        return closestPointTo(targetPoint).distanceSquaredTo(targetPoint);
    }

    public double closestDistanceTo(Vec3f targetPoint) {
        return closestPointTo(targetPoint).distanceTo(targetPoint);
    }

}
