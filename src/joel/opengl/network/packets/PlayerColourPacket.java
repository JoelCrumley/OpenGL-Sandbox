package joel.opengl.network.packets;

import joel.opengl.maths.Vec4f;
import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.PlayerPacketHandlerI;

public class PlayerColourPacket extends Packet<PlayerPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.PLAYER_COLOUR;

    public final int playerID;
    public final Vec4f colour;

    public PlayerColourPacket(int playerID, Vec4f colour) {
        super(id);
        this.playerID = playerID;
        this.colour = colour;
    }

    public PlayerColourPacket(PacketDataSerializer data) {
        super(id);
        this.playerID = data.readInt();
        this.colour = data.readVec4f();
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeInt(playerID);
        data.writeVec4f(colour);
    }

    @Override
    public void handle(PlayerPacketHandlerI handler) {
        handler.handleColour(this);
    }

}
