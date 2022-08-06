package joel.opengl.network.server;

import joel.opengl.network.Packet;
import joel.opengl.network.PacketHandler;
import joel.opengl.network.TestPacketHandler;

public class Server {

    public TestPacketHandler testPacketHandler;
    public ConnectionHandler connectionHandler;

    public Server(int port) {

        testPacketHandler = new TestPacketHandler();
        connectionHandler = new ConnectionHandler(this, port);

        System.out.println("Starting server...");
        connectionHandler.init();
        connectionHandler.start();
        System.out.println("Server started on port " + port + ".");

    }

    public void handlePacket(Packet packet) {
        Class<? extends PacketHandler> handler = packet.id.handler;
        if (handler == TestPacketHandler.class) {
            packet.handle(testPacketHandler);
        } else if (true) {

        }
    }

}
