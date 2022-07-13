package joel.opengl.maths;

public class Vec3f extends Vecf {

    public Vec3f(float defaultValue) {
        super(3, defaultValue);
    }

    public Vec3f() {
        super(3, 0.0f);
    }

    public Vec3f(float x, float y, float z) {
        super(x, y, z);
    }

    public Vec3f(double x, double y, double z) {
        super(x, y, z);
    }

    public Vec3f clone() {
        return new Vec3f(data[0], data[1], data[2]);
    }

    public float x() {
        return data[0];
    }

    public float y() {
        return data[1];
    }

    public float z() {
        return data[2];
    }

    public Vec3f cross(Vec3f vec) {
        return new Vec3f(
                data[1]*vec.data[2] - data[2]*vec.data[1],
                data[2]*vec.data[0] - data[0]*vec.data[2],
                data[0]*vec.data[1] - data[1]*vec.data[0]);
    }

    /*
      xyz axis are oriented such that x X y = z, y X z = x, z X x = y where X is cross product.
      yaw = pitch = 0 corresponds to vector (1, 0, 0) i.e. parallel to x axis.
      Positive yaw direction is counterclockwise about positive y axis.
      Note: (yaw = pi/2, pitch = 0) -> (0, 0, -1) so positive yaw first hits negative z axis.
      Positive pitch direction is up from the x-z plane towards positive y axis.
     */
    public static Vec3f getDirectionVector(float yaw, float pitch) {
        float ca = (float) Math.cos(yaw), sa = (float) Math.cos(yaw), cb = (float) Math.cos(pitch), sb = (float) Math.sin(pitch);
        return new Vec3f(ca*cb, sb, sa*cb);
    }

}
