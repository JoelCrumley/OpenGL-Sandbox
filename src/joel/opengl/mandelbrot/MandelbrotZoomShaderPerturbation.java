package joel.opengl.mandelbrot;

import joel.opengl.oldRendering.Loader;
import joel.opengl.shaders.ShaderProgram;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class MandelbrotZoomShaderPerturbation extends ShaderProgram {

//    private static final String VERTEX_FILE = "src/res/shaders/vert2DPassthrough.txt";
//    private static final String FRAGMENT_FILE = "src/res/shaders/fragMandelbrotDoubles.txt";
    private static final String VERTEX_FILE = "vert2DPassthrough.txt";
    private static final String FRAGMENT_FILE = "fragMandelbrot.txt";

    private static final float limitSpeed = 1.01f, iterationsSpeed = 1.01f;
    private static final double translationSpeed = 0.01f, zoomSpeed = 0.98f;

    private int resolutionLocation, translationLocation, zoomLocation, limitLocation, iterationsLocation, colourModeLocation, showCrosshairLocation, pointsBufferLocation;

    private int width, height, colourMode;
    private double x, y, zoom;
    private float limit;
    private float iterations;
    private boolean showCrosshair;

    public MandelbrotZoomShaderPerturbation(int width, int height, double x, double y, double zoom, float limit, int iterations, int colourMode, boolean showCrosshair) {
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


        /*
        https://stackoverflow.com/questions/50516439/sending-uvec4-array-in-ssbo
        https://stackoverflow.com/questions/32035423/trivial-opengl-shader-storage-buffer-object-ssbo-not-working
        https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glBufferData.xhtml
        https://stackoverflow.com/questions/25055580/bind-an-ssbo-to-a-fragment-shader
        https://www.google.com/search?q=opengl+ssbo+fragment+shader+example&rlz=1C1CHBF_en-GBGB919GB919&oq=opengl+ssbo+fragment+shader+example&aqs=chrome..69i57.6936j0j4&sourceid=chrome&ie=UTF-8
        https://www.khronos.org/opengl/wiki/Shader_Storage_Buffer_Object
        https://computergraphics.stackexchange.com/questions/5323/dynamic-array-in-glsl

        NEED TO FIGURE OUT HOW JAVA / OPENGL WORK TOGETHER. IF I PASS A FLOAT BUFFER TO SUPPLY THE DATA FOR A VEC2 ARRAY, DOES OPENGL KNOW HOW TO USE THIS DATA?
        VEC[0] = VEC2(DATA[0], DATA[1]), VEC[1] = VEC2(DATA[2], DATA[3]), ETC.
        IF ALL ELSE FAILS JUST USE FLOAT ARRAY IN CODE AND MAKE THE VECTORS YOURSELF IN ITERATION

        https://en.wikipedia.org/wiki/Perturbation_theory
        https://en.wikipedia.org/wiki/Plotting_algorithms_for_the_Mandelbrot_set#Perturbation_theory_and_series_approximation
         */

        float[] data = null;
        FloatBuffer buffer = Loader.arrayToBuffer(data);
        pointsBufferLocation = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, pointsBufferLocation);
        //glBufferData(GL_SHADER_STORAGE_BUFFER, sizeof(vec4) * window.getNumberOfPixels, new vec4[window.getNumberOfPixels], GL_DYNAMIC_DRAW);
        glBufferData(GL_SHADER_STORAGE_BUFFER, buffer, GL_DYNAMIC_DRAW);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, pointsBufferLocation);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0); // Unbind

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
        super.pushVector(resolutionLocation, width, height);
    }

    public void pushTranslation() {
        System.out.println("Pushing translation (" + x + ", " + y + ")");
        super.pushVector(translationLocation, (float) x , (float) y);
    }

    public void pushZoom() {
        System.out.println("Pushing zoom (" + zoom + ")");
        super.pushFloat(zoomLocation, (float) zoom);
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
