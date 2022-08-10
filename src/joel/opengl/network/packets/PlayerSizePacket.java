package joel.opengl.network.packets;

import joel.opengl.maths.Vec2f;
import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.PlayerPacketHandlerI;

public class PlayerSizePacket extends Packet<PlayerPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.PLAYER_SIZE;

    public final int playerID;
    public final float size;

    public PlayerSizePacket(int playerID, float size) {
        super(id);
        this.playerID = playerID;
        this.size = size;
    }

    public PlayerSizePacket(PacketDataSerializer data) {
        super(id);
        this.playerID = data.readInt();
        this.size = data.readFloat();
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeInt(playerID);
        data.writeFloat(size);
    }

    @Override
    public void handle(PlayerPacketHandlerI handler) {
        handler.handleSize(this);
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.UDP;
    }

}
