package joel.opengl.maths;

public class Maths {

//    public static boolean isEqual(float a, float b, int precision) {
//        int c = 0x0;
//        Math.getExponent(a);
//
//        return true;
//    }

    public static int clamp(int i, int lower, int upper) {
        return i <= lower ? lower : (i >= upper ? upper : i);
    }

    public static double clamp(double d, double lower, double upper) {
        return d <= lower ? lower : (d >= upper ? upper : d);
    }

    public static float clamp(float f, float lower, float upper) {
        return f <= lower ? lower : (f >= upper ? upper : f);
    }

    public static float abs(float a) {
        return a > 0.0f ? a : -a;
    }

    public static float min(float a, float b) {
        return a > b ? b : a;
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }



}
