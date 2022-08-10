package joel.opengl.network.packets;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.PlayerPacketHandlerI;

public class PlayerDisconnectPacket extends Packet<PlayerPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.PLAYER_DISCONNECT;

    public final int playerID;

    public PlayerDisconnectPacket(int playerID) {
        super(id);
        this.playerID = playerID;
    }

    public PlayerDisconnectPacket(PacketDataSerializer data) {
        super(id);
        this.playerID = data.readInt();
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeInt(playerID);
    }

    @Override
    public void handle(PlayerPacketHandlerI handler) {
        handler.handleDisconnect(this);
    }

}
