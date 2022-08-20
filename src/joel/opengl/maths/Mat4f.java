package joel.opengl.maths;

public class Mat4f {

    public static final int DIMENSION = 4, LENGTH = 16;

    public float[] data;
    public boolean rowMajor = true;

    public Mat4f() {
        this(MatrixUtils.identityArray(DIMENSION));
    }

    public Mat4f(float[] data) {
        assert data.length == LENGTH;
        this.data = data.clone();
    }

    public Mat4f(float[] data, boolean rowMajor) {
        this(data);
        this.rowMajor = rowMajor;
    }

    public Mat4f clone() {
        return new Mat4f(this.data, this.rowMajor);
    }

    public float get(int i, int j) {
        return rowMajor ? data[j + DIMENSION * i] : data[i + DIMENSION * j];
    }

    public Mat4f set(int i, int j, float f) {
        data[rowMajor ? j + DIMENSION * i : i + DIMENSION * j ] = f;
        return this;
    }

    // returns this x vector
    public Vec4f multiply(Vec4f vector) {
        float[] vec = new float[DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            float value = 0.0f;
            for (int j = 0; j < DIMENSION; j++) value += get(i, j) * vector.data[j];
            vec[i] = value;
        }
        return new Vec4f(vec);
    }

    // this = this x other
    public Mat4f multiply(Mat4f other) {
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

    public Mat4f leftMultiply(Mat4f other) {
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

    public Mat4f multiply(float scalar) {
        for (int i = 0; i < LENGTH; i++) data[i] *= scalar;
        return this;
    }

    public Mat4f add(Mat4f other) {
        toRowMajor();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                data[j + i * DIMENSION] += other.get(i, j);
            }
        }
        return this;
    }

    public Mat4f subtract(Mat4f other) {
        toRowMajor();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                data[j + i * DIMENSION] -= other.get(i, j);
            }
        }
        return this;
    }

    public Mat4f transpose() {
        rowMajor = !rowMajor;
        return this;
    }

    // Flips rowMajor but does not transpose. i.e. reformats data.
    public Mat4f swapFormat() {
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

    public Mat4f toRowMajor() {
        return rowMajor ? this : swapFormat();
    }

    public float getDeterminant() {
        float[] cofactors = new float[DIMENSION];

        for (int i = 0; i < DIMENSION; i++) {

            float[] currentMatrix = new float[9];
            int index = 0;
            for (int a = 0; a < DIMENSION; a++) {
                for (int b = 1; b < DIMENSION; b++) {
                    if (a == i) continue;
                    currentMatrix[index++] = get(a, b);
                }
            }
            cofactors[i] = determinant3x3(currentMatrix);

        }

        return get(0, 0) * cofactors[0] - get(1, 0) * cofactors[1] + get(2, 0) * cofactors[2] - get(3, 0) * cofactors[3];
    }

    public float[] getInverse() {
        float[] data = new float[LENGTH];

        float[] currentMatrix = new float[9];
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
                data[i + j * DIMENSION] = ((i + j) % 2 == 0 ? 1.0f : -1.0f) * Mat4f.determinant3x3(currentMatrix);
            }
        }

        float antiDeterminant = 1.0f / (get(0, 0) * data[0] + get(1, 0) * data[1] + get(2, 0) * data[2] + get(3, 0) * data[3]);
        if (Float.isInfinite(antiDeterminant)) return null;

        for (int i = 0; i < data.length; i++) data[i] *= antiDeterminant;

        return data;
    }

    public Mat4f invert() {
        data = getInverse();
        rowMajor = true;
        return this;
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

    public static Mat4f identityMatrix() {
        return new Mat4f();
    }

    public static Mat4f zeroMatrix() {
        return new Mat4f(MatrixUtils.zeroArray(LENGTH));
    }

    public static Mat4f diagonalMatrix(float f1, float f2, float f3, float f4) {
        return new Mat4f(MatrixUtils.diagonalArray(DIMENSION, f1, f2, f3, f4));
    }

    public static Mat4f translationMatrix(Vec3f translation) {
        return identityMatrix().set(0, 3, translation.x()).set(1, 3, translation.y()).set(2, 3, translation.z());
    }

    public static Mat4f scaleMatrix(Vec3f scale) {
        return diagonalMatrix(scale.x(), scale.y(), scale.z(), 1.0f);
    }

    public static Mat4f rotationMatrix(Vec3f rotation) {
        Vec3f x = new Vec3f(1.0f, 0.0f, 0.0f), y = new Vec3f(0.0f, 1.0f, 0.0f), z = new Vec3f(0.0f, 0.0f, 1.0f);
        Quaternion quaternion = Quaternion.rotationQuaternion(rotation.x(), x)
                        .multiply(Quaternion.rotationQuaternion(rotation.y(), y))
                        .multiply(Quaternion.rotationQuaternion(rotation.z(), z));
        Vec3f right = quaternion.rotateVector(x), up = quaternion.rotateVector(y), forward = quaternion.rotateVector(z);
        float[] data = new float[] {
                right.x(), up.x(), forward.x(), 0.0f,
                right.y(), up.y(), forward.y(), 0.0f,
                right.z(), up.z(), forward.z(), 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
        };
        return new Mat4f(data);
    }

    // http://www.songho.ca/opengl/gl_projectionmatrix.html
    public static Mat4f perspectiveProjectionMatrix(float left, float right, float top, float bottom, float near, float far) {
        return new Mat4f(new float[] {
                (2.0f * near) / (right - left), 0.0f, (right + left) / (right - left), 0.0f,
                0.0f, (2.0f * near) / (top - bottom), (top + bottom) / (top - bottom), 0.0f,
                0.0f, 0.0f, -(far + near) / (far - near), (-2.0f * far * near) / (far - near),
                0.0f, 0.0f, -1.0f, 0.0f
        });
    }

    public static Mat4f symmetricPerspectiveProjectionMatrix(float right, float top, float near, float far) {
        return new Mat4f(new float[] {
                near / right, 0.0f, 0.0f, 0.0f,
                0.0f, near / top, 0.0f, 0.0f,
                0.0f, 0.0f, -(far + near) / (far - near), (-2.0f * far * near) / (far - near),
                0.0f, 0.0f, -1.0f, 0.0f
        });
    }

    public static Mat4f fromVec3(Vec3f e1, Vec3f e2, Vec3f e3) {
        return new Mat4f(new float[] {
                e1.x(), e2.x(), e3.x(), 0.0f,
                e1.y(), e2.y(), e3.y(), 0.0f,
                e1.z(), e2.z(), e3.z(), 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f,
        });
    }

    public static Mat4f modelToWorldMatrix(Vec3f translation, Vec3f rotation, Vec3f scale) {
        return translationMatrix(translation).multiply(rotationMatrix(rotation)).multiply(scaleMatrix(scale));
    }

}
