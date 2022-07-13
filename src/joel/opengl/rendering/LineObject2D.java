package joel.opengl.rendering;

import joel.opengl.maths.BoundingBoxAA2D;
import joel.opengl.maths.LineSegment2D;
import joel.opengl.maths.Vec2f;

import static org.lwjgl.opengl.GL30.*;

public class LineObject2D extends RenderObject {

    public int mode;
    private BoundingBoxAA2D boundingBox;

    public LineObject2D(LineSegment2D... lines) {
        this.mode = GL_LINES;
        vao = Loader.createAndBindVAO();
        float[] vertices = new float[lines.length * 4];
        for (int i = 0; i < lines.length; i++) {
            int start = 4 * i;
            LineSegment2D line = lines[i];
            vertices[start + 0] = line.start.x();
            vertices[start + 1] = line.start.y();
            vertices[start + 2] = line.end.x();
            vertices[start + 3] = line.end.y();
        }
        Loader.storeDataInAttributeList(0, vertices, 2, GL_FLOAT, false, 0 , 0);
        Loader.unbindVAO();
        vertexCount = vertices.length / 2;
        boundingBox = BoundingBoxAA2D.fromVerticesArray(vertices);
    }

    public LineObject2D(boolean loop, Vec2f... points) {
        this.mode = loop ? GL_LINE_LOOP : GL_LINE_STRIP;
        vao = Loader.createAndBindVAO();
        float[] vertices = new float[points.length * 2];
        for (int i = 0; i < points.length; i++) {
            Vec2f point = points[i];
            int start = 2 * i;
            vertices[start + 0] = point.x();
            vertices[start + 1] = point.y();
        }
        Loader.storeDataInAttributeList(0, vertices, 2, GL_FLOAT, false, 0 , 0);
        Loader.unbindVAO();
        vertexCount = points.length;
        boundingBox = BoundingBoxAA2D.fromVerticesArray(vertices);
    }

    public BoundingBoxAA2D getBoundingBox() {
        return boundingBox;
    }

    @Override
    public boolean shouldAlwaysRender() {
        return false;
    }
}
