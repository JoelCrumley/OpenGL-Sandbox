package joel.opengl.rendering.text;

import joel.opengl.maths.Mat4f;
import joel.opengl.rendering.Camera3D;
import joel.opengl.shaders.ShaderProgram;

public class TextShader extends ShaderProgram {

    private static final String VERTEX_FILE = "vert3DTransformTex.glsl";
    private static final String FRAGMENT_FILE = "fragText.glsl";

    private int modelToWorldLocation, worldToClipLocation, textColorLocation;

    public Camera3D camera;

    public TextShader(Camera3D camera) {
        super(VERTEX_FILE, FRAGMENT_FILE);
        this.camera = camera;
        bind();
        pushMat4f(worldToClipLocation, camera.worldToClipMatrix);
        pushTextColor(1.0f, 1.0f, 1.0f, 1.0f);
        unbind();
    }

    public void pushWorldToClipMatrix() {
        pushMat4f(worldToClipLocation, camera.worldToClipMatrix);
    }

    public void pushModelToWorldMatrix(Mat4f matrix) {
        pushMat4f(modelToWorldLocation, matrix);
    }

    public void pushTextColor(float r, float g, float b, float a) {
        pushVector(textColorLocation, r, g, b, a);
    }

    @Override
    protected void getAllUniformLocations() {
        modelToWorldLocation = super.getUniformLocation("modelToWorld");
        worldToClipLocation = super.getUniformLocation("worldToClip");
        textColorLocation = super.getUniformLocation("textColor");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "iposition");
        super.bindAttribute(1, "itextureCoords");
    }

}
