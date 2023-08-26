package joel.opengl.shaders;

import joel.opengl.rendering.Camera3D;

public class ColouredCubeMeshShader extends ShaderProgram {

    private static final String VERTEX_FILE = "vertColourCube.glsl";
    private static final String FRAGMENT_FILE = "fragInColour.txt";

    private int worldToClipLocation;

    public Camera3D camera;

    public ColouredCubeMeshShader(Camera3D camera) {
        super(VERTEX_FILE, FRAGMENT_FILE);

        this.camera = camera;
        bind();
        pushMatrix();
        unbind();
    }

    public void pushMatrix() {
        pushMat4f(worldToClipLocation, camera.worldToClipMatrix);
    }

    @Override
    protected void getAllUniformLocations() {
        worldToClipLocation = super.getUniformLocation("worldToClip");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "vertIndex");
        super.bindAttribute(2, "modelToWorld");
        super.bindAttribute(6, "iColour");
    }
}
