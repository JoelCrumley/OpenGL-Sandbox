package joel.opengl.newRendering;

import joel.opengl.entity.EntityHandler;
import joel.opengl.maths.Vec4f;

public class ColouredCubeMeshComponent extends MeshComponent {

    public static final float[] VERTICES = new float[] {
        -0.5f, -0.5f, -0.5f, 0.0f, // x, y, z, vertexIndex
        -0.5f, -0.5f, +0.5f, 1.0f,
        -0.5f, +0.5f, -0.5f, 2.0f,
        -0.5f, +0.5f, +0.5f, 3.0f,
        +0.5f, -0.5f, -0.5f, 4.0f,
        +0.5f, -0.5f, +0.5f, 5.0f,
        +0.5f, +0.5f, -0.5f, 6.0f,
        +0.5f, +0.5f, +0.5f, 7.0f
    };

    public static final int[] INDICES = new int[] {
        // -x face
        0, 1, 2,
        2, 1, 3,
        // +x face
        5, 4, 7,
        7, 4, 6,
        // +y face
        2, 3, 6,
        6, 3, 7,
        // -y face
        1, 0, 5,
        5, 0, 4,
        // +z face
        1, 5, 3,
        3, 5, 7,
        // -z face
        4, 0, 6,
        6, 0, 2
    };

    protected Vec4f[] colours;
    protected boolean changed = true;

    public ColouredCubeMeshComponent(Renderer renderer, Vec4f colour) {
        this(renderer, fillArray(colour));
    }

    public static Vec4f[] fillArray(Vec4f colour) {
        Vec4f[] colours = new Vec4f[8];
        for (int i = 0; i < 8; i++) colours[i] = colour.clone();
        return colours;
    }

    public ColouredCubeMeshComponent(Renderer renderer, Vec4f[] colours) {
        super(renderer);
        assert colours.length == 8;
        this.colours = colours;
    }

    public void setColour(CubeVertex vertex, Vec4f colour) {
        colours[vertex.index] = colour;
        changed = true;
    }

    public Vec4f getColour(CubeVertex vertex) {
        return colours[vertex.index];
    }

    @Override
    public void onComponentAdded(EntityHandler entityHandler, int entity) {
        renderer.getRenderer(this).onComponentAdded(entity, this);
    }

    @Override
    public void onComponentRemoved(EntityHandler entityHandler, int entity) {
        renderer.getRenderer(this).onComponentRemoved(entity, this);
    }

}
