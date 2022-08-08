package joel.opengl.network.server.packethandlers;

import com.theromus.sha.Parameters;
import joel.opengl.maths.security.Cryptography;
import joel.opengl.maths.security.RSAContainer;
import joel.opengl.network.Packet;
import joel.opengl.network.packets.*;
import joel.opengl.network.packets.handlers.AuthenticationPacketHandlerI;
import joel.opengl.network.server.Connection;
import joel.opengl.network.server.Server;
import joel.opengl.scheduler.ScheduledTask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationPacketHandler implements AuthenticationPacketHandlerI {

    private Server server;
    public AuthenticationPacketHandler(Server server) {
        this.server = server;
    }

    @Override
    public void handleRSARequest(RSARequestPacket packet) {
        Connection source = server.connectionHandler.getConnection(packet.source);
        if (source == null) return;
        source.send(new RSAKeyPacket(server.rsaContainer.key));
    }

    @Override
    public void handleLoginRequest(LoginRequestPacket packet) {

        final Connection source = server.connectionHandler.getConnection(packet.source);
        if (source == null) return;

        RSAContainer rsa = server.rsaContainer;
        String password = Cryptography.decodeWord(Cryptography.decryptRSA(packet.password, rsa.key.modulus, rsa.d));
        final byte[] hashedPassword = Cryptography.hash(password, Parameters.SHA3_512);
        final String userName = packet.userName;

        new ScheduledTask() {
            @Override
            public void run() {

                try {

                    PreparedStatement ps = server.sql.getConnection().prepareStatement("SELECT * FROM `users` WHERE `name`=?");
                    ps.setString(1, userName);
                    ResultSet rs = ps.executeQuery();

                    Packet response;

                    if (rs.next()) {
                        String name = rs.getString("name");
                        byte[] storedPassword = rs.getBytes("password");
                        if (Cryptography.equals(storedPassword, hashedPassword)) {
                            response = new LoginAcceptPacket(name);
                        } else {
                            response = new LoginRefusePacket(LoginRefusePacket.Reason.INCORRECT_PASSWORD);
                        }
                    } else {
                        response = new LoginRefusePacket(LoginRefusePacket.Reason.INCORRECT_USERNAME);
                    }

                    rs.close();
                    ps.close();

                    new ScheduledTask() {
                        @Override
                        public void run() {
                            source.send(response);
                        }
                    }.runTask(server.scheduler);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }.runParallelTask(server.scheduler);

    }

    @Override
    public void handleRegisterRequest(RegisterRequestPacket packet) {

        final Connection source = server.connectionHandler.getConnection(packet.source);
        if (source == null) return;

        RSAContainer rsa = server.rsaContainer;
        String password = Cryptography.decodeWord(Cryptography.decryptRSA(packet.password, rsa.key.modulus, rsa.d));
        final byte[] hashedPassword = Cryptography.hash(password, Parameters.SHA3_512);
        final String userName = packet.userName;

        new ScheduledTask() {
            @Override
            public void run() {

                try {

                    PreparedStatement ps = server.sql.getConnection().prepareStatement("SELECT * FROM `users` WHERE `name`=?");
                    ps.setString(1, userName);
                    ResultSet rs = ps.executeQuery();

                    Packet response;

                    if (rs.next()) {

                        response = new LoginRefusePacket(LoginRefusePacket.Reason.USERNAME_TAKEN);

                        rs.close();
                        ps.close();

                    } else {

                        rs.close();
                        ps.close();
                        ps = server.sql.getConnection().prepareStatement("INSERT INTO `users` (name, password) VALUES (?, ?)");
                        ps.setString(1, userName);
                        ps.setBytes(2, hashedPassword);

                        ps.executeUpdate();
                        ps.close();

                        response = new LoginAcceptPacket(userName);

                    }

                    new ScheduledTask() {
                        @Override
                        public void run() {
                            source.send(response);
                        }
                    }.runTask(server.scheduler);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }.runParallelTask(server.scheduler);

    }

    @Override
    public void handleRSAPacket(RSAKeyPacket packet) {

    }

    @Override
    public void handleLoginRefuse(LoginRefusePacket packet) {

    }

    @Override
    public void handleLoginAccept(LoginAcceptPacket packet) {

    }
}
