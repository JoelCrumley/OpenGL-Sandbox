package joel.opengl.maths;

public class Quaternion {

    public float scalar;
    public Vec3f vec;

    public Quaternion(float scalar, Vec3f vec) {
        this.scalar = scalar;
        this.vec = vec;
    }

    public Quaternion add(Quaternion other) {
        scalar += other.scalar;
        vec.add(other.vec);
        return this;
    }

    public Quaternion multiply(Quaternion other) {
        float newScalar = scalar * other.scalar - vec.dot(other.vec);
        vec = (Vec3f) vec.multiply(other.scalar).add(other.vec.clone().multiply(scalar)).add(vec.cross(other.vec));
        scalar = newScalar;
        return this;
    }

    public Quaternion multiply(float scalar) {
        this.scalar *= scalar;
        vec.multiply(scalar);
        return this;
    }

    public Quaternion divide(float scalar) {
        this.scalar /= scalar;
        vec.divide(scalar);
        return this;
    }

    public Quaternion conjugate() {
        vec.negate();
        return this;
    }

    public float normSquared() {
        return scalar * scalar + vec.lengthSquared();
    }

    public float norm() {
        return (float) Math.sqrt(normSquared());
    }

    public Quaternion invert() {
        return conjugate().divide(normSquared());
    }

    public Quaternion clone() {
        return new Quaternion(scalar, vec.clone());
    }

    public Vec3f rotateVector(Vec3f vector) {
        return (Vec3f) vector.clone()
                .multiply(scalar * scalar - vec.lengthSquared())
                .add(vec.clone().multiply(2.0f * vector.dot(vec)))
                .add(vec.cross(vector).multiply(2.0f * scalar));
    }

    public static Quaternion rotationQuaternion(float angle, Vec3f axis) {
        float t = angle * 0.5f;
        return new Quaternion((float) Math.cos(t), (Vec3f) axis.clone().normalize().multiply(Math.sin(t)));
    }

    public static Vec3f rotateVector(Vec3f vector, Vec3f axis, float angle) {
        return rotationQuaternion(angle, axis).rotateVector(vector);
    }

}














