package joel.opengl.rendering;

import static org.lwjgl.opengl.GL40.*;

public class TextureManager {

    public final int TEXTURE_SLOTS;

    public TextureManager() {
        TEXTURE_SLOTS = glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS);
    }
}
