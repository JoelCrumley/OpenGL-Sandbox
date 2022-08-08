package joel.opengl.network.packets.handlers;

import joel.opengl.network.packets.ChatPacket;

public interface ChatPacketHandlerI extends PacketHandler {

    void handleMessage(ChatPacket packet);

}
