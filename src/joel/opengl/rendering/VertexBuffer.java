package joel.opengl.rendering;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class VertexBuffer {

    private int id;

    public VertexBuffer(FloatBuffer buffer) {
        id = glGenBuffers();
        bind();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public VertexBuffer(float[] data) {
        this(Loader.arrayToBuffer(data));
    }

    public int getID() {
        return id;
    }

    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, id);
    }

    public static void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void cleanup() {
        glDeleteBuffers(id);
    }

}
