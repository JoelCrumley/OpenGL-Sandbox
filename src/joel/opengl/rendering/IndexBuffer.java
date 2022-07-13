package joel.opengl.rendering;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;

public class IndexBuffer {

    private int id, indexCount;

    public IndexBuffer(IntBuffer buffer, int indexCount) {
        id = glGenBuffers();
        this.indexCount = indexCount;
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public IndexBuffer(int[] data) {
        this(Loader.arrayToBuffer(data), data.length);
    }

    public int getID() {
        return id;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    public static void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void cleanup() {
        glDeleteBuffers(id);
    }

}
