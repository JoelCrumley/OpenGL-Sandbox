package joel.opengl.rendering.text;

import joel.opengl.maths.Mat4f;
import joel.opengl.rendering.Camera;
import joel.opengl.shaders.ShaderProgram;

public class TextShader extends ShaderProgram {

    private static final String VERTEX_FILE = "vert3DTransformTex.glsl";
    private static final String FRAGMENT_FILE = "fragText.glsl";

    private int modelToWorldLocation, worldToClipLocation;

    public Camera camera;

    public TextShader(Camera camera) {
        super(VERTEX_FILE, FRAGMENT_FILE);
        this.camera = camera;
        bind();
        pushMat4f(worldToClipLocation, camera.worldToClipMatrix);
        unbind();
    }

    public void pushWorldToClipMatrix() {
        pushMat4f(worldToClipLocation, camera.worldToClipMatrix);
    }

    public void pushModelToWorldMatrix(Mat4f matrix) {
        pushMat4f(modelToWorldLocation, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        modelToWorldLocation = super.getUniformLocation("modelToWorld");
        worldToClipLocation = super.getUniformLocation("worldToClip");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "iposition");
        super.bindAttribute(1, "itextureCoords");
    }

}
