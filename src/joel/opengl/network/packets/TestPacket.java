package joel.opengl.network.packets;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.TestPacketHandlerI;

import java.util.Optional;

public class TestPacket extends Packet<TestPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.TEST;

    public final int integer;
    public final Optional<String> optionalString;

    public TestPacket(int integer, Optional<String> optionalString) {
        super(id);
        this.integer = integer;
        this.optionalString = optionalString;
    }

    public TestPacket(PacketDataSerializer data) {
        super(id);
        this.integer = data.readInt();
        this.optionalString = data.readOptional(PacketDataSerializer::readUTF);
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeInt(integer);
        data.writeOptional(optionalString, PacketDataSerializer::writeUTF);
    }

    @Override
    public void handle(TestPacketHandlerI handler) {
        handler.handleTestPacket(this);
    }

}
















