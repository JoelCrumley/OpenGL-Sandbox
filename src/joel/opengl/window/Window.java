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
    public final boolean[] isKeyDown = new boolean[GLFW_KEY_LAST];
    private ResizeCallback resizeCallback;
    private MouseMoveCallback mouseMoveCallback;
    private double lastMouseX, lastMouseY;

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

        glfwSetCursorPosCallback(id, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if (mouseMoveCallback != null) mouseMoveCallback.mouseMove(lastMouseX, lastMouseY, xpos, ypos);
                lastMouseX = xpos;
                lastMouseY = ypos;
            }
        });

        glfwSetKeyCallback(id, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key < 0 || key > GLFW_KEY_LAST) return;
                isKeyDown[key] = action != GLFW_RELEASE;
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

    public void close() {
        glfwSetWindowShouldClose(id, true);
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

    public Window setDecorated(boolean decorated) {
        glfwSetWindowAttrib(id, GLFW_DECORATED, decorated ? GLFW_TRUE : GLFW_FALSE);
        return this;
    }

    public Window resize(int newWidth, int newHeight) {
        glfwSetWindowSize(id, newWidth, newHeight);
        return this;
    }

    public Window setPosition(int x, int y) {
        glfwSetWindowPos(id, x, y);
        return this;
    }

    public Window centerWindow() {
        return centerWindow(glfwGetPrimaryMonitor());
    }

    public Window centerWindow(long monitor) {
        GLFWVidMode videoMode = glfwGetVideoMode(monitor);
        glfwSetWindowPos(id,(videoMode.width() - width) / 2,(videoMode.height() - height) / 2);
        return this;
    }

    public Window setBorderlessFullscreen() {
        return setBorderlessFullscreen(glfwGetPrimaryMonitor());
    }

    public Window setBorderlessFullscreen(long monitor) {
        GLFWVidMode videoMode = glfwGetVideoMode(monitor);
        return this.setDecorated(false).resize(videoMode.width(), videoMode.height()).centerWindow(monitor);
    }

    public Window setWindowed(int width, int height) {
        return setWindowed(glfwGetPrimaryMonitor(), width, height);
    }

    public Window setWindowed(long monitor, int width, int height) {
        return this.setDecorated(true).resize(width, height).centerWindow(monitor);
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

    public void setWindowCloseCallback(GLFWWindowCloseCallbackI callback) {
        glfwSetWindowCloseCallback(id, callback);
    }

    public void setWindowPositionCallback(GLFWWindowPosCallbackI callback) {
        glfwSetWindowPosCallback(id, callback);
    }

    public void setWindowFocusCallback(GLFWWindowFocusCallbackI callback) {
        glfwSetWindowFocusCallback(id, callback);
    }

    public void setMouseMoveCallback(MouseMoveCallback callback) {
        mouseMoveCallback = callback;
    }

    public void setMouseEnterCallback(GLFWCursorEnterCallbackI callback) {
        glfwSetCursorEnterCallback(id, callback);
    }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI callback) {
        glfwSetMouseButtonCallback(id, callback);
    }

    public void setScrollCallback(GLFWScrollCallbackI callback) {
        glfwSetScrollCallback(id, callback);
    }

    public void disableCursor() {
        glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void hideCursor() {
        glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }

    public void enableCursor() {
        glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

}
