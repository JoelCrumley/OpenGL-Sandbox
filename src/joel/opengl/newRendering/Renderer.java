package joel.opengl.newRendering;

import joel.opengl.entity.EntityHandler;
import joel.opengl.window.Window;
import org.lwjgl.opengl.GL;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL46.*;

public class Renderer {

    private final HashMap<Class<? extends MeshComponent>, InstancedRenderer<?>> renderers = new HashMap<>();
    public final EntityHandler entityHandler;
    public final Window window;
    public final Camera camera;

    public Renderer(Window window, EntityHandler entityHandler, float nearClip, float farClip, float fov) {
        this.entityHandler = entityHandler;
        this.window = window;
        camera = new Camera(nearClip, farClip, fov, (float) window.getWidth() / (float) window.getHeight());
        initOpenGL();
        initRenderers();
    }

    public void render() {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (camera.hasChanged()) camera.calculateMatrix();

        for (InstancedRenderer renderer : renderers.values()) renderer.render();

        camera.setNotChanged();

        glfwSwapBuffers(window.id);

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

    private void initRenderers() {
        renderers.put(ColouredCubeMeshComponent.class, new ColouredCubeMeshRenderer(entityHandler, camera).init());
    }

    public <T extends MeshComponent> InstancedRenderer<T> getRenderer(T type) {
        return (InstancedRenderer<T>) renderers.get(type.getClass());
    }

    public <T extends MeshComponent> InstancedRenderer<T> getRenderer(Class<T> type) {
        return (InstancedRenderer<T>) renderers.get(type);
    }

    public void cleanUp() {
        for (InstancedRenderer renderer : renderers.values()) {
            renderer.deleteInstanceBuffer();
            renderer.cleanUp();
        }
    }

}
