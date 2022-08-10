package joel.opengl.network.packets;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.AuthenticationPacketHandlerI;

public class LoginAcceptPacket extends Packet<AuthenticationPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.LOGIN_ACCEPT;

    public final String userName;
    public final int userID;

    public LoginAcceptPacket(String userName, int userID) {
        super(id);
        this.userName = userName;
        this.userID = userID;
    }

    public LoginAcceptPacket(PacketDataSerializer data) {
        super(id);
        this.userName = data.readUTF();
        this.userID = data.readInt();
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeUTF(userName);
        data.writeInt(userID);
    }

    @Override
    public void handle(AuthenticationPacketHandlerI handler) {
        handler.handleLoginAccept(this);
    }

}
