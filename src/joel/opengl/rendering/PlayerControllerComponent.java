package joel.opengl.rendering;

import joel.opengl.entity.components.TransformComponent;
import joel.opengl.maths.Quaternion;
import joel.opengl.maths.Vec3f;

public class PlayerControllerComponent extends CameraControllerComponent {

    private static final Vec3f up = new Vec3f(0.0f, 1.0f, 0.0f);
    private Vec3f forward, right;

    public PlayerControllerComponent(TransformComponent transform, Camera3D camera) {
        super(transform, camera);
        calculateAxis();
    }

    public CameraControllerComponent calculateAxis() {
        Quaternion rot = Quaternion.rotationQuaternion(yaw, up);
        right = rot.rotateVector(new Vec3f(1.0f, 0.0f, 0.0f));
        forward = rot.rotateVector(new Vec3f(0.0f, 0.0f, -1.0f));
        return this;
    }

    public PlayerControllerComponent moveRelative(float distForward, float distUp, float distRight) {
        transform.getTranslation().add(forward.clone().multiply(distForward)).add(up.clone().multiply(distUp)).add(right.clone().multiply(distRight));
        return this;
    }

    public PlayerControllerComponent moveForward(float distance) {
        transform.moveBy((Vec3f) forward.clone().multiply(distance));
        return this;
    }

    public PlayerControllerComponent moveRight(float distance) {
        transform.moveBy((Vec3f) right.clone().multiply(distance));
        return this;
    }

    @Override
    public PlayerControllerComponent setYaw(float newYaw) {
        this.yaw = newYaw % 6.283185307179586476925286766559f;
        camera.setYaw(yaw);
        transform.setRotation(Quaternion.rotationQuaternion(yaw, new Vec3f(0.0f, 1.0f, 0.0f)));
        calculateAxis();
        forceUpdateCamera();
        return this;
    }

    public static Vec3f getUpDirection() {
        return up;
    }

    public Vec3f getForwardDirection() {
        return forward;
    }

    public Vec3f getRightDirection() {
        return right;
    }

}
