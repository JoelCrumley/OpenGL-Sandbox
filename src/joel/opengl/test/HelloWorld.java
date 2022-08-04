package joel.opengl.test;

import joel.opengl.maths.Mat4f;
import joel.opengl.maths.Maths;
import joel.opengl.maths.Vec4f;
import joel.opengl.maths.security.PrimeGenerator;
import joel.opengl.util.TimerUtil;

import java.math.BigInteger;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

public class HelloWorld {

    private void run(String[] args) {

//        for (int i = 0; i < 256; i++) System.out.println(i + ": " + ((char)i));

//        int[] primesUpTo1000 = PrimeGenerator.primesUpToN(1000);
//        for (int i = 0; i < primesUpTo1000.length; i++) {
//            System.out.println(primesUpTo1000[i]);
//        }

        int primes = 1, bits = 1024;
        long[] times = new long[primes];
        for (int i = 0; i < primes; i++) {
            TimerUtil.start(TimerUtil.Type.GENERIC);
            System.out.println(PrimeGenerator.generate(bits, 64).toString());
            times[i] = TimerUtil.getTime(TimerUtil.Type.GENERIC);
            TimerUtil.end(TimerUtil.Type.GENERIC, "generated prime");
        }
        System.out.println("Average " + bits + " bits: " + Maths.average(times) + "ms");


//        Random random = new Random();
//
//        Mat4f matrix = new Mat4f(random(random, Mat4f.LENGTH, -100.0f, 100.0f));
//
//        Mat4f original = matrix.clone();
//
//        matrix.print();
//
//        for (int i = 0; i < 10000; i++) {
//            matrix.invert().invert();
//            System.out.println(original.clone().subtract(matrix).getDeterminant());
//        }

    }

    private float[] numbers(int start) {
        float[] arr = new float[Mat4f.LENGTH];
        for (int i = 0; i < arr.length; i++) arr[i] = i + start;
        return arr;
    }

    private float[] random(Random random, int length, float start, float end) {
        float[] arr = new float[length];
        for (int i = 0; i < arr.length; i++) arr[i] = start + (end - start) * random.nextFloat();
        return arr;
    }

    private void printArray(int[] arr) {
        String s = "{ ";
        if (arr.length != 0) {
            s += arr[0];
            for (int i = 1; i < arr.length; i++) s += ", " + arr[i];
        }
        s += "}";
        System.out.println(s);
    }

    private String printArray(Object[] arr) {
        String s = "{ ";
        if (arr.length != 0) {
            s += arr[0].toString();
            for (int i = 1; i < arr.length; i++) s += ", " + arr[i].toString();
        }
        return s + "}";
    }

    private void test(int[] arr) {
        arr[1] = 10;
    }

    private HelloWorld(String[] args) {
        run(args);
    }

    public static void main(String[] args) {
        new HelloWorld(args);
    }

}
