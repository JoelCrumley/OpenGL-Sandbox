package joel.opengl.network.packets.handlers;

import joel.opengl.network.packets.*;

public interface AuthenticationPacketHandlerI extends PacketHandler {

    void handleRSARequest(RSARequestPacket packet);
    void handleLoginRequest(LoginRequestPacket packet);
    void handleRegisterRequest(RegisterRequestPacket packet);

    void handleRSAPacket(RSAKeyPacket packet);
    void handleLoginRefuse(LoginRefusePacket packet);
    void handleLoginAccept(LoginAcceptPacket packet);

}
