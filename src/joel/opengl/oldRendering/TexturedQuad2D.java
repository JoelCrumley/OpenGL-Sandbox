package joel.opengl.oldRendering;

import joel.opengl.maths.Vec2f;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

public class TexturedQuad2D extends Quad2D {

    public OldTexture texture;

    private static final float[] fullImageCoords = new float[] {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    public TexturedQuad2D(float[] vertices, float[] textureCoords, OldTexture texture) {
        super(vertices);

        Loader.bindVAO(vao);
        Loader.storeDataInAttributeList(1, textureCoords, 2, GL_FLOAT, false, 0 , 0);
        Loader.unbindVAO();

        this.texture = texture;
    }

    public TexturedQuad2D(float[] vertices, OldTexture texture) {
        this(vertices, fullImageCoords, texture);
    }

    public TexturedQuad2D(Vec2f center, Vec2f size, OldTexture texture) {
        this(Quad2D.quadVertices(center, size), texture);
    }

}
