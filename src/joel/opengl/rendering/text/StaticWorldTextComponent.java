package joel.opengl.rendering.text;

import joel.opengl.entity.Component;
import joel.opengl.entity.EntityHandler;
import joel.opengl.maths.Vec2f;
import joel.opengl.maths.Vec3f;

public class StaticWorldTextComponent extends Component {

    private TextRenderer renderer;
    private Font font;
    private float charSpacing, height;

    public TextLine line;

    public StaticWorldTextComponent(TextRenderer renderer, Font font, float charSpacing, float height) {
        this.renderer = renderer;
        this.font = font;
        this.charSpacing = charSpacing;
        this.height = height;
//        setText(" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007F\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F ¡¢£¤¥¦§¨©ª«¬\u00AD®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊË");
        setText("The quick brown fox jumps over the lazy dog");
        System.out.println(font.baseWidths[font.getIndex('L')]);
    }

    public void setText(String text) {
        line = new TextLine(font, text, charSpacing, height, new Vec3f(0.0f));
    }

    public Font getFont() {
        return font;
    }

    @Override
    public void onComponentAdded(EntityHandler entityHandler, int entity) {
        renderer.staticWorldTexts.add(entity);
    }

    @Override
    public void onComponentRemoved(EntityHandler entityHandler, int entity) {
        renderer.staticWorldTexts.remove(entity);
    }

    public class TextLine {

        public Font font;
        public String text;
        public float charSpacing, height; // in world coords
        public Vec3f offset;

        public boolean changed = true;

        private int[] indices;
        private float[] vertices;

        public TextLine(Font font, String text, float charSpacing, float height, Vec3f offset) {
            this.font = font;
            this.text = text;
            this.charSpacing = charSpacing;
            this.height = height;
            this.offset = offset;
        }

        public int[] getIndices() {
            if (!changed) return indices;
            indices = new int[6 * text.length()]; // 1 quad per char = 2 triangles per char = 6 indices per char
            for (int i = 0; i < text.length(); i++) {
                indices[6 * i + 0] = 4 * i + 0; // top left
                indices[6 * i + 1] = 4 * i + 1; // bottom left
                indices[6 * i + 2] = 4 * i + 2; // bottom right
                indices[6 * i + 3] = 4 * i + 0; // top left
                indices[6 * i + 4] = 4 * i + 2; // bottom right
                indices[6 * i + 5] = 4 * i + 3; // top right
            }
            return indices;
        }

        public float[] getVertices(Vec3f origin) {
            if (!changed) return vertices;

            float pixelToWorldCoord = height / (float) font.fontHeight; // pixelToWorldCoord * pixelWidth = worldWidth

            vertices = new float[4 * text.length() * 5]; // 4 vertices per character, 5 floats per vertex (x,y,z,u,v)

            float currentX = 0.0f;
            char[] characters = text.toCharArray();
            for (int i = 0; i < characters.length; i++) {
                char c = characters[i];

                float pixelWidth = font.baseWidths[font.getIndex(c)];
                float width = pixelToWorldCoord * (float) pixelWidth;
                float texWidth = (pixelWidth / (float) font.imageWidth);
                Vec2f texCoord = font.getTexCoord(c); // UV Coordinate of top left of character cell

                // Top Left Vertex
                vertices[20 * i + 0] = origin.x() + offset.x() + currentX; // x
                vertices[20 * i + 1] = origin.y() + offset.y(); // y
                vertices[20 * i + 2] = origin.z() + offset.z(); // z
                vertices[20 * i + 3] = texCoord.x(); // u
                vertices[20 * i + 4] = texCoord.y(); // v

                // Bottom Left Vertex
                vertices[20 * i + 5] = origin.x() + offset.x() + currentX; // x
                vertices[20 * i + 6] = origin.y() + offset.y() - height; // y
                vertices[20 * i + 7] = origin.z() + offset.z(); // z
                vertices[20 * i + 8] = texCoord.x(); // u
                vertices[20 * i + 9] = texCoord.y() - font.relCellHeight; // v

                // Bottom Right Vertex
                vertices[20 * i + 10] = origin.x() + offset.x() + currentX + width; // x
                vertices[20 * i + 11] = origin.y() + offset.y() - height; // y
                vertices[20 * i + 12] = origin.z() + offset.z(); // z
                vertices[20 * i + 13] = texCoord.x() + texWidth; // u
                vertices[20 * i + 14] = texCoord.y() - font.relCellHeight; // v

                // Top Right Vertex
                vertices[20 * i + 15] = origin.x() + offset.x() + currentX + width; // x
                vertices[20 * i + 16] = origin.y() + offset.y(); // y
                vertices[20 * i + 17] = origin.z() + offset.z(); // z
                vertices[20 * i + 18] = texCoord.x() + texWidth; // u
                vertices[20 * i + 19] = texCoord.y(); // v

                currentX += width + charSpacing;
            }

            return vertices;
        }

    }

}
