package joel.opengl.shaders;

import joel.opengl.maths.Vec4f;
import joel.opengl.oldRendering.Camera2D;

public class SolidColour2DShader extends ShaderProgram {

    private static final String VERTEX_FILE = "vert2DTransform.txt";
    private static final String FRAGMENT_FILE = "fragSolidColour.txt";

    private int resolutionLocation, translationLocation, zoomLocation, colourLocation;

    private Camera2D camera;
    private Vec4f colour;

    public SolidColour2DShader(Camera2D camera, Vec4f colour) {
        super(VERTEX_FILE, FRAGMENT_FILE);

        this.camera = camera;
        this.colour = colour;

        bind();
        pushResolution();
        pushTranslation();
        pushZoom();
        pushColour();
        unbind();
    }

    public SolidColour2DShader(Camera2D camera, float red, float green, float blue, float alpha) {
        this(camera, new Vec4f(red, green, blue, alpha));
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
        colourLocation = super.getUniformLocation("colour");
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

    public void pushColour() {
        super.pushVector(colourLocation, colour.x(), colour.y(), colour.z(), colour.w());
    }

}
