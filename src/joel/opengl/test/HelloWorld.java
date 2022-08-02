package joel.opengl.test;

import joel.opengl.maths.Mat4f;
import joel.opengl.maths.Vec4f;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

public class HelloWorld {

    private void run(String[] args) {

        Random random = new Random();

        Mat4f matrix = new Mat4f(random(random, Mat4f.LENGTH, -100.0f, 100.0f));

        Mat4f original = matrix.clone();

        matrix.print();

        for (int i = 0; i < 100; i++) {
            matrix.invert().invert();
            original.clone().subtract(matrix).print();
        }


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
