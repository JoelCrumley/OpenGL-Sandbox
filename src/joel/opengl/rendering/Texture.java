package joel.opengl.rendering;

import joel.opengl.oldRendering.Loader;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;

public class Texture {

    private final TextureManager manager;

    public volatile ByteBuffer stbImageData = null;
    public volatile int width = 0, height = 0;

    private int textureID = -1;

    public Texture(TextureManager manager) {
        this.manager = manager;
        manager.textures.add(this);
    }

    public boolean isOnGPU() {
        return textureID >= 0;
    }

    public int getTextureID() {
        return textureID;
    }

    public void bind(int textureSlot) {
        glActiveTexture(GL_TEXTURE0 + textureSlot);
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void loadGPU(int textureSlot) {
        if (!isOnCPU()) return;
        textureID = glGenTextures();
        bind(textureSlot);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, stbImageData);
    }

    public void unloadGPU() {
        glDeleteTextures(textureID);
        textureID = -1;
    }

    public boolean isOnCPU() {
        return stbImageData != null;
    }

    public void loadCPU(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Prepare image buffers */
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            /* Load image */
            stbi_set_flip_vertically_on_load(true);

            ByteBuffer fileData;
            try {
                InputStream is = Loader.class.getResourceAsStream("/" + path);
                byte[] bytes = is.readAllBytes();

                fileData = BufferUtils.createByteBuffer(bytes.length);
                fileData.put(bytes);
                fileData.flip();
            } catch (IOException e) {
                System.err.println("Error loading image from \"" + path + "\"");
                e.printStackTrace();
                return;
            }
            stbImageData = stbi_load_from_memory(fileData, w, h, comp, 4);

            if (stbImageData == null) {
                throw new RuntimeException("Failed to load a texture file!"
                        + System.lineSeparator() + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }
    }

    public void unloadCPU() {
        if (stbImageData == null) return;
        stbi_image_free(stbImageData);
        stbImageData = null;
    }

    public void unload() {
        unloadGPU();
        unloadCPU();
    }

    public void delete() {
        unload();
        Iterator<Texture> it = manager.textures.iterator();
        while (it.hasNext()) {
            if (it.next() == this) {
                it.remove();
                break;
            }
        }
    }

}
