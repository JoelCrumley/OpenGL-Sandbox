package joel.opengl.network;

import joel.opengl.network.packets.handlers.PacketHandler;

public abstract class Packet<T extends PacketHandler> {

    public final EnumPacket id;
    public int source; // Only used server-side. Source is connection id of packet source.

    public Packet(EnumPacket id) {
        this.id = id;
    }

    public abstract void writeData(PacketDataSerializer data);

    public abstract void handle(T t);

    public Protocol getProtocol() {
        return Protocol.TCP;
    }

    public enum Protocol {
        UDP, TCP;
    }

}
