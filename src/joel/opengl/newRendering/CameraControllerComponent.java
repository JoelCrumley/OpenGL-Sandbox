package joel.opengl.newRendering;

import joel.opengl.entity.Component;
import joel.opengl.entity.EntityHandler;
import joel.opengl.entity.components.TransformComponent;
import joel.opengl.maths.Quaternion;
import joel.opengl.maths.Vec3f;

public class CameraControllerComponent extends Component {

    protected TransformComponent transform;
    protected Camera camera;

    protected float yaw = 0.0f; // Pitch/Roll have no affect on transform, only on camera so no need to store them here.

    protected float zoom = 3.0f;

    protected Vec3f eyeOffset = new Vec3f(0.0f, 0.5f, 0.0f);

    public CameraControllerComponent(TransformComponent transform, Camera camera) {
        this.transform = transform;
        this.camera = camera;
    }

    public CameraControllerComponent updateCamera() { // To be called every frame.
        if (transform.hasChanged()) forceUpdateCamera();
        return this;
    }

    public CameraControllerComponent forceUpdateCamera() {
        Quaternion yRot = Quaternion.rotationQuaternion(yaw, new Vec3f(0.0f, 1.0f, 0.0f));
        Vec3f right = yRot.rotateVector(new Vec3f(1.0f, 0.0f, 0.0f));
        Vec3f forward = Quaternion.rotationQuaternion(camera.getPitch(), right).rotateVector(yRot.rotateVector(new Vec3f(0.0f, 0.0f, -1.0f)));
        camera.moveTo((Vec3f) transform.getTranslation().clone().subtract(forward.multiply(zoom)).add(eyeOffset));
        return this;
    }

    public Vec3f getEyeOffset() {
        return eyeOffset;
    }

    public CameraControllerComponent setEyeOffset(Vec3f eyeOffset) {
        this.eyeOffset = eyeOffset;
        forceUpdateCamera();
        return this;
    }

    public CameraControllerComponent addZoom(float dZoom) {
        return setZoom(zoom + dZoom);
    }

    public CameraControllerComponent setZoom(float zoom) {
        this.zoom = zoom < 0.0f ? 0.0f : zoom;
        forceUpdateCamera();
        return this;
    }

    public float getZoom() {
        return zoom;
    }

    public CameraControllerComponent addYaw(float dYaw) {
        return setYaw(yaw + dYaw);
    }

    public CameraControllerComponent setYaw(float yaw) {
        this.yaw = yaw;
        camera.setYaw(yaw);
        forceUpdateCamera();
        return this;
    }

    public CameraControllerComponent addPitch(float dPitch) {
        camera.addPitch(dPitch);
        forceUpdateCamera();
        return this;
    }

    public CameraControllerComponent setPitch(float pitch) {
        camera.setPitch(pitch);
        forceUpdateCamera();
        return this;
    }

    public TransformComponent getTransform() {
        return transform;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void onComponentAdded(EntityHandler entityHandler, int entity) {

    }

    @Override
    public void onComponentRemoved(EntityHandler entityHandler, int entity) {

    }
}
