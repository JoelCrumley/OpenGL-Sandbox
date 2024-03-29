package joel.opengl.maths.security;

import com.theromus.sha.Keccak;
import com.theromus.sha.Parameters;
import com.theromus.utils.HexUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.Future;

public class Cryptography {

    public static final BigInteger RSA_COMMON_EXPONENT = BigInteger.valueOf(65537L);
    public static final int RSA_MIN_BITS = 1024, RSA_MAX_BITS = 2048, RSA_PRIMEGEN_ITERATIONS = 64;

    /*

    https://en.wikipedia.org/wiki/Secure_Hash_Algorithms#Comparison_of_SHA_functions
    https://github.com/romus/sha

     */

    public static final Keccak keccak = new Keccak();
    public static final SecureRandom sr = new SecureRandom();

    public static byte[] stringToBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String bytesToString(byte[] data) {
        return HexUtils.convertBytesToString(data);
    }

    public static byte[] hash(byte[] message, Parameters param) {
        return keccak.getHash(message, param);
    }

    public static byte[] hash(String message, Parameters param) {
        return hash(stringToBytes(message), param);
    }

    public static String hashToString(byte[] message, Parameters param) {
        return bytesToString(hash(message, param));
    }

    public static String hashToString(String message, Parameters param) {
        return bytesToString(hash(stringToBytes(message), param));
    }

    public static boolean equals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) if (a[i] != b[i]) return false;
        return true;
    }

    /**
     * @param q Large prime such that p := 2q + 1 is also prime.
     * @param alpha Primitive root mod p.
     * @param beta Another primitive root mod p.
     * @param message 0 <= m <= q^2
     * @return discrete log hash of message, h(message), with 0 < h(m) < p = 2q + 1.
     */
    public static BigInteger discreteLogHash(BigInteger q, BigInteger alpha, BigInteger beta, BigInteger message) {
        /*
        Discrete Log Hash:
        Let q be large prime : p = 2q + 1 is also prime. Let alpha and beta be two distinct primitive roots mod p.
        Message m : 0 <= m <= q^2
        Given m = x_0 + x_1 * q with 0 <= x_0, x_1 <= q - 1
        h(m) = alpha ^ x_0 * beta ^ x_1 mod p with 0 < h(m) < p
         */
        BigInteger[] x = message.divideAndRemainder(q); // x[0] = divided = x_1 above, x[1] = remainder = x_0 above
        BigInteger p = q.multiply(BigInteger.TWO).add(BigInteger.ONE);
        return alpha.modPow(x[1], p).multiply(beta.modPow(x[0], p)).mod(p);
    }

    public static RSAContainer generateRSAKey() {
        RSAContainer rsa = new RSAContainer();

        rsa.p = PrimeGenerator.generate(RSA_MIN_BITS, RSA_MAX_BITS, RSA_PRIMEGEN_ITERATIONS);
        rsa.q = PrimeGenerator.generate(RSA_MIN_BITS, RSA_MAX_BITS, RSA_PRIMEGEN_ITERATIONS);
        rsa.key.modulus = rsa.p.multiply(rsa.q);
        rsa.phi = rsa.p.subtract(BigInteger.ONE).multiply(rsa.q.subtract(BigInteger.ONE));
        rsa.key.exponent = RSA_COMMON_EXPONENT;

        while (rsa.key.exponent.gcd(rsa.phi).compareTo(BigInteger.ONE) != 0) { // Extremely unlikely
            rsa.key.exponent = BigInteger.valueOf(PrimeGenerator.first1000Primes[50 + sr.nextInt(900)]);
        }

        rsa.d = rsa.key.exponent.modInverse(rsa.phi);

        return rsa;
    }

    public static BigInteger encrypt(BigInteger message, RSAPublicKey key) {
        return message.modPow(key.exponent, key.modulus);
    }

    public static BigInteger decryptRSA(BigInteger text, BigInteger modulus, BigInteger decryption) {
        return text.modPow(decryption, modulus);
    }

    private static final int START_CHARACTER = 33, END_CHARACTER = 126, CHARACTERS = END_CHARACTER - START_CHARACTER + 1;
    private static final BigInteger base = BigInteger.valueOf(CHARACTERS);

    // 94^x = 2^2048 gives solution of x=312.45 so worst case our rsa modulus can support 312 characters.
    public static final int MAX_WORD_LENGTH = 300;

    public static BigInteger encodeWord(String word) {
        if (word == null) return null;
        BigInteger encoded = BigInteger.ZERO;
        BigInteger unit = BigInteger.ONE;

        char[] chars = word.toCharArray();
        if (chars.length > MAX_WORD_LENGTH) return null;
        for (int i = 0; i < chars.length; i++) {
            int character = chars[i];
            if (character < START_CHARACTER || character > END_CHARACTER) return null;
            BigInteger id = BigInteger.valueOf(character - START_CHARACTER);
            encoded = encoded.add(id.multiply(unit));
            unit = unit.multiply(base);
        }

        return encoded;
    }

    public static String decodeWord(BigInteger encoded) {
        String s = "";

        BigInteger[] dr = encoded.divideAndRemainder(base);
        while (dr[1].compareTo(BigInteger.ZERO) != 0) {
            s += (char) (dr[1].intValue() + START_CHARACTER);
            dr = dr[0].divideAndRemainder(base);
        }

        return s;
    }

}
