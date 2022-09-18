package joel.opengl.rendering.text;

import joel.opengl.maths.Vec2f;
import joel.opengl.rendering.Texture;
import joel.opengl.rendering.TextureManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Font {

    public final String name;

    public String internalName;
    public int imageWidth, imageHeight, cellWidth, cellHeight, startChar, fontHeight, characters, cellsPerRow, cellsPerColumn;
    public float relCellWidth, relCellHeight;
    public int[] baseWidths, widthOffsets, xOffsets, yOffsets;

    public Texture atlas;

    protected Font(String name) {
        this.name = name;
    }

    public boolean isSupported(char character) {
        int index = (int) character - startChar;
        return (index >= 0 && index < character);
    }

    public int getIndex(char character) {
        int index = (int) character - startChar;
        return (index < 0 || index >= character) ? 0 : index;
    }

    public int getWidth(String string, int spacing) {
        if (string.length() == 0) return 0;
        int width = spacing * (string.length() - 1);
        for (char c : string.toCharArray()) width += baseWidths[getIndex(c)];
        return width;
    }

    /**
     * N.B. OpenGL uv coords have (0,0) at bottom left, and (1,1) at top right, so to get bottom right uv coord do: topLeft + Vec2f(relCellWidth, -relCelHeight)
     * @param character
     * @return Top left uv coord of the cell on the texture atlas corresponding to the character inputted. Returns character 0 coord if out of bounds.
     */
    public Vec2f getTexCoord(char character) {
        int index = getIndex(character);
        float row = index / cellsPerRow;
        float col = index % cellsPerRow;
        return new Vec2f(col * relCellWidth, 1.0f - row * relCellHeight);
    }

    public boolean loadFontData() {
        ArrayList<String> lines = new ArrayList<>();

        try {
            InputStream stream = Font.class.getResourceAsStream("/" + FontManager.FONT_PARENT_DIR + "/" + name + "/fontData.csv");
            if (stream == null) return false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while((line = reader.readLine())!=null) lines.add(line);
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }

        int count = 0;
        for (String line : lines) {
            if (count > 8) break;
            String[] split = line.split(",");
            if (split.length < 2) continue;

            if (split[0].equalsIgnoreCase("Image Width")) {
                imageWidth = Integer.parseInt(split[1]);
            } else if (split[0].equalsIgnoreCase("Image Height")) {
                imageHeight = Integer.parseInt(split[1]);
            } else if (split[0].equalsIgnoreCase("Cell Width")) {
                cellWidth = Integer.parseInt(split[1]);
            } else if (split[0].equalsIgnoreCase("Cell Height")) {
                cellHeight = Integer.parseInt(split[1]);
            } else if (split[0].equalsIgnoreCase("Start Char")) {
                startChar = Integer.parseInt(split[1]);
            } else if (split[0].equalsIgnoreCase("Font Name")) {
                internalName = split[1];
            } else if (split[0].equalsIgnoreCase("Font Height")) {
                fontHeight = Integer.parseInt(split[1]);
            }

            count++;
        }

        relCellWidth = (float) cellWidth / (float) imageWidth;
        relCellHeight = (float) cellHeight / (float) imageHeight;
        cellsPerRow = imageWidth / cellWidth;
        cellsPerColumn = imageHeight / cellHeight;
        characters = cellsPerColumn * cellsPerRow;
        baseWidths = new int[characters];
        widthOffsets = new int[characters];
        xOffsets = new int[characters];
        yOffsets = new int[characters];

        for (String line : lines) {
            String[] split = line.split(",");
            String chop = split[0];
            if (!chop.startsWith("Char ")) continue;
            chop = chop.substring(5);

            if (chop.endsWith(" Base Width")) {
                chop = chop.substring(0, chop.length() - " Base Width".length());
                baseWidths[Integer.parseInt(chop)] = Integer.parseInt(split[1]);
            } else if (chop.endsWith(" Width Offset")) {
                chop = chop.substring(0, chop.length() - " Width Offset".length());
                widthOffsets[Integer.parseInt(chop)] = Integer.parseInt(split[1]);
            } else if (chop.endsWith(" X Offset")) {
                chop = chop.substring(0, chop.length() - " X Offset".length());
                xOffsets[Integer.parseInt(chop)] = Integer.parseInt(split[1]);
            } else if (chop.endsWith(" Y Offset")) {
                chop = chop.substring(0, chop.length() - " Y Offset".length());
                yOffsets[Integer.parseInt(chop)] = Integer.parseInt(split[1]);
            }
        }

        return true;
    }

    public Texture loadAtlas(TextureManager manager) {
        atlas = new Texture(manager);
        atlas.loadCPU(FontManager.FONT_PARENT_DIR + "/" + name + "/atlas.bmp");
        atlas.loadGPU(0);
        atlas.unloadCPU();
        return atlas;
    }

}
