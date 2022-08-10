package joel.opengl.network.server;

import joel.opengl.maths.Maths;
import joel.opengl.maths.Vec2f;
import joel.opengl.maths.Vec4f;

public class Player {

    public static final float SPEED = 20.0f, MIN_SIZE = 1.0f, MAX_SIZE = 256.0f, DEFAULT_SIZE = 10.0f, SIZE_SPEED = 4.0f;

    public final int id;
    public Vec2f position;
    public float size;
    public Vec4f colour;

    public Player(int id, Vec2f position, float size, Vec4f colour) {
        this.id = id;
        this.position = position;
        setSize(size);
        this.colour = colour;
    }

    public void setSize(float size) {
        this.size = Maths.clamp(size, MIN_SIZE, MAX_SIZE);
    }

}
