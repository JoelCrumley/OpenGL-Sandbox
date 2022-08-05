package joel.opengl.maths.security;

import java.math.BigInteger;

public class RSAContainer {

    protected RSAContainer() {
        key = new RSAPublicKey();
    }

    public RSAPublicKey key;
    public BigInteger p, q, phi, d;

}
