package joel.opengl.test;

import joel.opengl.maths.LineSegment2D;
import joel.opengl.maths.Vec2f;
import joel.opengl.rendering.*;
import joel.opengl.shaders.BasicGridShader;
import joel.opengl.shaders.SolidColour2DShader;
import joel.opengl.shaders.Texture2DShader;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class BasicGrid {

    // The window handle
    private long window;

    private int WIDTH = 1280, HEIGHT = 720;
    private final double UPS = 60.0d;

    private boolean up, down, left, right, in, out, reset, clearPoints, connectPoints;
    private Camera2D camera;
    private BasicGridShader gridShader;
    private SolidColour2DShader lineShader, randomShader;
    private Texture2DShader textureShader;
    private ArrayList<LineObject2D> lines = new ArrayList<>();
    private ArrayList<Quad2D> quads = new ArrayList<>();
    private ArrayList<TexturedQuad2D> texturedQuads = new ArrayList<>();
    private ArrayList<Vec2f> clickedPoints = new ArrayList<>();
    private Renderer renderer;
    private FullscreenQuad2D screen;

    protected BasicGrid() {

    }
    public static void main(String[] args) {
        new BasicGrid().run();
    }

    public void run() {
        System.out.println("LWJGL " + Version.getVersion() + "!");

        initWindow();
        initOpenGL();
        init();
        loop();
        cleanUp();
        closeOpenGL();
    }

    private void initWindow() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
//        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                WIDTH = width;
                HEIGHT = height;
                if (camera != null) {
                    camera.width = width;
                    camera.height = height;
                    camera.resolutionChanged = true;
                }
            }
        });

        glfwSetScrollCallback(window, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {

                /*
                Scroll Up: (0.0, +1.0)
                Scroll Down: (0.0, -1.0)
                Scroll Left: (1.0, 0.0)
                Scroll Right: (-1.0, 0.0)
                 */

                if (camera == null) return;
                if (yoffset > 0.1) camera.doZoomIn();
                if (yoffset < -0.1) camera.doZoomOut();

            }
        });

        glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {

                if (gridShader == null) return;

                try ( MemoryStack stack = stackPush() ) {
                    DoubleBuffer posX = stack.mallocDouble(1); // int*
                    DoubleBuffer posY = stack.mallocDouble(1); // int*
                    glfwGetCursorPos(window, posX, posY);

                    boolean press = action == GLFW_PRESS;

                    // Cursor position xy has (0, 0) at top left.
                    // glFragCoord uv has (0, 0) at bottom left.
                    // x E [0, WIDTH], y E [0, HEIGHT]
                    // u,v E [0, 1]

                    /*
                    Button IDs:
                    0 - Left Click
                    1 - Right Click
                    2 - Middle Mouse
                    3 - Back
                    4 - Forward
                    */

                    if (button == GLFW_MOUSE_BUTTON_1 && !press) {
                        Vec2f clickedScreen = new Vec2f(posX.get(), HEIGHT - posY.get());
                        Vec2f clickedGrid = camera.getGridCoordinate(clickedScreen);
                        clickedPoints.add(clickedGrid);
                        System.out.println("Clicked screen position " + clickedScreen + " which is grid position " + clickedGrid + ". " + clickedPoints.size() + " points are saved.");
                    } else if (button == GLFW_MOUSE_BUTTON_2 && !press) {
                        Vec2f clickedScreen = new Vec2f(posX.get(), HEIGHT - posY.get());
                        Vec2f clickedGrid = camera.getGridCoordinate(clickedScreen);
                        System.out.println("Moving to grid position " + clickedGrid);
                        camera.setTranslation(clickedGrid.x(), clickedGrid.y());
                    }

                    //System.out.println("MOUSE EVENT: " + (press ? "Pressed" : "Released") + " Button " + button + " at x:" + posX.get() + " y:" + posY.get());

                }
            }
        });

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {

            if (action == GLFW_RELEASE) {
                if (key == GLFW_KEY_ESCAPE) glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }

            boolean press = (action == GLFW_PRESS || action == GLFW_REPEAT);
            switch(key) {
                case GLFW_KEY_W:
                    up = press;
                    break;
                case GLFW_KEY_A:
                    left = press;
                    break;
                case GLFW_KEY_S:
                    down = press;
                    break;
                case GLFW_KEY_D:
                    right = press;
                    break;
                case GLFW_KEY_Q:
                    out = press;
                    break;
                case GLFW_KEY_E:
                    in = press;
                    break;
                case GLFW_KEY_SPACE:
                    reset = press;
                    break;
                case GLFW_KEY_ENTER:
                    connectPoints = press;
                    break;
                case GLFW_KEY_BACKSPACE:
                    clearPoints = press;
                    break;
                default:
            }

        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void initOpenGL() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Set up alpha blending
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        System.out.println("Vendor: " + glGetString(GL_VENDOR) + "\nRenderer: " + glGetString(GL_RENDERER) + "\nVersion: " + glGetString(GL_VERSION));
    }

    private void init() {

        camera = new Camera2D(WIDTH, HEIGHT, 0.0f, 0.0f, 1.0f);
        gridShader = new BasicGridShader(camera);
        lineShader = new SolidColour2DShader(camera, 0.0f, 0.0f, 1.0f, 1.0f);
        randomShader = new SolidColour2DShader(camera, 1.0f, 0.0f, 0.0f, 0.5f);
        textureShader = new Texture2DShader(camera);

        renderer = new Renderer();
        screen = new FullscreenQuad2D();
        quads.add(new Quad2D(new Vec2f(1.0f, 2.0f), new Vec2f(3.0f, 10.0f)));
        quads.add(new Quad2D(new Vec2f(1.0f, 2.0f), new Vec2f(10.0f, 3.0f)));

        Texture texture = Loader.loadTexture("images/MandelbrotShowcase.png");
        texturedQuads.add(new TexturedQuad2D(new Vec2f(1.0f, -3.0f), new Vec2f(2.0f, texture.getScaledHeight(2.0f)), texture));

        {
            float[] randomPoints = new float[] {
                    4.256562f, -1.8872454f,
                    2.6236782f, -3.931467f,
                    4.350048f, -5.3025913f,
                    5.827123f, -2.9467506f
            };
            texturedQuads.add(new TexturedQuad2D(randomPoints, texture));

            randomPoints = new float[] {
                    7.251812f, -2.4983277f,
                    5.2255893f, -4.382586f,
                    11.291351f, -5.8215914f,
                    10.12337f, -3.395286f
            };
            texturedQuads.add(new TexturedQuad2D(randomPoints, texture));
        }

        { // Draws many straight line segments to give illusion of a curve
            ArrayList<LineSegment2D> segments = new ArrayList<>();
            float size = 1.0f;
            int numberLines = 100;
            float spacing = (2.0f * size) / (float) numberLines;
            for (int i = 0; i < numberLines; i++) {
                float dist = spacing * (float) i;
                segments.add(new LineSegment2D(-size, -size + dist, size - dist, -size)); // bottom left
                segments.add(new LineSegment2D(size, -size + dist, size - dist, size)); // top right
                segments.add(new LineSegment2D(-size, -size + dist, -size + dist, +size)); // top left
                segments.add(new LineSegment2D(+size, -size + dist, -size + dist, -size)); // bottom right
            }
//
            lines.add(new LineObject2D(segments.toArray(new LineSegment2D[0])));
        }

        { // Draws a very bad snowflake
            ArrayList<LineSegment2D> segments = new ArrayList<>();
            Vec2f center = new Vec2f(0.0f, 5.0f);
            float radius = 2.0f;
            int numberLines = 32;
            for (int i = 0; i < numberLines; i++) {
                float t = 2.0f * 3.1415926535f * (float) i / (float) numberLines;
                segments.add(new LineSegment2D(center, new Vec2f(center.x() + radius * (float) Math.cos(t), center.y() + radius * Math.sin(t))));
            }
            lines.add(new LineObject2D(segments.toArray(new LineSegment2D[0])));

            // Draws circles inside the snowflake
            int numberCircles = 16;
            float r = radius;
            for (int i = 0; i < numberCircles; i++) {
                drawCircleFromLines(center, r, numberLines+1);
                r /= 2.0f;
            }

        }

        { // Plots function of 1 variable
            float startX = 2.0f, endX = 8.0f;
            int numberPoints = 100000;
            Vec2f[] points = new Vec2f[numberPoints];
            float xSpacing = (endX - startX) / (float) (numberPoints - 1);
            for (int i = 0; i < numberPoints; i++) {
                float x = xSpacing * (float) i;
                float t = (float) i / (float) (numberPoints - 1);
                points[i] = new Vec2f(startX + x, functionToPlot(t));
            }
            lines.add(new LineObject2D(false, points));
        }

        { // Smooth circle
            drawCircleFromLines(new Vec2f(-4.5f, -2.5f), 2.0f, 100000);
        }


//        for (LineSegment2D segment : segments) lines.add(new LineObject2D(segment)); // VERY BAD METHOD, CREATES A FUCK TON OF DRAW CALLS


//        lines.add(
//                new LineObject2D(
//                        new LineSegment2D(-2.0f, -2.0f, 2.0f, -2.0f),
//                        new LineSegment2D(-2.0f, -1.0f, 2.0f, -1.0f),
//                        new LineSegment2D(-2.0f, 0.0f, 2.0f, 0.0f),
//                        new LineSegment2D(-2.0f, 1.0f, 2.0f, 1.0f),
//                        new LineSegment2D(-2.0f, 2.0f, 2.0f, 2.0f),
//
//                        new LineSegment2D(-2.0f, -2.0f, -2.0f, 2.0f),
//                        new LineSegment2D(-1.0f, -2.0f, -1.0f, 2.0f),
//                        new LineSegment2D(0.0f, -2.0f, 0.0f, 2.0f),
//                        new LineSegment2D(1.0f, -2.0f, 1.0f, 2.0f),
//                        new LineSegment2D(2.0f, -2.0f, 2.0f, 2.0f)
//
//                        )
//        );

//        lines.add(
//                new LineObject2D(
//                        new LineSegment2D(-1.5f, -0.5f, 1.5f, -0.5f)
//                )
//        );

    }

    private void drawCircleFromLines(Vec2f center, float radius, int numberPoints) {
            Vec2f[] points = new Vec2f[numberPoints];
            for (int i = 0; i < numberPoints; i++) {
                float t = 2.0f * 3.1415926535f * (float) i / (float) (numberPoints - 1);
                points[i] = new Vec2f(center.x() + radius * Math.cos(t), center.y() + radius * Math.sin(t));
            }
            lines.add(new LineObject2D(true, points));
    }

    private float functionToPlot(float t) { // Input t, return f(t). For consistency, t should be in [0, 1]
        return (float) (
                1.0 * Math.sin(2 * 3.1415926535 * t) +
                0.1 * Math.sin(100 * 3.1415926535 * t));
    }

    private void loop() {
        double lastSecond = glfwGetTime(), lastUpdate = glfwGetTime();
        final double updateInterval = 1.0d / UPS;
        int frameCount = 0, updateCount = 0;



        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            gridShader.bind();
            renderer.render(screen, camera);
            gridShader.unbind();

            lineShader.bind();
            for (LineObject2D line : lines) renderer.render(line, camera);
            lineShader.unbind();

            textureShader.bind();
            for (TexturedQuad2D quad : texturedQuads) renderer.render(quad, camera);
            textureShader.unbind();

            randomShader.bind(); // For testing alpha blending
            for (Quad2D quad : quads) renderer.render(quad, camera);
            randomShader.unbind();

            glfwSwapBuffers(window); // swap the color buffers

            // FPS Counter in title.
            frameCount++;
            double currentTime = glfwGetTime();
            double delta = currentTime - lastUpdate;

            if (delta > updateInterval) {

                // Do an update
                camera.update(up, down, left, right, in, out, reset);
                gridShader.update();
                lineShader.update();
                randomShader.update();
                textureShader.update();

                // Should be at the end of update, or at the very least after calling update on any object that references camera.
                camera.resetHasChanged();

                int numberClickedPoints = clickedPoints.size();
                if (clearPoints && numberClickedPoints > 0) {
                    clickedPoints.clear();
                    System.out.println("Cleared " + numberClickedPoints + " points.");
                    clearPoints = false;
                } else if (connectPoints && numberClickedPoints > 1) {
                    lines.add(new LineObject2D(LineSegment2D.fromPoints(clickedPoints.toArray(new Vec2f[0]))));
                    clickedPoints.clear();
                    System.out.println("Connected " + numberClickedPoints + " points.");
                    connectPoints = false;
                }

                updateCount++;
                lastUpdate += updateInterval;
            }

            int drawCalls = renderer.getDrawCalls();

            if (currentTime - lastSecond > 1.0d) {
                lastSecond = currentTime;
                glfwSetWindowTitle(window, "Basic Grid (" + frameCount + " FPS, " + updateCount + " UPS, " + drawCalls + " DCs)");
                frameCount = 0;
                updateCount = 0;
            }
        }
    }

    private void cleanUp() {
        gridShader.cleanUp();
        lineShader.cleanUp();
        randomShader.cleanUp();
        textureShader.cleanUp();
        Loader.cleanUp();
    }

    private void closeOpenGL() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

}
