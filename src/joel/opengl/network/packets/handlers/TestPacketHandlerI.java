package joel.opengl.network.packets.handlers;

import joel.opengl.network.packets.TestPacket;

public interface TestPacketHandlerI extends PacketHandler {

    void handleTestPacket(TestPacket packet);

}
