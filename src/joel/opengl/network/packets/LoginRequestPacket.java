package joel.opengl.network.packets;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.AuthenticationPacketHandlerI;

import java.math.BigInteger;

public class LoginRequestPacket extends Packet<AuthenticationPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.USER_LOGIN;

    public final String userName;
    public final BigInteger password;

    public LoginRequestPacket(String userName, BigInteger password) {
        super(id);
        this.userName = userName;
        this.password = password;
    }

    public LoginRequestPacket(PacketDataSerializer data) {
        super(id);
        this.userName = data.readUTF();
        this.password = data.readBigInteger();
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeUTF(userName);
        data.writeBigInteger(password);
    }

    @Override
    public void handle(AuthenticationPacketHandlerI handler) {
        handler.handleLoginRequest(this);
    }

}
