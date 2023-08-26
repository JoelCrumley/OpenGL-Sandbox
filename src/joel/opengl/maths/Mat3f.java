package joel.opengl.maths;

public class Mat3f extends Matf {

    public static final int DIMENSION = 3, LENGTH = 9;

    public Mat3f() {
        this(MatrixUtils.identityArray(DIMENSION));
    }

    public Mat3f(float... data) {
        assert data.length == LENGTH;
        this.data = data.clone();
    }

    public Mat3f(float[] data, boolean rowMajor) {
        this(data);
        this.rowMajor = rowMajor;
    }

    public Mat3f clone() {
        return new Mat3f(this.data, this.rowMajor);
    }

    public float get(int i, int j) {
        return rowMajor ? data[j + DIMENSION * i] : data[i + DIMENSION * j];
    }

    public Mat3f set(int i, int j, float f) {
        data[rowMajor ? j + DIMENSION * i : i + DIMENSION * j ] = f;
        return this;
    }

    // returns this x vector
    public Vec3f multiply(Vec3f vector) {
        float[] vec = new float[DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            float value = 0.0f;
            for (int j = 0; j < DIMENSION; j++) value += get(i, j) * vector.data[j];
            vec[i] = value;
        }
        return new Vec3f(vec);
    }

    // this = this x other
    public Mat3f multiply(Mat3f other) {
        toRowMajor();
        float[] currentRow = new float[DIMENSION];
        for (int row = 0; row < DIMENSION; row++) {

            for (int i = 0; i < DIMENSION; i++) currentRow[i] = get(row, i);

            for (int column = 0; column < DIMENSION; column++) {
                float value = 0.0f;
                for (int i = 0; i < DIMENSION; i++) value += currentRow[i] * other.get(i, column);
                data[column + row * DIMENSION] = value;
            }

        }
        return this;
    }

    public Mat3f leftMultiply(Mat3f other) {
        toRowMajor();
        float[] currentCol = new float[DIMENSION];
        for (int column = 0; column < DIMENSION; column++) {

            for (int i = 0; i < DIMENSION; i++) currentCol[i] = get(i, column);

            for (int row = 0; row < DIMENSION; row++) {
                float value = 0.0f;
                for (int i = 0; i < DIMENSION; i++) value += currentCol[i] * other.get(row, i);
                data[column + row * DIMENSION] = value;
            }

        }
        return this;
    }

    public Mat3f multiply(float scalar) {
        for (int i = 0; i < LENGTH; i++) data[i] *= scalar;
        return this;
    }

    public Mat3f add(Mat3f other) {
        toRowMajor();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                data[j + i * DIMENSION] += other.get(i, j);
            }
        }
        return this;
    }

    public Mat3f subtract(Mat3f other) {
        toRowMajor();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                data[j + i * DIMENSION] -= other.get(i, j);
            }
        }
        return this;
    }

    public Mat3f transpose() {
        rowMajor = !rowMajor;
        return this;
    }

    // Flips rowMajor but does not transpose. i.e. reformats data.
    public Mat3f swapFormat() {
        data = transposeData(data);
        this.rowMajor = !rowMajor;
        return this;
    }

    public float[] getRowMajorData() {
        return rowMajor ? data.clone() : transposeData(data);
    }

    public float[] getColumnMajorData() {
        return rowMajor ? transposeData(data) : data.clone();
    }

    public static float[] transposeData(float[] data) {
        float[] newData = new float[LENGTH];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                newData[j + DIMENSION * i] = data[i + DIMENSION * j];
            }
        }
        return newData;
    }

    public Mat3f toRowMajor() {
        return rowMajor ? this : swapFormat();
    }

    public float getDeterminant() {
        return determinant3x3(data);
//        float[] cofactors = new float[DIMENSION];
//
//        for (int i = 0; i < DIMENSION; i++) {
//
//            float[] currentMatrix = new float[9];
//            int index = 0;
//            for (int a = 0; a < DIMENSION; a++) {
//                for (int b = 1; b < DIMENSION; b++) {
//                    if (a == i) continue;
//                    currentMatrix[index++] = get(a, b);
//                }
//            }
//            cofactors[i] = determinant3x3(currentMatrix);
//
//        }
//
//        return get(0, 0) * cofactors[0] - get(1, 0) * cofactors[1] + get(2, 0) * cofactors[2] - get(3, 0) * cofactors[3];
    }

    public float[] getInverse() {
        float[] data = new float[LENGTH];

        float[] currentMatrix = new float[4];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {

                int index = 0;
                for (int a = 0; a < DIMENSION; a++) {
                    for (int b = 0; b < DIMENSION; b++) {
                        if (a == i || b == j) continue;
                        currentMatrix[index++] = get(a, b);
                    }
                }

                // Note cofactors are transposed below to get adjoint matrix
                data[i + j * DIMENSION] = ((i + j) % 2 == 0 ? 1.0f : -1.0f) * Mat3f.determinant2x2(currentMatrix);
            }
        }

        float antiDeterminant = 1.0f / (get(0, 0) * data[0] + get(1, 0) * data[1] + get(2, 0) * data[2]);
        if (Float.isInfinite(antiDeterminant)) return null;

        for (int i = 0; i < data.length; i++) data[i] *= antiDeterminant;

        return data;
    }

    public Mat3f invert() {
        data = getInverse();
        rowMajor = true;
        return this;
    }

    public static float determinant2x2(float[] data) {
        assert data.length == 4;
        return data[0] * data[3] - data[1] * data[2];
    }

    public static float determinant3x3(float[] data) {
        assert data.length == 9;
        return data[0] * (data[4] * data[8] - data[5] * data[7])
                - data[1] * (data[3] * data[8] - data[5] * data[6])
                + data[2] * (data[3] * data[7] - data[4] * data[6]);
    }

    public void print() {
        String s = "\n{";
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) s += " " + get(i, j) + ( i == j && j == DIMENSION-1 ? "" :  ",");
            s += i == DIMENSION - 1 ? " }" : "\n ";
        }
        System.out.println(s);
    }

    public static void printArray(float[] data) {
        assert data.length == LENGTH;
        String s = "\n{";
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) s += " " + data[j + i * DIMENSION] + ( i == j && j == DIMENSION-1 ? "" :  ",");
            s += i == DIMENSION - 1 ? " }" : "\n ";
        }
        System.out.println(s);
    }

    public static Mat3f identityMatrix() {
        return new Mat3f();
    }

    public static Mat3f zeroMatrix() {
        return new Mat3f(MatrixUtils.zeroArray(LENGTH));
    }

    public static Mat3f diagonalMatrix(float f1, float f2, float f3) {
        return new Mat3f(MatrixUtils.diagonalArray(DIMENSION, f1, f2, f3));
    }

    public static Mat3f translationMatrix(Vec2f translation) {
        return identityMatrix().set(0, 2, translation.x()).set(1, 2, translation.y());
    }

    public static Mat3f scaleMatrix(Vec2f scale) {
        return diagonalMatrix(scale.x(), scale.y(), 1.0f);
    }



    public static Mat3f rotationMatrix(float rotation) {
        float cos = (float) Math.cos(rotation), sin = (float) Math.sin(rotation);
        return new Mat3f(cos, -sin, 0.0f, sin, cos, 0.0f, 0.0f, 0.0f, 1.0f);
    }

//    // http://www.songho.ca/opengl/gl_projectionmatrix.html
//    public static Mat3f perspectiveProjectionMatrix(float left, float right, float top, float bottom, float near, float far) {
//        return new Mat3f(new float[] {
//                (2.0f * near) / (right - left), 0.0f, (right + left) / (right - left), 0.0f,
//                0.0f, (2.0f * near) / (top - bottom), (top + bottom) / (top - bottom), 0.0f,
//                0.0f, 0.0f, -(far + near) / (far - near), (-2.0f * far * near) / (far - near),
//                0.0f, 0.0f, -1.0f, 0.0f
//        });
//    }
//
//    public static Mat3f symmetricPerspectiveProjectionMatrix(float right, float top, float near, float far) {
//        return new Mat3f(new float[] {
//                near / right, 0.0f, 0.0f, 0.0f,
//                0.0f, near / top, 0.0f, 0.0f,
//                0.0f, 0.0f, -(far + near) / (far - near), (-2.0f * far * near) / (far - near),
//                0.0f, 0.0f, -1.0f, 0.0f
//        });
//    }

    public static Mat3f fromVec2(Vec2f e1, Vec2f e2) {
        return new Mat3f(
                e1.x(), e2.x(), 0.0f,
                e1.y(), e2.y(), 0.0f,
                0.0f, 0.0f, 1.0f);
    }

    public static Mat3f modelToWorldMatrix(Vec2f translation, float rotation, Vec2f scale) {
        return translationMatrix(translation).multiply(rotationMatrix(rotation)).multiply(scaleMatrix(scale));
    }

}
