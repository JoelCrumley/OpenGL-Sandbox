package joel.opengl.maths;

public class MatrixUtils {

    public static float[] diagonalArray(int dimension, float... values) {
        assert values.length == dimension;
        float[] arr = zeroArray(dimension * dimension);
        for (int i = 0; i < dimension; i++) arr[i * (dimension + 1)] = values[i];
        return arr;
    }

    public static float[] identityArray(int dimension) {
        return diagonalArray(dimension, 1.0f, 1.0f, 1.0f, 1.0f);
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
