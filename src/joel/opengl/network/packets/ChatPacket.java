package joel.opengl.network.packets;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.ChatPacketHandlerI;

import java.util.Optional;

public class ChatPacket extends Packet<ChatPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.CHAT;

    public final Optional<String> sender;
    public final String message;

    public ChatPacket(Optional<String> sender, String message) {
        super(id);
        this.sender = sender;
        this.message = message;
    }

    public ChatPacket(String sender, String message) {
        this(Optional.of(sender), message);
    }

    public ChatPacket(String message) {
        this(Optional.empty(), message);
    }

    public ChatPacket(PacketDataSerializer data) {
        super(id);
        this.sender = data.readOptional(PacketDataSerializer::readUTF);
        this.message = data.readUTF();
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeOptional(sender, PacketDataSerializer::writeUTF);
        data.writeUTF(message);
    }

    @Override
    public void handle(ChatPacketHandlerI handler) {
        handler.handleMessage(this);
    }

}
