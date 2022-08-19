package joel.opengl.newRendering;

import joel.opengl.maths.Mat4f;
import joel.opengl.maths.Vec3f;

public class Camera {

    public Vec3f position, rotation;
    public float nearClip, farClip, fov, aspectRatio;

    public boolean changed = true; // When this is true, calculateMatrix will be called and new matrix will be pushed to shader on next draw
    public Mat4f worldToClipMatrix;

    public Camera(Vec3f position, Vec3f rotation, float nearClip, float farClip, float fov, float aspectRatio) {
        this.position = position;
        this.rotation = rotation;
        this.nearClip = nearClip;
        this.farClip = farClip;
        this.fov = (float) Math.toRadians(fov);
        this.aspectRatio = aspectRatio;
        calculateMatrix();
    }

    public Camera(float nearClip, float farClip, float fov, float aspectRatio) {
        this(new Vec3f(0.0f), new Vec3f(0.0f), nearClip, farClip, fov, aspectRatio);
    }

    public Mat4f calculateMatrix() {
        float right = nearClip * (float) Math.tan(fov / 2.0);
        worldToClipMatrix = Mat4f.symmetricPerspectiveProjectionMatrix(right, right / aspectRatio, nearClip, farClip)
                .multiply(Mat4f.rotationMatrix((Vec3f) rotation.clone().negate()))
                .multiply(Mat4f.translationMatrix((Vec3f) position.clone().negate()));
        return worldToClipMatrix;
    }

}
