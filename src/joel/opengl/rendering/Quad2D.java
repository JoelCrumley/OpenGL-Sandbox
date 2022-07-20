package joel.opengl.rendering;

import joel.opengl.maths.BoundingBoxAA2D;
import joel.opengl.maths.Maths;
import joel.opengl.maths.Vec2f;
import org.lwjgl.stb.STBImage;

import static org.lwjgl.opengl.GL20.*;

public class Quad2D extends RenderObject {

    private static int[] indices = {
            0,1,3,	//Top left triangle (V0,V1,V3)
            3,1,2	//Bottom right triangle (V3,V1,V2)
    };
    private BoundingBoxAA2D boundingBox;

    // Vertices should be given in order v0, v1, v2, v3.
    // Quad is constructed anti clockwise.
    // https://i.imgur.com/5PYyKP5.png
    public Quad2D(float[] vertices) {
        vao = Loader.createAndBindVAO();
        Loader.bindIndicesBuffer(indices);
        Loader.storeDataInAttributeList(0, vertices, 2, GL_FLOAT, false, 0 , 0);
        Loader.unbindVAO();
        vertexCount = indices.length;

        this.boundingBox = BoundingBoxAA2D.fromVerticesArray(vertices);
    }

    public Quad2D(Vec2f center, Vec2f size) {
        this(quadVertices(center, size));
    }
    
    public static float[] quadVertices(Vec2f center, Vec2f size) {
        float dw = size.x() / 2.0f, dh = size.y() / 2.0f;
        float[] vertices = new float[] {
                center.x() - dw, center.y() + dh,
                center.x() - dw, center.y() - dh,
                center.x() + dw, center.y() - dh,
                center.x() + dw, center.y() + dh
        };
        return vertices;
    }

    public BoundingBoxAA2D getBoundingBox() {
        return boundingBox;
    }

    @Override
    public boolean shouldAlwaysRender() {
        return false;
    }
}
