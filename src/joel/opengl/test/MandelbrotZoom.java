package joel.opengl.test;

import joel.opengl.mandelbrot.MandelbrotShaderAbstract;
import joel.opengl.mandelbrot.MandelbrotZoomShader;
import joel.opengl.oldRendering.FullscreenQuad2D;
import joel.opengl.oldRendering.Loader;
import joel.opengl.oldRendering.OldRenderer;
import joel.opengl.mandelbrot.MandelbrotZoomShaderDoubles;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class MandelbrotZoom {

    // The window handle
    private long window;

    private final int WIDTH = 960, HEIGHT = 960;
    private final double UPS = 60.0d;

    private boolean useDoubles = false;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private boolean up, down, left, right, in, out, limitUp, limitDown, iterUp, iterDown, updateColour, toggleCrosshair;
    private int newColour;

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

//        GL45.glDebugMessageCallback(new GLDebugMessageCallback() {
//            @Override
//            public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
//
//            }
//        }, 0L);

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
//        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

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
                case GLFW_KEY_T:
                    limitUp = press;
                    break;
                case GLFW_KEY_G:
                    limitDown = press;
                    break;
                case GLFW_KEY_R:
                    iterUp = press;
                    break;
                case GLFW_KEY_F:
                    iterDown = press;
                    break;
                case GLFW_KEY_SPACE:
                    toggleCrosshair = toggleCrosshair || action == GLFW_PRESS;
                    break;
                case GLFW_KEY_1:
                    updateColour = newColour != 1;
                    newColour = 1;
                    break;
                case GLFW_KEY_2:
                    updateColour = newColour != 2;
                    newColour = 2;
                    break;
                case GLFW_KEY_3:
                    updateColour = newColour != 3;
                    newColour = 3;
                    break;
                case GLFW_KEY_4:
                    updateColour = newColour != 4;
                    newColour = 4;
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

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        double lastSecond = glfwGetTime(), lastUpdate = glfwGetTime();
        final double updateInterval = 1.0d / UPS;
        int frameCount = 0, updateCount = 0;

        OldRenderer renderer = new OldRenderer();
        FullscreenQuad2D screen = new FullscreenQuad2D();

        MandelbrotShaderAbstract shader;
        if (useDoubles) {
            shader = new MandelbrotZoomShaderDoubles(WIDTH, HEIGHT, 0.38117625939857014d, -0.2676774386535821d, 1.0d, 2.05f, 10, 1, true);
        } else {
            shader = new MandelbrotZoomShader(WIDTH, HEIGHT, 0.38117625939857014f, -0.2676774386535821f, 1.0f, 2.05f, 10, 1, true);
        }

//        glGetInteger(GL_MAX_VERTEX_UNIFORM_COMPONENTS);
//        System.out.println(glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS));

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            shader.bind();
            renderer.render(screen);
            shader.unbind();

            glfwSwapBuffers(window); // swap the color buffers

            // FPS Counter in title.
            frameCount++;
            double currentTime = glfwGetTime();

            if (currentTime - lastUpdate > updateInterval) {
                // Do an update
                shader.update(up, down, left, right, in, out, limitUp, limitDown, iterUp, iterDown, updateColour, newColour, toggleCrosshair);
                updateColour = false;
                toggleCrosshair = false;

                updateCount++;
                lastUpdate = currentTime;
            }
            if (currentTime - lastSecond > 1.0d) {
                lastSecond = currentTime;
                glfwSetWindowTitle(window, "Mandelbrot Zoom (" + frameCount + " FPS, " + updateCount + " UPS)");
                frameCount = 0;
                updateCount = 0;
            }
        }

        shader.cleanUp();
        Loader.cleanUp();
    }

    protected MandelbrotZoom(boolean useDoubles) {
        this.useDoubles = useDoubles;
    }

    public static void main(String[] args) {
        new MandelbrotZoom(false).run();
    }

}
