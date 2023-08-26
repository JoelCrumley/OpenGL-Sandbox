package joel.opengl.rendering;

import joel.opengl.maths.*;

public class Camera2D extends Camera {

    private Vec2f position;
    private float rotation, aspectRatio; // aspect ratio = width / height
    private Vec2f scale, right, up;

    public Mat3f worldToClipMatrix;

    public Camera2D(Vec2f position, float rotation, Vec2f scale, float aspectRatio) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.aspectRatio = aspectRatio;
        calculateMatrix();
    }

    public Camera2D(float aspectRatio) {
        this(new Vec2f(0.0f), 0.0f, new Vec2f(1.0f), aspectRatio);
    }

    public Vec2f getPosition() {
        return position;
    }

    public Vec2f getUpDirection() {
        return up;
    }

    public Vec2f getRightDirection() {
        return right;
    }

    public Mat3f calculateMatrix() {
        Mat3f rot = Mat3f.rotationMatrix(rotation);
        right = new Vec2f(rot.get(0, 0), rot.get(1, 0));
        up = new Vec2f(rot.get(0, 1), rot.get(1, 1));
        worldToClipMatrix = Mat3f.diagonalMatrix(aspectRatio / scale.x(), 1.0f / scale.y(), 1.0f).multiply(rot.transpose()).multiply(Mat3f.translationMatrix((Vec2f) position.clone().negate()));
//        worldToClipMatrix = Mat4f.symmetricPerspectiveProjectionMatrix(rightClip, rightClip / aspectRatio, nearClip, farClip)
//                .multiply(new Mat4f(new float[] { // Inverse of rotation matrix (SO3) is it's
//                        right.x(), right.y(), right.z(), 0.0f,
//                        up.x(), up.y(), up.z(), 0.0f,
//                        backward.x(), backward.y(), backward.z(), 0.0f,
//                        0.0f, 0.0f, 0.0f, 1.0f
//                }))
//                .multiply(Mat4f.translationMatrix((Vec3f) position.clone().negate()));
        return worldToClipMatrix;
    }

    public Camera2D addRotation(float dRotation) {
        return setRotation(rotation + dRotation);
    }

    public Camera2D setRotation(float rotation) {
        this.rotation = rotation % 6.283185307179586476925286766559f;
        changed = true;
        return this;
    }

    public Camera2D setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        changed = true;
        return this;
    }

    public Camera2D setAspectRatio(int width, int height) {
        return setAspectRatio((float) width / (float) height);
    }

    public Camera2D moveUp(float distance) {
        position.add(up.clone().multiply(distance));
        changed = true;
        return this;
    }

    public Camera2D moveRight(float distance) {
        position.add(right.clone().multiply(distance));
        changed = true;
        return this;
    }

    public Camera2D moveBy(float dx, float dy) {
        position.add(dx, dy);
        changed = true;
        return this;
    }

    public Camera2D moveTo(float x, float y) {
        position.data[0] = x;
        position.data[1] = y;
        changed = true;
        return this;
    }

    public Camera2D moveTo(Vec2f position) {
        this.position = position.clone();
        changed = true;
        return this;
    }

}
