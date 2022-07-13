package joel.opengl.maths;

public class Vec4f extends Vecf {

    public Vec4f(float defaultValue) {
        super(4, defaultValue);
    }

    public Vec4f() {
        super(4, 0.0f);
    }

    public Vec4f(float x, float y, float z, float w) {
        super(x, y, z, w);
    }

    public Vec4f(double x, double y, double z, double w) {
        super(x, y, z, w);
    }

    public Vec4f clone() {
        return new Vec4f(data[0], data[1], data[2], data[3]);
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

    public float w() {
        return data[3];
    }

}
