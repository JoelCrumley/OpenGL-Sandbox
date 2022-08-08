package joel.opengl.network.server.packethandlers;

import joel.opengl.network.packets.TestPacket;
import joel.opengl.network.packets.handlers.TestPacketHandlerI;
import joel.opengl.network.server.Server;

public class TestPacketHandler implements TestPacketHandlerI {

    private Server server;
    public TestPacketHandler(Server server) {
        this.server = server;
    }

    @Override
    public void handleTestPacket(TestPacket packet) {
        System.out.println("Test Packet");
        System.out.println("integer:" + packet.getInteger());
        System.out.println("optionalString:" + packet.getOptionalString().toString());
    }

}
