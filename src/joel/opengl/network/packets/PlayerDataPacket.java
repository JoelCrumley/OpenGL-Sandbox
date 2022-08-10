package joel.opengl.network.packets;

import joel.opengl.maths.Vec2f;
import joel.opengl.maths.Vec4f;
import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.PlayerPacketHandlerI;

public class PlayerDataPacket extends Packet<PlayerPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.PLAYER_DATA;

    public final int count;
    public final int[] playerID;
    public final Vec2f[] position;
    public final float[] size;
    public final Vec4f[] colour;

    /**
     * To be sent by client to request data.
     */
    public PlayerDataPacket() {
        super(id);
        count = 0;
        playerID = null;
        position = null;
        size = null;
        colour = null;
    }

    public PlayerDataPacket(int[] playerID, Vec2f[] position, float[] size, Vec4f[] colour) {
        super(id);
        assert playerID.length == position.length && position.length == size.length && size.length == colour.length;
        this.count = playerID.length;
        this.playerID = playerID;
        this.position = position;
        this.size = size;
        this.colour = colour;
    }

    public PlayerDataPacket(PacketDataSerializer data) {
        super(id);
        this.count = data.readInt();

        this.playerID = new int[count];
        this.position = new Vec2f[count];
        this.size = new float[count];
        this.colour = new Vec4f[count];

        for (int i = 0; i < count; i++) {
            playerID[i] = data.readInt();
            position[i] = data.readVec2f();
            size[i] = data.readFloat();
            colour[i] = data.readVec4f();
        }

    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeInt(count);
        for (int i = 0; i < count; i++) {
            data.writeInt(playerID[i]);
            data.writeVec2f(position[i]);
            data.writeFloat(size[i]);
            data.writeVec4f(colour[i]);
        }
    }

    @Override
    public void handle(PlayerPacketHandlerI handler) {
        handler.handleData(this);
    }

}
