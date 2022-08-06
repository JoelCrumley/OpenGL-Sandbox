package joel.opengl.network;

public abstract class Packet<T extends PacketHandler> {

    public final EnumPacket id;

    public Packet(EnumPacket id) {
        this.id = id;
    }

    public abstract void writeData(PacketDataSerializer data);

    public abstract void handle(T t);

}
