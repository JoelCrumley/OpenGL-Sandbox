package joel.opengl.network.client.packethandlers;

import joel.opengl.network.packets.ChatPacket;
import joel.opengl.network.packets.handlers.ChatPacketHandlerI;

public class ChatPacketHandler implements ChatPacketHandlerI {

    @Override
    public void handleMessage(ChatPacket packet) {
        String sender = packet.sender.isEmpty() ? "SERVER: " : packet.sender.get() + ": ";
        System.out.println(sender + packet.message);
    }

}
