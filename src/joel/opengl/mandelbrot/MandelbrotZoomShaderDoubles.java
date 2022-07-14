package joel.opengl.mandelbrot;

import joel.opengl.shaders.ShaderProgram;

public class MandelbrotZoomShaderDoubles extends MandelbrotShaderAbstract {

//    private static final String VERTEX_FILE = "src/res/shaders/vert2DPassthrough.txt";
//    private static final String FRAGMENT_FILE = "src/res/shaders/fragMandelbrotDoubles.txt";
    private static final String VERTEX_FILE = "vert2DPassthrough.txt";
    private static final String FRAGMENT_FILE = "fragMandelbrotDoubles.txt";

    private static final double translationSpeed = 0.01f, zoomSpeed = 0.98f;
    private static final float limitSpeed = 1.01f, iterationsSpeed = 1.01f;

    private int resolutionLocation, translationLocation, zoomLocation, limitLocation, iterationsLocation, colourModeLocation, showCrosshairLocation;

    private int width, height, colourMode;
    private double x, y, zoom;
    private float iterations, limit;
    private boolean showCrosshair;

    public MandelbrotZoomShaderDoubles(int width, int height, double x, double y, double zoom, float limit, int iterations, int colourMode, boolean showCrosshair) {
        super(VERTEX_FILE, FRAGMENT_FILE);

        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.zoom = zoom;
        this.limit = limit;
        this.iterations = (float) iterations;
        this.colourMode = colourMode;
        this.showCrosshair = showCrosshair;

        bind();
        pushResolution();
        pushTranslation();
        pushZoom();
        pushLimit();
        pushIterations();
        pushColourMode();
        pushCrosshair();
        unbind();
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        resolutionLocation = super.getUniformLocation("u_resolution");
        translationLocation = super.getUniformLocation("translation");
        zoomLocation = super.getUniformLocation("zoom");
        limitLocation = super.getUniformLocation("limit");
        iterationsLocation = super.getUniformLocation("iterations");
        colourModeLocation = super.getUniformLocation("colourMode");
        showCrosshairLocation = super.getUniformLocation("showCrosshair");
    }

//    private boolean up, down, left, right, in, out, limitUp, limitDown;
    public void update(boolean up, boolean down, boolean left, boolean right, boolean in, boolean out, boolean limitUp, boolean limitDown, boolean iterUp, boolean iterDown, boolean updateColour, int newColour, boolean toggleCrosshair) {
        if (up || down || left || right || in || out || limitUp || limitDown || iterUp || iterDown || updateColour || toggleCrosshair) { // Possibly save us from doing an unnecessary shader program binding
            bind();

            if (up) y += translationSpeed * zoom;
            if (down) y -= translationSpeed * zoom;
            if (left) x -= translationSpeed * zoom;
            if (right) x += translationSpeed * zoom;
            if (in) zoom *= zoomSpeed;
            if (out) zoom /= zoomSpeed;
            if (limitUp) limit *= limitSpeed;
            if (limitDown) limit /= limitSpeed;
            if (iterUp) iterations *= iterationsSpeed;
            if (iterDown) iterations /= iterationsSpeed;
            if (limit < 0.0f) limit = 0.0f;
            if (iterations < 1.0f) iterations = 1.0f;

            if (up || down || left || right) pushTranslation();
            if (in || out) pushZoom();
            if (limitUp || limitDown) pushLimit();
            if (iterUp || iterDown) pushIterations();
            if (updateColour) {
                this.colourMode = newColour;
                pushColourMode();
            }
            if (toggleCrosshair) {
                showCrosshair = !showCrosshair;
                pushCrosshair();
            }
            unbind();
        }
    }

    public void pushResolution() {
        super.pushVectord(resolutionLocation, (double) width,  (double) height);
    }

    public void pushTranslation() {
        System.out.println("Pushing translation (" + x + ", " + y + ")");
        super.pushVectord(translationLocation, x ,y);
    }

    public void pushZoom() {
        System.out.println("Pushing zoom (" + zoom + ")");
        super.pushDouble(zoomLocation, zoom);
    }

    public void pushLimit() {
        System.out.println("Pushing limit (" + limit + ")");
        super.pushFloat(limitLocation, limit);
    }

    public void pushIterations() {
        int it = (int) iterations;
        System.out.println("Pushing iterations (" + it + ")");
        super.pushInt(iterationsLocation, it);
    }

    public void pushColourMode() {
        System.out.println("Pushing colour mode (" + colourMode + ")");
        super.pushInt(colourModeLocation, colourMode);
    }

    public void pushCrosshair() {
        System.out.println("Toggling crosshair (" + showCrosshair + ")");
        super.pushBoolean(showCrosshairLocation, showCrosshair);
    }

}
