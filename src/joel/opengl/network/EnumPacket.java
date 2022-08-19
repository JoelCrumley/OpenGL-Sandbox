package joel.opengl.network;

import joel.opengl.network.packets.*;
import joel.opengl.network.packets.handlers.*;

public enum EnumPacket {

    TEST(0, TestPacket.class, TestPacketHandlerI.class),
    RSA_REQUEST(1, RSARequestPacket.class, AuthenticationPacketHandlerI.class),
    RSA_KEY(2, RSAKeyPacket.class, AuthenticationPacketHandlerI.class),
    USER_LOGIN(3, LoginRequestPacket.class, AuthenticationPacketHandlerI.class),
    USER_REGISTER(4, RegisterRequestPacket.class, AuthenticationPacketHandlerI.class),
    LOGIN_REFUSE(5, LoginRefusePacket.class, AuthenticationPacketHandlerI.class),
    LOGIN_ACCEPT(6, LoginAcceptPacket.class, AuthenticationPacketHandlerI.class),
    CHAT(7, ChatPacket.class, ChatPacketHandlerI.class),
    PLAYER_MOVE(8, PlayerMovePacket.class, PlayerPacketHandlerI.class),
    PLAYER_SIZE(9, PlayerSizePacket.class, PlayerPacketHandlerI.class),
    PLAYER_COLOUR(10, PlayerColourPacket.class, PlayerPacketHandlerI.class),
    PLAYER_DATA(11, PlayerDataPacket.class, PlayerPacketHandlerI.class),
    PLAYER_DISCONNECT(12, PlayerDisconnectPacket.class, PlayerPacketHandlerI.class),
    UDP_PORT(13, UDPPortPacket.class, AuthenticationPacketHandlerI.class)
    ;

    public static final int MAX_ID = 65535;
    public static final EnumPacket[] lookup = new EnumPacket[MAX_ID + 1];

    static {
        int duplicate = -1;
        for (EnumPacket packet : EnumPacket.values()) {
            if (lookup[packet.id] != null) {
                duplicate = packet.id;
                break;
            } else {
                lookup[packet.id] = packet;
            }
        }
        if (duplicate != -1) {
            System.err.println("Duplicate ID found in EnumPacket, id:" + duplicate);
            assert (duplicate == -1);
        }
    }

    public static EnumPacket get(int id) {
        return lookup[id];
    }

    EnumPacket(int id, Class<? extends Packet> packet, Class<? extends PacketHandler> handler) {
        this.id = id;
        this.packet = packet;
        this.handler = handler;
    }

    public final int id; // Only two bytes of this number will be used. So id should be in range 0 - 65535
    public final Class<? extends PacketHandler> handler;
    public final Class<? extends Packet> packet;

}
