package joel.opengl.network.packets;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.AuthenticationPacketHandlerI;

public class LoginAcceptPacket extends Packet<AuthenticationPacketHandlerI> {

    public static final EnumPacket id = EnumPacket.LOGIN_ACCEPT;

    private String userName;

    public LoginAcceptPacket(String userName) {
        super(id);
        this.userName = userName;
    }

    public LoginAcceptPacket(PacketDataSerializer data) {
        super(id);
        this.userName = data.readUTF();
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeUTF(userName);
    }

    @Override
    public void handle(AuthenticationPacketHandlerI handler) {
        handler.handleLoginAccept(this);
    }

}
