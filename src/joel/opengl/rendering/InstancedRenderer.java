package joel.opengl.rendering;

import joel.opengl.entity.EntityHandler;
import joel.opengl.shaders.ShaderProgram;

import java.util.Arrays;
import java.util.HashMap;

import static org.lwjgl.opengl.GL46.*;

public abstract class InstancedRenderer<T extends MeshComponent> {

    public static final int INSTANCE_BUFFER_DEFAULT_SIZE = 64;

    public final EntityHandler entityHandler;
    protected final HashMap<Integer, Integer> entityToIndex = new HashMap<>(); // maps entity id to instance index
    protected final HashMap<Integer, Integer> indexToEntity = new HashMap<>(); // maps instance index to entity id
    private int entityCount;

    private int instanceBuffer;
    private int instanceBufferSize;
    public boolean[] indexNeedsUpdate;
    // if indexNeedsUpdate[instanceIndex] == true then data in the instance vertex buffer needs updated at the given index.

    public InstancedRenderer(EntityHandler entityHandler) {
        this.entityHandler = entityHandler;
        initInstanceBuffer();
    }

    public abstract Class<? extends MeshComponent> getComponentClass();

    /**
     * @return Generated buffer location
     */
    public int initInstanceBuffer() {
        indexNeedsUpdate = new boolean[INSTANCE_BUFFER_DEFAULT_SIZE];
        instanceBufferSize = INSTANCE_BUFFER_DEFAULT_SIZE * bytesPerInstance();
        instanceBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, instanceBuffer);
        glBufferData(GL_ARRAY_BUFFER, instanceBufferSize, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return instanceBuffer;
    }

    /**
     * Creates new array buffer with twice the capacity of old buffer. Copys over data from old buffer. Deletes old buffer.
     * @return New buffer location
     */
    public int growInstanceBuffer() {
        int oldSize = indexNeedsUpdate.length;
        int newSize = oldSize << 1;

        int newBufferSize = newSize * bytesPerInstance();

        indexNeedsUpdate = Arrays.copyOf(indexNeedsUpdate, newSize);
        int newBuffer = glGenBuffers();
        glBindBuffer(GL_COPY_WRITE_BUFFER, newBuffer);
        glBufferData(GL_COPY_WRITE_BUFFER, newBufferSize, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_COPY_READ_BUFFER, instanceBuffer);
        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, 0L, 0L, instanceBufferSize);
        glBindBuffer(GL_COPY_READ_BUFFER, 0);
        glBindBuffer(GL_COPY_WRITE_BUFFER, 0);
        glDeleteBuffers(instanceBuffer);

        instanceBuffer = newBuffer;
        instanceBufferSize = newBufferSize;

        setupInstanceBuffer();

        return instanceBuffer;
    }

    public void deleteInstanceBuffer() {
        glDeleteBuffers(instanceBuffer);
    }

    public void removeEntity(int entity) {
        Integer indexC = entityToIndex.get(entity);
        if (indexC == null) return;
        int index = indexC.intValue();
        int lastIndex = --entityCount;
        int replacementEntity = indexToEntity.get(lastIndex);
        indexToEntity.put(index, replacementEntity);
        indexToEntity.remove(lastIndex, replacementEntity);
        entityToIndex.put(replacementEntity, index);
        entityToIndex.remove(entity, index);
        indexNeedsUpdate[index] = true;
    }

    /**
     * @return instance index of added entity
     */
    public int addEntity(int entity) {
        int index = entityCount++;
        if (index >= indexNeedsUpdate.length) growInstanceBuffer();
        indexToEntity.put(index, entity);
        entityToIndex.put(entity, index);
        indexNeedsUpdate[index] = true;
        return index;
    }

    public int getEntityCount() {
        return entityCount;
    }

    public int getInstanceIndex(int entity) {
        return entityToIndex.get(entity);
    }

    public int getEntity(int instanceIndex) {
        return indexToEntity.get(instanceIndex);
    }

    public int getInstanceBuffer() {
        return instanceBuffer;
    }

    public abstract ShaderProgram getShader();
    public abstract void onComponentAdded(int entity, T component);
    public abstract void onComponentRemoved(int entity, T component);
    public abstract int bytesPerInstance();
    public abstract float[] getVertices();
    public abstract int[] getIndices();
    public abstract void setupInstanceBuffer();
    public abstract void pushData(int entity);
    public abstract void pushAllData(); // Should be called before render
    public abstract InstancedRenderer init(); // Should return itself to make storing new renderer look nice in Renderer#initRenderers
    public abstract void render();
    public abstract void cleanUp();

}
