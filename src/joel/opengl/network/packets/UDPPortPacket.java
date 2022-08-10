package joel.opengl.network.packets;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.AuthenticationPacketHandlerI;

public class UDPPortPacket extends Packet<AuthenticationPacketHandlerI> {
    public static final EnumPacket id = EnumPacket.UDP_PORT;

    public final int port;

    public UDPPortPacket(int port) {
        super(id);
        this.port = port;
    }

    public UDPPortPacket(PacketDataSerializer data) {
        super(id);
        this.port = data.readInt();
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeInt(port);
    }

    @Override
    public void handle(AuthenticationPacketHandlerI handler) {
        handler.handleUDPPort(this);
    }
}
