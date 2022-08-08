package joel.opengl.network.packets;

import joel.opengl.maths.security.RSAPublicKey;
import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.AuthenticationPacketHandlerI;

import java.math.BigInteger;

public class RSAKeyPacket extends Packet<AuthenticationPacketHandlerI> {

    public static EnumPacket id = EnumPacket.RSA_KEY;

    public final BigInteger modulus, exponent;

    public RSAKeyPacket(RSAPublicKey key) {
        this(key.modulus, key.exponent);
    }

    public RSAKeyPacket(BigInteger modulus, BigInteger exponent) {
        super(id);
        this.modulus = modulus;
        this.exponent = exponent;
    }

    public RSAKeyPacket(PacketDataSerializer data) {
        super(id);
        this.modulus = data.readBigInteger();
        this.exponent = data.readBigInteger();
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeBigInteger(modulus);
        data.writeBigInteger(exponent);
    }

    @Override
    public void handle(AuthenticationPacketHandlerI handler) {
        handler.handleRSAPacket(this);
    }
}









