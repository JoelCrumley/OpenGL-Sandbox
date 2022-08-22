package joel.opengl.entity.components;

import joel.opengl.entity.Component;
import joel.opengl.entity.EntityHandler;
import joel.opengl.maths.Mat4f;
import joel.opengl.maths.Quaternion;
import joel.opengl.maths.Vec3f;

public class TransformComponent extends Component {

    private Vec3f translation, scale;
    private Quaternion rotation;
    private boolean changed = true;

    public TransformComponent(float dx, float dy, float dz, Quaternion rotation, float sx, float sy, float sz) {
        this(new Vec3f(dx, dy, dz), rotation, new Vec3f(sx, sy, sz));
    }

    public TransformComponent(Vec3f translation, Quaternion rotation, Vec3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vec3f getTranslation() {
        return translation;
    }

    public Vec3f getScale() {
        return scale;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public TransformComponent rotate(float angle, Vec3f axis) {
        rotation.rotate(angle, axis);
        changed = true;
        return this;
    }

    public TransformComponent setRotation(Quaternion rotation) {
        this.rotation = rotation;
        changed = true;
        return this;
    }

    public TransformComponent setScale(float sx, float sy, float sz) {
        scale.data[0] = sx;
        scale.data[1] = sy;
        scale.data[2] = sz;
        changed = true;
        return this;
    }

    public TransformComponent scaleBy(float dx, float dy, float dz) {
        scale.add(dx, dy, dz);
        changed = true;
        return this;

    }

    public TransformComponent moveBy(Vec3f vec) {
        return moveBy(vec.x(), vec.y(), vec.z());
    }

    public TransformComponent moveBy(float dx, float dy, float dz) {
        translation.add(dx, dy, dz);
        changed = true;
        return this;
    }

    public TransformComponent moveTo(float x, float y, float z) {
        translation.data[0] = x;
        translation.data[1] = y;
        translation.data[2] = z;
        changed = true;
        return this;
    }

    public boolean hasChanged() {
        return changed;
    }

    public TransformComponent setHasNotChanged() {
        changed = false;
        return this;
    }

    public Mat4f getModelToWorldMatrix() {
        return Mat4f.modelToWorldMatrix(translation, rotation, scale);
    }

    @Override
    public void onComponentAdded(EntityHandler entityHandler, int entity) {

    }

    @Override
    public void onComponentRemoved(EntityHandler entityHandler, int entity) {

    }
}
