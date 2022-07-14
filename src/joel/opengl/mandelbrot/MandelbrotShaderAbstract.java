package joel.opengl.mandelbrot;

import joel.opengl.shaders.ShaderProgram;

public abstract class MandelbrotShaderAbstract extends ShaderProgram {

    public MandelbrotShaderAbstract(String vertexFile, String fragmentFile) {
        super(vertexFile, fragmentFile);
    }

    public abstract void update(boolean up, boolean down, boolean left, boolean right, boolean in, boolean out,
                                boolean limitUp, boolean limitDown, boolean iterUp, boolean iterDown, boolean updateColour,
                                int newColour, boolean toggleCrosshair);

}
