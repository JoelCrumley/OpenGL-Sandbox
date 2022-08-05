package joel.opengl.maths.security;

import java.math.BigInteger;

public class ModularArithmetic {

    // Use BigInteger#modPow
    @Deprecated
    public static BigInteger pow(BigInteger number, BigInteger power, BigInteger modulo) {
        BigInteger a = number;
        BigInteger x = power.testBit(0) ? number : BigInteger.ONE;
        x.modPow(a, x);
        for (int bit = 1; bit < power.bitLength(); bit++) {
            a = a.multiply(a).remainder(modulo);
            if (power.testBit(bit)) x = x.multiply(a).remainder(modulo);
        }
        return x;
    }

}
