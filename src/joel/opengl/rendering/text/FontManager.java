package joel.opengl.rendering.text;

import joel.opengl.rendering.TextureManager;

import java.util.HashMap;

public class FontManager {

    public static final String FONT_PARENT_DIR = "fonts";

    private final HashMap<String, Font> fonts = new HashMap<>();

    private TextureManager textureManager;

    public FontManager(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public Font loadFont(String name) {
        Font font = new Font(name);
        if (!font.loadFontData()) return null;
        font.loadAtlas(textureManager);
        fonts.put(name, font);
        return font;
    }

    public void unloadFont(String name) {
        Font font = getFont(name);
        if (font == null) return;
        font.atlas.delete();
        fonts.remove(name);
    }

    public Font getFont(String name) {
        return fonts.get(name);
    }

}
