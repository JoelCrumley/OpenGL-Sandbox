package joel.opengl.rendering;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class Loader {

    private static final List<Integer> vaos = new ArrayList<Integer>();
    private static final List<Integer> vbos = new ArrayList<Integer>();

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
    }

    // Should be followed by an unbindVAO call after manipulation.
    public static int createAndBindVAO() {
        int vaoID = glGenVertexArrays();
        vaos.add(vaoID);
        glBindVertexArray(vaoID);
        return vaoID;
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

}
