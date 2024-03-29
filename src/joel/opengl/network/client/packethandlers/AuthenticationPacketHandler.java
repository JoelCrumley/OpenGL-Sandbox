package joel.opengl.network.client.packethandlers;

import joel.opengl.maths.security.RSAPublicKey;
import joel.opengl.network.client.AuthenticationState;
import joel.opengl.network.client.Client;
import joel.opengl.network.client.ChatState;
import joel.opengl.network.packets.*;
import joel.opengl.network.packets.handlers.AuthenticationPacketHandlerI;

public class AuthenticationPacketHandler implements AuthenticationPacketHandlerI {

    private Client client;
    public AuthenticationPacketHandler(Client client) {
        this.client = client;
    }


    @Override
    public void handleRSARequest(RSARequestPacket packet) {

    }

    @Override
    public void handleLoginRequest(LoginRequestPacket packet) {

    }

    @Override
    public void handleRegisterRequest(RegisterRequestPacket packet) {

    }

    @Override
    public void handleUDPPort(UDPPortPacket packet) { }

    @Override
    public void handleRSAPacket(RSAKeyPacket packet) {
        if (!(client.getCurrentState() instanceof AuthenticationState)) return;
        AuthenticationState state = (AuthenticationState) client.getCurrentState();
        state.key = new RSAPublicKey(packet.modulus, packet.exponent);
    }

    @Override
    public void handleLoginRefuse(LoginRefusePacket packet) {
        if (!(client.getCurrentState() instanceof AuthenticationState)) return;
        System.out.println("Login refused, reason: " + packet.reason.name());
        AuthenticationState state = (AuthenticationState) client.getCurrentState();
        state.goToInput();
    }

    @Override
    public void handleLoginAccept(LoginAcceptPacket packet) {
        client.userName = packet.userName;
        client.id = packet.userID;
        client.changeState(new ChatState(client));
    }
}
