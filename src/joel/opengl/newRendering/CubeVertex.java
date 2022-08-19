package joel.opengl.newRendering;

public enum CubeVertex {

    NEGATIVE_XYZ(0),
    NEGATIVE_XY_POSITIVE_Z(1),
    NEGATIVE_XZ_POSITIVE_Y(2),
    NEGATIVE_X_POSITIVE_YZ(3),
    NEGATIVE_YZ_POSITIVE_X(4),
    NEGATIVE_Y_POSITIVE_XZ(5),
    NEGATIVE_Z_POSITIVE_XY(6),
    POSITIVE_XYZ(7);

    public final int index;
    private CubeVertex(int index) {
        this.index = index;
    }

}
