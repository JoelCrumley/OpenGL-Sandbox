package joel.opengl.network.packets;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.AuthenticationPacketHandlerI;

public class RSARequestPacket extends Packet<AuthenticationPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.RSA_REQUEST;

    public RSARequestPacket() {
        super(id);
    }

    public RSARequestPacket(PacketDataSerializer data) {
        super(id);
    }

    @Override
    public void writeData(PacketDataSerializer data) {

    }

    @Override
    public void handle(AuthenticationPacketHandlerI handler) {
        handler.handleRSARequest(this);
    }

}














