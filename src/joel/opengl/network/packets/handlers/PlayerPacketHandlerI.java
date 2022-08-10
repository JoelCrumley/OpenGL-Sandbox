package joel.opengl.network.packets.handlers;

import joel.opengl.network.packets.*;

public interface PlayerPacketHandlerI extends PacketHandler {

    void handleMove(PlayerMovePacket packet);
    void handleSize(PlayerSizePacket packet);
    void handleColour(PlayerColourPacket packet);
    void handleData(PlayerDataPacket packet);
    void handleDisconnect(PlayerDisconnectPacket packet);

}
