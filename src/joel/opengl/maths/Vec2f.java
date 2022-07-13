package joel.opengl.maths;

public class Vec2f extends Vecf {

    public Vec2f(float defaultValue) {
        super(2, defaultValue);
    }

    public Vec2f() {
        super(2, 0.0f);
    }

    public Vec2f(float x, float y) {
        super(x, y);
    }

    public Vec2f(double x, double y) {
        super(x, y);
    }

    public float x() {
        return data[0];
    }

    public float y() {
        return data[1];
    }

    public float width() {
        return data[0];
    }

    public float height() {
        return data[1];
    }

    public Vec2f clone() {
        return new Vec2f(data[0], data[1]);
    }

    public static Vec2f getDirectionVector(float theta) {
        return new Vec2f(Math.cos(theta), Math.sin(theta));
    }

}
