package joel.opengl.maths;

public class Vecf {

    public float[] data;

    public Vecf(float... data) {
        assert data.length != 0;
        this.data = data.clone();
    }

    public Vecf(int dim, float defaultValue) {
        assert dim > 0;
        data = new float[dim];
        for (int i = 0; i < dim; i++) data[i] = defaultValue;
    }

    public Vecf(double... data) { // Because java is scared to implicitly cast doubles to floats, I guess because it loses bits.
        assert data.length != 0;
        this.data = toFloats(data);
    }

    public Vecf clone() {
        return new Vecf(data);
    }

    public static float[] toFloats(double... values) {
        float[] arr = new float[values.length];
        for (int i = 0; i < arr.length; i++) arr[i] = (float) values[i];
        return arr;
    }

    public int dimension() {
        return data.length;
    }

    public String toString() {
        if (data.length == 1) {
            return "(" + data[0] + ")";
        } else {
            String s = "(" + data[0];
            for (int i = 1; i < data.length; i++) s += ", " + data[i];
            s += ")";
            return s;
        }
    }

    public Vecf multiply(float... values) {
        if (values.length == 1) {
            for (int i = 0; i < data.length; i++) data[i] *= values[0];
        } else {
            assert values.length == data.length;
            for (int i = 0; i < data.length; i++) data[i] *= values[i];
        }
        return this;
    }

    public Vecf multiply(double... values) {
        if (values.length == 1) {
            for (int i = 0; i < data.length; i++) data[i] *= (float) values[0];
        } else {
            assert values.length == data.length;
            for (int i = 0; i < data.length; i++) data[i] *= (float) values[i];
        }
        return this;
    }

    public Vecf multiply(Vecf vec) {
        return multiply(vec.data);
    }

    public Vecf divide(float... values) {
        if (values.length == 1) {
            for (int i = 0; i < data.length; i++) data[i] /= values[0];
        } else {
            assert values.length == data.length;
            for (int i = 0; i < data.length; i++) data[i] /= values[i];
        }
        return this;
    }

    public Vecf divide(double... values) {
        if (values.length == 1) {
            for (int i = 0; i < data.length; i++) data[i] /= (float) values[0];
        } else {
            assert values.length == data.length;
            for (int i = 0; i < data.length; i++) data[i] /= (float) values[i];
        }
        return this;
    }

    public Vecf divide(Vecf vec) {
        return divide(vec.data);
    }

    public Vecf add(float... values) {
        if (values.length == 1) {
            for (int i = 0; i < data.length; i++) data[i] += values[0];
        } else {
            assert values.length == data.length;
            for (int i = 0; i < data.length; i++) data[i] += values[i];
        }
        return this;
    }

    public Vecf add(double... values) {
        if (values.length == 1) {
            for (int i = 0; i < data.length; i++) data[i] += (float) values[0];
        } else {
            assert values.length == data.length;
            for (int i = 0; i < data.length; i++) data[i] += (float) values[i];
        }
        return this;
    }

    public Vecf add(Vecf vec) {
        return add(vec.data);
    }

    public Vecf subtract(float... values) {
        if (values.length == 1) {
            for (int i = 0; i < data.length; i++) data[i] -= values[0];
        } else {
            assert values.length == data.length;
            for (int i = 0; i < data.length; i++) data[i] -= values[i];
        }
        return this;
    }

    public Vecf subtract(double... values) {
        if (values.length == 1) {
            for (int i = 0; i < data.length; i++) data[i] -= (float) values[0];
        } else {
            assert values.length == data.length;
            for (int i = 0; i < data.length; i++) data[i] -= (float) values[i];
        }
        return this;
    }

    public Vecf subtract(Vecf vec) {
        return subtract(vec.data);
    }

    // Add values to data, if values dimension is less than data dimension assume that the missed values are zero.
    public Vecf addFillZero(float... values) {
        assert data.length >= values.length;
        for (int i = 0; i < values.length; i++) data[i] += values[i];
        return this;
    }

    public Vecf addFillZero(double... values) {
        assert data.length >= values.length;
        for (int i = 0; i < values.length; i++) data[i] += (float) values[i];
        return this;
    }

    public Vecf addFillZero(Vecf vec) {
        return addFillZero(vec.data);
    }

    // Subtract values from data, if values dimension is less than data dimension assume that the missed values are zero.
    public Vecf subtractFillZero(float... values) {
        assert data.length >= values.length;
        for (int i = 0; i < values.length; i++) data[i] -= values[i];
        return this;
    }

    public Vecf subtractFillZero(double... values) {
        assert data.length >= values.length;
        for (int i = 0; i < values.length; i++) data[i] -= (float) values[i];
        return this;
    }

    public Vecf subtractFillZero(Vecf vec) {
        return subtractFillZero(vec.data);
    }

    public Vecf negate() {
        for (int i = 0; i < data.length; i++) data[i] = -data[i];
        return this;
    }

    public static float lengthSquared(Vecf vec) {
        float value = 0.0f;
        for (float f : vec.data) value += f*f;
        return value;
    }

    public float lengthSquared() {
        return lengthSquared(this);
    }

    public static double length(Vecf vec) {
        return Math.sqrt(lengthSquared(vec));
    }

    public double length() {
        return length(this);
    }

    public Vecf normalize() {
        double length = length();
        assert length != 0;
        return divide(length);
    }

    public float dot(Vecf vec) {
        assert data.length == vec.data.length;
        float value = 0.0f;
        for (int i = 0; i < data.length; i++) value += data[i] * vec.data[i];
        return value;
    }

    public float distanceSquaredTo(Vecf vec) {
        return vec.clone().subtract(this).lengthSquared();
    }

    public double distanceTo(Vecf vec) {
        return Math.sqrt(distanceSquaredTo(vec));
    }

    @Deprecated
    // Should use epsilon comparison depending on use case;
    public boolean equals(Vecf vec) {
        if (data.length != vec.length()) return false;
        for (int i = 0; i < data.length; i++) if (data[i] != vec.data[i]) return false;
        return true;
    }

    public static Vecf zeroVector(int dimension) {
        return new Vecf(dimension, 0.0f);
    }

}
