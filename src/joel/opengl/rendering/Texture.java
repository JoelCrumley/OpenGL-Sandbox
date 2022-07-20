package joel.opengl.rendering;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL40.*;

public class Texture {

    public final int id, width, height;

    protected Texture(int width, int height, ByteBuffer data) {
        id = glGenTextures();
        this.width = width;
        this.height = height;
        System.out.println("Texture (" + id + "): " + width + "x" + height);

        bind();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    }

    public float getScaledWidth(float height) {
        return height * (float) this.width / (float) this.height;
    }

    public float getScaledHeight(float width) {
        return width * (float) this.height / (float) this.width;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    protected void cleanUp() {
        glDeleteTextures(id);
    }

}
