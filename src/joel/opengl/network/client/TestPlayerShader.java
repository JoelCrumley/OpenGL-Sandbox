package joel.opengl.network.client;

import joel.opengl.shaders.ShaderProgram;

public class TestPlayerShader extends ShaderProgram {

    private static final String VERTEX_FILE = "vertTestPlayer.txt";
    private static final String FRAGMENT_FILE = "fragSolidColour.txt";

    private int resolutionLocation, translationLocation, sizeLocation, colourLocation;

    public TestPlayerShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        resolutionLocation = super.getUniformLocation("resolution");
        translationLocation = super.getUniformLocation("translation");
        sizeLocation = super.getUniformLocation("size");
        colourLocation = super.getUniformLocation("colour");
    }

    public void pushResolution(float width, float height) {
        super.pushVector(resolutionLocation, width, height);
    }

    public void pushTranslation(float x, float y) {
        super.pushVector(translationLocation, x , y);
    }

    public void pushSize(float size) {
        super.pushFloat(sizeLocation, size);
    }

    public void pushColour(float r, float g, float b, float a) {
        super.pushVector(colourLocation, r, g, b, a);
    }

}
