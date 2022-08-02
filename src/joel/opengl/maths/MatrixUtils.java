package joel.opengl.maths;

public class MatrixUtils {

    public static float[] identityArray(int dimension) {
        float[] arr = zeroArray(dimension * dimension);
        for (int i = 0; i < arr.length; i += dimension + 1) arr[i] = 1.0f;
        return arr;
    }

    public static float[] zeroArray(int size) {
        return fillArray(size, 0.0f);
    }

    public static float[] fillArray(int size, float value) {
        float[] arr = new float[size];
        for (int i = 0; i < arr.length; i++) arr[i] = value;
        return arr;
    }

}
