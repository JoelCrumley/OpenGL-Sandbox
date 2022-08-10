package joel.opengl.network.packets;

import joel.opengl.maths.Vec2f;
import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.PlayerPacketHandlerI;

public class PlayerMovePacket extends Packet<PlayerPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.PLAYER_MOVE;

    public final int playerID;
    public final Vec2f position;

    public PlayerMovePacket(int playerID, Vec2f position) {
        super(id);
        this.playerID = playerID;
        this.position = position;
    }

    public PlayerMovePacket(PacketDataSerializer data) {
        super(id);
        this.playerID = data.readInt();
        this.position = data.readVec2f();
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeInt(playerID);
        data.writeVec2f(position);
    }

    @Override
    public void handle(PlayerPacketHandlerI handler) {
        handler.handleMove(this);
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.UDP;
    }

}
