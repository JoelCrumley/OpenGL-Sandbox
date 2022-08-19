package joel.opengl.entity.components;

import joel.opengl.entity.Component;
import joel.opengl.entity.EntityHandler;
import joel.opengl.maths.Mat4f;
import joel.opengl.maths.Vec3f;

public class TransformComponent extends Component {

    public Vec3f translation, rotation, scale;
    public boolean changed = true;

    public TransformComponent(float dx, float dy, float dz, float rx, float ry, float rz, float sx, float sy, float sz) {
        this(new Vec3f(dx, dy, dz), new Vec3f(rx, ry, rz), new Vec3f(sx, sy, sz));
    }

    public TransformComponent(Vec3f translation, Vec3f rotation, Vec3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
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
