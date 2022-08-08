package joel.opengl.network.server.packethandlers;

import joel.opengl.network.Profile;
import joel.opengl.network.packets.ChatPacket;
import joel.opengl.network.packets.handlers.ChatPacketHandlerI;
import joel.opengl.network.server.Connection;
import joel.opengl.network.server.Server;

public class ChatPacketHandler implements ChatPacketHandlerI {

    private Server server;
    public ChatPacketHandler(Server server) {
        this.server = server;
    }

    @Override
    public void handleMessage(ChatPacket packet) {
        Connection source = server.connectionHandler.getConnection(packet.source);
        if (!source.isAuthenticated()) return;
        Profile profile = source.profile;
        server.connectionHandler.broadcastPacket(new ChatPacket(profile.userName, packet.message));
    }

}
