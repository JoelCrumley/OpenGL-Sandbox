package joel.opengl.test;

import com.theromus.sha.Keccak;
import com.theromus.sha.Parameters;
import com.theromus.utils.HexUtils;
import joel.opengl.maths.Mat4f;
import joel.opengl.maths.Maths;
import joel.opengl.maths.Vec4f;
import joel.opengl.maths.security.Cryptography;
import joel.opengl.maths.security.ModularArithmetic;
import joel.opengl.maths.security.PrimeGenerator;
import joel.opengl.maths.security.RSAContainer;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.util.TimerUtil;
import org.lwjgl.system.CallbackI;

import java.math.BigInteger;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

public class HelloWorld {

    private void run(String[] args) {

        for (int i = 0; i < 65536; i++) System.out.println(i + ": " + ((char)i));

//        int[] primesUpTo1000 = PrimeGenerator.primesUpToN(1000);
//        for (int i = 0; i < primesUpTo1000.length; i++) {
//            System.out.println(primesUpTo1000[i]);
//        }

        
        

//        TimerUtil.start(TimerUtil.Type.R);
//        RSAContainer rsa = Cryptography.generateRSAKey();
//        System.out.println("Modulus:");
//        System.out.println(rsa.key.modulus.toString());
//        System.out.println("\nExponent: " + rsa.key.exponent.toString());
//        TimerUtil.end(TimerUtil.Type.R, "Generated RSA Key");
//
//        String str = "fsankjsnfd";
//        System.out.println(str);
//        BigInteger encoded = Cryptography.encodeWord(str);
//        encoded = Cryptography.encrypt(encoded, rsa.key);
//        encoded = Cryptography.decryptRSA(encoded, rsa.key.modulus, rsa.d);
//        String decoded = Cryptography.decodeWord(encoded);
//        System.out.println(decoded);

//
//        for (int i = 0; i < 100; i++) {
//            TimerUtil.start(TimerUtil.Type.R);
//            int correct = 0, incorrect = 0;
//            for (int j = 0; j < 100; j++) {
//                BigInteger randomMessage = new BigInteger(rsa.key.modulus.bitLength() - 1, Cryptography.sr);
//                BigInteger cipherText = Cryptography.encrypt(randomMessage, rsa.key);
//                BigInteger plainText = Cryptography.decryptRSA(cipherText, rsa.key.modulus, rsa.d);
//                if (plainText.compareTo(randomMessage) != 0) {
//                    print("\nmessage: " + randomMessage.toString());
//                    print("decipher: " + plainText.toString());
//                    incorrect++;
//                } else {
//                    correct++;
//                }
//            }
//            TimerUtil.end(TimerUtil.Type.R, "Correct: " + correct + " Incorrect: " + incorrect);
//        }




//        int primes = 1, bits = 10000;
//        long[] times = new long[primes];
//        for (int i = 0; i < primes; i++) {
//            TimerUtil.start(TimerUtil.Type.GENERIC);
//            System.out.println(PrimeGenerator.generate(bits, 64).toString());
//            times[i] = TimerUtil.getTime(TimerUtil.Type.GENERIC);
//            TimerUtil.end(TimerUtil.Type.GENERIC, "generated prime");
//        }
//        System.out.println("Average " + bits + " bits: " + Maths.average(times) + "ms");



//        TimerUtil.start(TimerUtil.Type.GENERIC);
//        System.out.println(PrimeGenerator.generate(3200, 64).toString());
//        TimerUtil.end(TimerUtil.Type.GENERIC, "generated prime");



//        // https://en.wikipedia.org/wiki/Secure_Hash_Algorithms#Comparison_of_SHA_functions
//        // https://github.com/romus/sha
//        Keccak keccak = new Keccak();
//        byte[] data = "dfjn dsifnisdnfdsnfsknff yregf hfdd".getBytes(StandardCharsets.UTF_8);
//        String hash = HexUtils.convertBytesToString(keccak.getHash(data, Parameters.SHA3_512));
//        System.out.println(hash);



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

    private static void print(String msg) {
        System.out.println(msg);
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
