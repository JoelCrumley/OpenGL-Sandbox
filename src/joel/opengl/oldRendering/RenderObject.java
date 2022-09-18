package joel.opengl.oldRendering;

import joel.opengl.maths.BoundingBoxAA2D;

public abstract class RenderObject {
    protected int vao; // Pointer
    protected int vertexCount;
    public abstract BoundingBoxAA2D getBoundingBox();
    public abstract boolean shouldAlwaysRender(); // Used for things that don't use world coordinates. Should be replaced by a proper rendering hierarchy.
}
