package joel.opengl.rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Loader {

    private static final List<Integer> vaos = new ArrayList<Integer>();
    private static final List<Integer> vbos = new ArrayList<Integer>();
    private static final List<Texture> textures = new ArrayList<>();

//    public RawModel loadToVAO(float[] positions,int[] indices){
//        int vaoID = createVAO();
//        bindIndicesBuffer(indices);
//        storeDataInAttributeList(0,positions);
//        unbindVAO();
//        return new RawModel(vaoID,indices.length);
//    }

    public static void cleanUp(){
        for(int vao : vaos) glDeleteVertexArrays(vao);
        for(int vbo : vbos) glDeleteBuffers(vbo);
        for (Texture texture : textures) texture.cleanUp();
    }

    // Should be followed by an unbindVAO call after manipulation (but not really necessary).
    public static int createAndBindVAO() {
        int vaoID = glGenVertexArrays();
        vaos.add(vaoID);
        glBindVertexArray(vaoID);
        return vaoID;
    }

    public static void bindVAO(int vao) {
        glBindVertexArray(vao);
    }

    public static void unbindVAO(){
        glBindVertexArray(0);
    }

    public static int createVBO() {
        int vboID = glGenBuffers();
        vbos.add(vboID);
        return vboID;
    }

    public static int storeDataInAttributeList(int attributeNumber, float[] data, int size, int type, boolean normalized, int stride, int offset) {
        int vboID = createVBO();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = arrayToBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(attributeNumber);
        glVertexAttribPointer(attributeNumber, size, type, normalized, stride, offset);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vboID;
    }

    public static int bindIndicesBuffer(int[] indices) {
        int vboID = createVBO();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = arrayToBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        return vboID;
    }

    public static IntBuffer arrayToBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static FloatBuffer arrayToBuffer(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer arrayToBuffer(byte[] data){
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static Texture loadTexture(String path) {
        ByteBuffer image;
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Prepare image buffers */
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            /* Load image */
            stbi_set_flip_vertically_on_load(true);

            ByteBuffer buffer;
            try {
                InputStream is = Loader.class.getResourceAsStream("/" + path);
                byte[] bytes = is.readAllBytes();
                buffer = Loader.arrayToBuffer(bytes);
            } catch (IOException e) {
                System.err.println("Error loading image from \"" + path + "\"");
                e.printStackTrace();
                return null;
            }
            image = stbi_load_from_memory(buffer, w, h, comp, 4);

//            image = stbi_load(RESOURCES_DIR + path, w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load a texture file!"
                        + System.lineSeparator() + stbi_failure_reason());
            }

            /* Get width and height of image */
            width = w.get();
            height = h.get();
        }
        Texture texture = new Texture(width, height, image);
        textures.add(texture);
        stbi_image_free(image);
        return texture;
    }

}
