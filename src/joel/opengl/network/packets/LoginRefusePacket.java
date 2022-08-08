package joel.opengl.network.packets;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.packets.handlers.AuthenticationPacketHandlerI;

public class LoginRefusePacket extends Packet<AuthenticationPacketHandlerI> {

    public enum Reason {

        INCORRECT_PASSWORD(0),
        INCORRECT_USERNAME(1),
        USERNAME_TAKEN(2);

        private Reason(int id) {
            this.id = id;
        }
        public final int id;

        public static Reason fromID(int id) {
            for (Reason reason : Reason.values()) if (reason.id == id) return reason;
            return null;
        }

    }

    public static final EnumPacket id = EnumPacket.LOGIN_REFUSE;

    public final Reason reason;

    public LoginRefusePacket(Reason reason) {
        super(id);
        this.reason = reason;
    }

    public LoginRefusePacket(PacketDataSerializer data) {
        super(id);
        this.reason = Reason.fromID(data.readUnsignedShort());
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeUnsignedShort(reason.id);
    }

    @Override
    public void handle(AuthenticationPacketHandlerI handler) {
        handler.handleLoginRefuse(this);
    }

}
