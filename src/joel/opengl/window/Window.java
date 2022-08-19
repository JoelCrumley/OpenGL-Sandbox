package joel.opengl.window;

import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public final long id;
    public final KeyboardCallback[] keyboardCallbacks;
    private ResizeCallback resizeCallback;

    private int width, height;
    private String title;

    public Window(int width, int height, String title) {
        System.out.println("LWJGL " + Version.getVersion() + "!");

        setDimensions(width, height);
        this.title = title;

        GLFWErrorCallback.createPrint(System.err).set();
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

        id = glfwCreateWindow(width, height, title, NULL, NULL);

        if (id == NULL)
            throw new RuntimeException("Failed to create the GLFW window '" + title + "'");

        keyboardCallbacks = new KeyboardCallback[GLFW_KEY_LAST + 1];

        glfwSetKeyCallback(id, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key < 0 || key > GLFW_KEY_LAST) return;
                KeyboardCallback callback = keyboardCallbacks[key];
                if (callback == null) return;
                callback.keyEvent(KeyboardCallback.Action.fromGLFW(action),
                        (mods & GLFW_MOD_SHIFT) != 0, (mods & GLFW_MOD_CONTROL) != 0, (mods & GLFW_MOD_ALT) != 0,
                        (mods & GLFW_MOD_SUPER) != 0, (mods & GLFW_MOD_CAPS_LOCK) != 0, (mods & GLFW_MOD_NUM_LOCK) != 0);
            }
        });

        glfwSetFramebufferSizeCallback(id, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                int oldWidth = getWidth(), oldHeight = getHeight();
                setDimensions(width, height);
                if (resizeCallback != null) resizeCallback.resize(width, height, oldWidth, oldHeight);
            }
        });

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(id, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    id,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(id);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(id);

    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(id);
    }

    public void destroy() {
        glfwFreeCallbacks(id);
        glfwDestroyWindow(id);
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(id, title);
    }

    public Window setVisible(boolean visible) {
        if (visible) glfwShowWindow(id);
        else glfwHideWindow(id);
        return this;
    }

    public Window setResizable(boolean resizable) {
        glfwSetWindowAttrib(id, GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
        return this;
    }

    public Window focus() {
        glfwFocusWindow(id);
        return this;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Window setKeyCallback(int key, KeyboardCallback callback) {
        keyboardCallbacks[key] = callback;
        return this;
    }

    public Window setResizeCallback(ResizeCallback callback) {
        resizeCallback = callback;
        return this;
    }

}
