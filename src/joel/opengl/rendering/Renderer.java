package joel.opengl.rendering;

import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    public Renderer() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.95f, 0.95f, 0.95f, 1.0f);

        // Set up alpha blending
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        System.out.println("Vendor: " + glGetString(GL_VENDOR) + "\nRenderer: " + glGetString(GL_RENDERER) + "\nVersion: " + glGetString(GL_VERSION));
    }

    private int drawCalls = 0;

    // Only kept here because Mandelbrot program hasn't been updated to use the Camera2D object
    @Deprecated
    public void render(RenderObject object) {
        glBindVertexArray(object.vao);
        glEnableVertexAttribArray(0);
        glDrawElements(GL_TRIANGLES, object.vertexCount, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        drawCalls++;
    }

    public boolean render(RenderObject object, Camera2D camera) {
        if (!object.shouldAlwaysRender() && !shouldRender(object, camera)) return false;
        glBindVertexArray(object.vao);
        glEnableVertexAttribArray(0);
        glDrawElements(GL_TRIANGLES, object.vertexCount, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        drawCalls++;
        return true;
    }

    public boolean render(LineObject2D line, Camera2D camera) {
        if (!line.shouldAlwaysRender() && !shouldRender(line, camera)) return false;
        glBindVertexArray(line.vao);
        glEnableVertexAttribArray(0);
        glDrawArrays(line.mode, 0, line.vertexCount);
//        glDrawElements(GL_LINES, object.vertexCount, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        drawCalls++;
        return true;
    }

    public boolean render(TexturedQuad2D quad, Camera2D camera) {
        if (!quad.shouldAlwaysRender() && !shouldRender(quad, camera)) return false;
        glBindVertexArray(quad.vao);
        quad.texture.bind();
        glEnableVertexAttribArray(0);
        glDrawElements(GL_TRIANGLES, quad.vertexCount, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        drawCalls++;
        return true;
    }

    public static boolean shouldRender(RenderObject object, Camera2D camera) {
        // Checks if object's bounding box is visible based on where camera is
        return object.getBoundingBox().intersects(camera.getGridViewport());
    }

    public int getDrawCalls() {
        int calls = drawCalls;
        drawCalls = 0;
        return calls;
    }

}
