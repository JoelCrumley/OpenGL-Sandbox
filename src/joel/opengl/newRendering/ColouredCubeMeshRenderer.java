package joel.opengl.newRendering;

import joel.opengl.entity.Component;
import joel.opengl.entity.EntityHandler;
import joel.opengl.entity.components.TransformComponent;
import joel.opengl.shaders.ColouredCubeMeshShader;
import joel.opengl.util.Bag;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Map;

import static org.lwjgl.opengl.GL33.*;

public class ColouredCubeMeshRenderer extends InstancedRenderer<ColouredCubeMeshComponent> {

    public ColouredCubeMeshRenderer(EntityHandler entityHandler, Camera camera) {
        super(entityHandler);
        shader = new ColouredCubeMeshShader(camera);
    }

    private ColouredCubeMeshShader shader;

    private int vao, indices, vertices;

    @Override
    public ColouredCubeMeshShader getShader() {
        return shader;
    }

    @Override
    public ColouredCubeMeshRenderer init() {

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        indices = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, getIndices(), GL_STATIC_DRAW);

        vertices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertices);
        glBufferData(GL_ARRAY_BUFFER, getVertices(), GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);

        setupInstanceBuffer();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices);
        IntBuffer buffer = BufferUtils.createIntBuffer(getIndices().length);
        glGetBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, buffer);

        return this;
    }

    @Override
    public void setupInstanceBuffer() {

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, getInstanceBuffer());

        for (int i = 1; i <= 12; i++) {
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, 4, GL_FLOAT, false, bytesPerInstance(), 4 * 4 * (i - 1));
            glVertexAttribDivisor(i, 1);
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);

    }

    @Override
    public void render() {

        pushAllData();

        shader.bind();
        if (shader.camera.hasChanged()) shader.pushMatrix();

        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
        glDrawElementsInstanced(GL_TRIANGLES, getIndices().length, GL_UNSIGNED_INT, 0, getEntityCount());
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shader.unbind();

    }

    @Override
    public void onComponentAdded(int entity, ColouredCubeMeshComponent component) {
        addEntity(entity);
    }

    @Override
    public void onComponentRemoved(int entity, ColouredCubeMeshComponent component) {
        removeEntity(entity);
    }

    @Override
    public int bytesPerInstance() {
        return 4 * (16 + 8*4); // 16 floats from Mat4f, 32 floats from 8 Vec4f.
    }

    @Override
    public float[] getVertices() {
        return ColouredCubeMeshComponent.VERTICES;
    }

    @Override
    public int[] getIndices() {
        return ColouredCubeMeshComponent.INDICES;
    }

    public void pushData(int index, TransformComponent transform, ColouredCubeMeshComponent cube) {
        if (!(transform.hasChanged() || cube.changed || indexNeedsUpdate[index])) return;

        float[] data = Arrays.copyOf(transform.getModelToWorldMatrix().getColumnMajorData(), bytesPerInstance() / 4);
        for (int i = 0; i < cube.colours.length; i++) {
            float[] colourData = cube.colours[i].data;
            for (int j = 0; j < colourData.length; j++) data[16 + 4*i + j] = colourData[j];
        }

        glBindBuffer(GL_COPY_WRITE_BUFFER, getInstanceBuffer());
        glBufferSubData(GL_COPY_WRITE_BUFFER, index * bytesPerInstance(), data);
        glBindBuffer(GL_COPY_WRITE_BUFFER, 0);

        indexNeedsUpdate[index] = false;
    }

    @Override
    public void pushData(int entity) {
        int index = getInstanceIndex(entity);

        Component[] components = entityHandler.getComponents(entity, TransformComponent.class, ColouredCubeMeshComponent.class);
        TransformComponent transform = (TransformComponent) components[0];
        ColouredCubeMeshComponent cube = (ColouredCubeMeshComponent) components[1];

        pushData(index, transform, cube);
    }

    @Override
    public void pushAllData() {

        Bag<TransformComponent> transforms = new Bag<>();
        Bag<ColouredCubeMeshComponent> cubes = new Bag<>();

        for (Map.Entry<Integer, Integer> entry : entityToIndex.entrySet()) {
            int entity = entry.getKey(), index = entry.getValue();
            Component[] components = entityHandler.getComponents(entity, TransformComponent.class, ColouredCubeMeshComponent.class);
            TransformComponent transform = (TransformComponent) components[0];
            transforms.add(transform);
            ColouredCubeMeshComponent cube = (ColouredCubeMeshComponent) components[1];
            cubes.add(cube);
            pushData(index, transform, cube);
        }

        for (TransformComponent transform : transforms) transform.setHasNotChanged();
        for (ColouredCubeMeshComponent cube : cubes) cube.changed = false;

    }

    @Override
    public void cleanUp() {
        glDeleteBuffers(vertices);
        glDeleteBuffers(indices);
        glDeleteVertexArrays(vao);
    }

}
