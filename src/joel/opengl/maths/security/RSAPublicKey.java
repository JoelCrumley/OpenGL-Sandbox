package joel.opengl.maths.security;

import java.math.BigInteger;

public class RSAPublicKey {

    protected RSAPublicKey() { }

    public RSAPublicKey(BigInteger modulus, BigInteger exponent) {
        this.modulus = modulus;
        this.exponent = exponent;
    }

    public BigInteger modulus, exponent;

}
