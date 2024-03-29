package joel.opengl.rendering;

import joel.opengl.maths.Mat4f;
import joel.opengl.maths.Maths;
import joel.opengl.maths.Quaternion;
import joel.opengl.maths.Vec3f;

public class Camera3D extends Camera {

    private Vec3f position;
    private float nearClip, farClip, fov, aspectRatio;

    private Vec3f backward, right, up;
    private float yaw, pitch, roll;

    public Mat4f worldToClipMatrix;

    public Camera3D(Vec3f position, float yaw, float pitch, float roll, float nearClip, float farClip, float fov, float aspectRatio) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        this.nearClip = nearClip;
        this.farClip = farClip;
        this.fov = (float) Math.toRadians(fov);
        this.aspectRatio = aspectRatio;
        calculateMatrix();
    }

    public Vec3f getPosition() {
        return position;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }

    public Vec3f getUpDirection() {
        return up;
    }

    public Vec3f getRightDirection() {
        return right;
    }

    public Vec3f getBackwardDirection() {
        return backward;
    }

    public float getNearClip() {
        return nearClip;
    }

    public float getFarClip() {
        return farClip;
    }

    public float getFov() {
        return fov;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public boolean hasChanged() {
        return changed;
    }

    public void setNotChanged() {
        changed = false;
    }

    public void calculateAxis() {
        right = new Vec3f(1.0f, 0.0f, 0.0f);
        up = new Vec3f(0.0f, 1.0f, 0.0f);
        backward = new Vec3f(0.0f, 0.0f, 1.0f);
        Quaternion yawRotate = Quaternion.rotationQuaternion(yaw, up);
        right = yawRotate.rotateVector(right);
        backward = yawRotate.rotateVector(backward);
        Quaternion pitchRotate = Quaternion.rotationQuaternion(pitch, right);
        up = pitchRotate.rotateVector(up);
        backward = pitchRotate.rotateVector(backward);
        Quaternion rollRotate = Quaternion.rotationQuaternion(roll, backward);
        right = rollRotate.rotateVector(right);
        up = rollRotate.rotateVector(up);
    }

    public Mat4f calculateMatrix() {
        calculateAxis();
        float rightClip = nearClip * (float) Math.tan(fov / 2.0);
        worldToClipMatrix = Mat4f.symmetricPerspectiveProjectionMatrix(rightClip, rightClip / aspectRatio, nearClip, farClip)
                .multiply(new Mat4f(new float[] { // Inverse of rotation matrix (SO3) is it's
                        right.x(), right.y(), right.z(), 0.0f,
                        up.x(), up.y(), up.z(), 0.0f,
                        backward.x(), backward.y(), backward.z(), 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f
                }))
                .multiply(Mat4f.translationMatrix((Vec3f) position.clone().negate()));
        return worldToClipMatrix;
    }

    public Camera3D addYaw(float dYaw) {
        return setYaw(yaw + dYaw);
    }

    public Camera3D addPitch(float dPitch) {
        return setPitch(pitch + dPitch);
    }

    public Camera3D addRoll(float dRoll) {
        return setRoll(roll + dRoll);
    }

    public Camera3D setYaw(float yaw) {
        this.yaw = yaw % 6.283185307179586476925286766559f;
        changed = true;
        return this;
    }

    public Camera3D setPitch(float pitch) {
        this.pitch = Maths.clamp(pitch, -1.5707963267948966192313216916398f, +1.5707963267948966192313216916398f);
        changed = true;
        return this;
    }

    public Camera3D setRoll(float roll) {
        this.roll = roll % 6.283185307179586476925286766559f;
        changed = true;
        return this;
    }

    public Camera3D setNearClip(float near) {
        this.nearClip = near;
        changed = true;
        return this;
    }

    public Camera3D setFarClip(float far) {
        this.farClip = far;
        changed = true;
        return this;
    }

    public Camera3D setFOV(float fov) {
        this.fov = fov;
        changed = true;
        return this;
    }

    public Camera3D setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        changed = true;
        return this;
    }

    public Camera3D moveForward(float distance) {
        position.add(backward.clone().multiply(-distance));
        changed = true;
        return this;
    }

    public Camera3D moveUp(float distance) {
        position.add(up.clone().multiply(distance));
        changed = true;
        return this;
    }

    public Camera3D moveRight(float distance) {
        position.add(right.clone().multiply(distance));
        changed = true;
        return this;
    }

    public Camera3D setAspectRatio(int width, int height) {
        this.aspectRatio = (float) width / (float) height;
        changed = true;
        return this;
    }

    public Camera3D moveBy(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
        changed = true;
        return this;
    }

    public Camera3D moveTo(float x, float y, float z) {
        position.data[0] = x;
        position.data[1] = y;
        position.data[2] = z;
        changed = true;
        return this;
    }

    public Camera3D moveTo(Vec3f position) {
        this.position = position.clone();
        changed = true;
        return this;
    }

    public Camera3D(float nearClip, float farClip, float fov, float aspectRatio) {
        this(new Vec3f(0.0f), 0.0f, 0.0f, 0.0f, nearClip, farClip, fov, aspectRatio);
    }

}
