package joel.opengl.network.server.packethandlers;

import joel.opengl.maths.Vec2f;
import joel.opengl.maths.Vec4f;
import joel.opengl.network.packets.*;
import joel.opengl.network.packets.handlers.PlayerPacketHandlerI;
import joel.opengl.network.server.Player;
import joel.opengl.network.server.Server;

import java.util.Iterator;

public class PlayerPacketHandler implements PlayerPacketHandlerI {

    private final Server server;
    public PlayerPacketHandler(Server server) {
        this.server = server;
    }

    @Override
    public void handleMove(PlayerMovePacket packet) {
        Player player = getPlayer(packet.source);
        player.position = packet.position;
        server.connectionHandler.broadcastPacket(new PlayerMovePacket(packet.source, player.position), packet.source);
    }

    @Override
    public void handleSize(PlayerSizePacket packet) {
        Player player = getPlayer(packet.source);
        player.setSize(packet.size);
        server.connectionHandler.broadcastPacket(new PlayerSizePacket(packet.source, player.size), packet.source);
    }

    @Override
    public void handleColour(PlayerColourPacket packet) {
        Player player = getPlayer(packet.source);
        player.colour = packet.colour;
        server.connectionHandler.broadcastPacket(new PlayerColourPacket(packet.source, player.colour));
    }

    @Override
    public void handleData(PlayerDataPacket packet) {
        boolean sourceHasPlayer = false;
        for (Player player : server.players) {
            if (player.id == packet.source) {
                sourceHasPlayer = true;
                break;
            }
        }
        if (!sourceHasPlayer) {
            Player player = new Player(packet.source, new Vec2f(0.0f, 0.0f), Player.DEFAULT_SIZE, new Vec4f(1.0f, 0.0f, 0.0f, 1.0f));
            server.players.add(player);
            PlayerDataPacket dataPacket = new PlayerDataPacket(new int[] { player.id }, new Vec2f[] { player.position }, new float[] { player.size }, new Vec4f[] { player.colour });
            server.connectionHandler.broadcastPacket(dataPacket, player.id);
        }

        int count = server.players.size();
        int[] playerID = new int[count];
        Vec2f[] position = new Vec2f[count];
        float[] size = new float[count];
        Vec4f[] colour = new Vec4f[count];
        int i = 0;
        for (Player player : server.players) {
            playerID[i] = player.id;
            position[i] = player.position;
            size[i] = player.size;
            colour[i] = player.colour;
            i++;
        }

        PlayerDataPacket dataPacket = new PlayerDataPacket(playerID, position, size, colour);
        server.connectionHandler.getConnection(packet.source).send(dataPacket);
    }

    @Override
    public void handleDisconnect(PlayerDisconnectPacket packet) {
        int id = packet.source;
        Iterator<Player> it = server.players.iterator();
        while (it.hasNext()) {
            Player player = it.next();
            if (player.id == id) it.remove();
        }
        server.connectionHandler.broadcastPacket(new PlayerDisconnectPacket(id), id);
        server.connectionHandler.closeConnection(id);
    }

    private Player getPlayer(int id) {
        for (Player player : server.players) if (player.id == id) return player;
        return null;
    }

}
