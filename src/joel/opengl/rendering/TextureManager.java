package joel.opengl.rendering;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL40.*;

public class TextureManager {

    public final int TEXTURE_SLOTS;

    public final ArrayList<Texture> textures = new ArrayList<>();

    public TextureManager() {
        TEXTURE_SLOTS = glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
    }

    public void cleanUp() {
        for (Texture texture : textures) texture.unload();
    }

}
