package joel.opengl.rendering;

import joel.opengl.window.Window;
import org.lwjgl.opengl.GL;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;

public abstract class Renderer {

    protected final Window window;
    protected final Camera camera;
    private final HashMap<Class<? extends MeshComponent>, InstancedRenderer<?>> renderers = new HashMap<>();

    protected Renderer(Window window, Camera camera) {
        this.window = window;
        this.camera = camera;
        initOpenGL();
    }

    private void initOpenGL() {
        GL.createCapabilities();

        glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

        glEnable(GL_DEPTH_TEST); // Makes sure triangles get drawn in right order
        glEnable(GL_CULL_FACE); // Only draw triangles facing camera

        // Set up alpha blending
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        System.out.println("Vendor: " + glGetString(GL_VENDOR) + "\nRenderer: " + glGetString(GL_RENDERER) + "\nVersion: " + glGetString(GL_VERSION));
    }

    public void addRenderer(InstancedRenderer<?> renderer) {
        renderers.put(renderer.getComponentClass(), renderer.init());
    }

    public <T extends MeshComponent> InstancedRenderer<T> getRenderer(T type) {
        return (InstancedRenderer<T>) renderers.get(type.getClass());
    }

    public <T extends MeshComponent> InstancedRenderer<T> getRenderer(Class<T> type) {
        return (InstancedRenderer<T>) renderers.get(type);
    }

    public abstract void callRenderers();

    public void render() {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (camera.hasChanged()) camera.calculateMatrix();

        for (InstancedRenderer renderer : renderers.values()) renderer.render();

        callRenderers();

        camera.setNotChanged();

        glfwSwapBuffers(window.id);

    }

    public abstract void cleanUpRenderers();

    public void cleanUp() {
        for (InstancedRenderer renderer : renderers.values()) {
            renderer.deleteInstanceBuffer();
            renderer.cleanUp();
        }
        cleanUpRenderers();
    }

    public Camera getCamera() {
        return camera;
    }

}
