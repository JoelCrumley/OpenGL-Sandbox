package joel.opengl.network.client.packethandlers;

import joel.opengl.network.client.Client;
import joel.opengl.network.client.ClientState;
import joel.opengl.network.client.TestRenderingState;
import joel.opengl.network.packets.*;
import joel.opengl.network.packets.handlers.PlayerPacketHandlerI;

public class PlayerPacketHandler implements PlayerPacketHandlerI {

    private final Client client;

    public PlayerPacketHandler(Client client) {
        this.client = client;
    }

    @Override
    public void handleMove(PlayerMovePacket packet) {
        ClientState currentState = client.getCurrentState();
        if (currentState == null) return;
        if (!(currentState instanceof TestRenderingState)) return;
        ((TestRenderingState) currentState).setPlayerPosition(packet.playerID, packet.position);
    }

    @Override
    public void handleSize(PlayerSizePacket packet) {
        ClientState currentState = client.getCurrentState();
        if (currentState == null) return;
        if (!(currentState instanceof TestRenderingState)) return;
        ((TestRenderingState) currentState).setPlayerSize(packet.playerID, packet.size);
    }

    @Override
    public void handleColour(PlayerColourPacket packet) {
        ClientState currentState = client.getCurrentState();
        if (currentState == null) return;
        if (!(currentState instanceof TestRenderingState)) return;
        ((TestRenderingState) currentState).setPlayerColour(packet.playerID, packet.colour);
    }

    @Override
    public void handleData(PlayerDataPacket packet) {
        ClientState currentState = client.getCurrentState();
        if (currentState == null) return;
        if (!(currentState instanceof TestRenderingState)) return;
        TestRenderingState state = ((TestRenderingState) currentState);
        for (int i = 0; i < packet.count; i++) state.setPlayerData(packet.playerID[i], packet.position[i], packet.size[i], packet.colour[i]);
    }

    @Override
    public void handleDisconnect(PlayerDisconnectPacket packet) {
        ClientState currentState = client.getCurrentState();
        if (currentState == null) return;
        if (!(currentState instanceof TestRenderingState)) return;
        ((TestRenderingState) currentState).removePlayer(packet.playerID);
    }
}
