package joel.opengl.shaders;

import joel.opengl.maths.Vec2f;
import joel.opengl.rendering.Camera2D;

import static org.lwjgl.opengl.GL11.glViewport;

public class BasicGridShader extends ShaderProgram {

    private static final String VERTEX_FILE = "vert2DPassthrough.txt";
    private static final String FRAGMENT_FILE = "fragBasicGrid.txt";

    private int resolutionLocation, translationLocation, zoomLocation, pixelScaleLocation;

    private Camera2D camera;

    public BasicGridShader(Camera2D camera) {
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
        pixelScaleLocation = super.getUniformLocation("pixelScale");
    }

    public void update() {
        // Possibly save us from doing an unnecessary shader program binding
        if (camera.translationChanged || camera.zoomChanged || camera.resolutionChanged) {
            bind();
            if (camera.translationChanged) pushTranslation();
            if (camera.zoomChanged) pushZoom();
            if (camera.resolutionChanged) pushResolution();
            if (camera.zoomChanged || camera.resolutionChanged) pushPixelScale();
            unbind();
        }
    }

    public void pushResolution() {
//        System.out.println("Pushing resolution (" + camera.width + ", " + camera.height + ")");
        super.pushVector(resolutionLocation, camera.width, camera.height);
    }

    public void pushTranslation() {
//        System.out.println("Pushing translation (" + camera.x + ", " + camera.y + ")");
        super.pushVector(translationLocation, (float) camera.x , (float) camera.y);
    }

    public void pushZoom() {
//        System.out.println("Pushing zoom (" + camera.zoom + ")");
        super.pushFloat(zoomLocation, (float) camera.zoom);
    }

    public void pushPixelScale() {
//        System.out.println("Pushing pixel scale (" + camera.pixelScale + ")");
        super.pushFloat(pixelScaleLocation, (float) camera.pixelScale);
    }

}
