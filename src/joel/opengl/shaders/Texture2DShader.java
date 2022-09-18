package joel.opengl.shaders;

import joel.opengl.oldRendering.Camera2D;

public class Texture2DShader extends ShaderProgram {

    private static final String VERTEX_FILE = "vert2DTransformTex.txt";
    private static final String FRAGMENT_FILE = "fragTex2D.txt";

    private int resolutionLocation, translationLocation, zoomLocation;

    private Camera2D camera;

    public Texture2DShader(Camera2D camera) {
        super(VERTEX_FILE, FRAGMENT_FILE);

        this.camera = camera;

        bind();
        pushResolution();
        pushTranslation();
        pushZoom();
        unbind();
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        resolutionLocation = super.getUniformLocation("resolution");
        translationLocation = super.getUniformLocation("translation");
        zoomLocation = super.getUniformLocation("zoom");
    }

    public void update() {
        // Possibly save us from doing an unnecessary shader program binding
        if (camera.translationChanged || camera.zoomChanged || camera.resolutionChanged) {
            bind();
            if (camera.translationChanged) pushTranslation();
            if (camera.zoomChanged) pushZoom();
            if (camera.resolutionChanged) pushResolution();
            unbind();
        }
    }

    public void pushResolution() {
        super.pushVector(resolutionLocation, camera.width, camera.height);
    }

    public void pushTranslation() {
        super.pushVector(translationLocation, (float) camera.x , (float) camera.y);
    }

    public void pushZoom() {
        super.pushFloat(zoomLocation, (float) camera.zoom);
    }

}
