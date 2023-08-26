package joel.opengl.rendering;

import joel.opengl.entity.EntityHandler;
import joel.opengl.rendering.text.TextRenderer;
import joel.opengl.window.Window;
import org.lwjgl.opengl.GL;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL46.*;

public class Renderer3D extends Renderer {

    private TextRenderer textRenderer;
    public final EntityHandler entityHandler;

    public Renderer3D(Window window, Camera3D camera, EntityHandler entityHandler) {
        super(window, camera);
        this.entityHandler = entityHandler;
        initRenderers();
    }

    @Override
    public Camera3D getCamera() {
        return (Camera3D) camera;
    }

    private void initRenderers() {
        textRenderer = new TextRenderer(entityHandler, getCamera()).init();
        addRenderer(new ColouredCubeMeshRenderer(entityHandler, getCamera()));
    }

    @Override
    public void callRenderers() {
        textRenderer.render();
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    @Override
    public void cleanUpRenderers() {
        textRenderer.cleanUp();
    }

}
