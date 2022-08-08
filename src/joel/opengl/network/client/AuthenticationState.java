package joel.opengl.network.client;

import joel.opengl.maths.security.Cryptography;
import joel.opengl.maths.security.RSAPublicKey;
import joel.opengl.network.packets.LoginRequestPacket;
import joel.opengl.network.packets.RSARequestPacket;
import joel.opengl.network.packets.RegisterRequestPacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

public class AuthenticationState extends ClientState {

    public static final int TICK_RATE = 5;

    public AuthenticationState(Client client) {
        super(client, TICK_RATE);
    }

    public RSAPublicKey key;

    public String userName, password;
    public boolean register;
    private boolean waiting;

    public void goToInput() {
        userName = null;
        password = null;
        waiting = false;
    }

    @Override
    public void start() {
        client.connection.send(new RSARequestPacket());
    }

    @Override
    public void tick() {

        if (key == null) return;

        if (userName == null && !waiting) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("Input 'Login' or 'Register'.");
                try {
                    String input = reader.readLine();
                    if (input == null) continue;
                    if (input.equalsIgnoreCase("login")) {
                        register = false;
                        break;
                    } else if (input.equalsIgnoreCase("register")) {
                        register = true;
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            try {
                System.out.println("Input username.");
                userName = reader.readLine();
                System.out.println("Input password.");
                password = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BigInteger encodedPassword = Cryptography.encodeWord(password);

            if (encodedPassword == null || userName == null) {
                goToInput();
                System.out.println("Unsupported characters in password.");
                return;
            }

            BigInteger encryptedPassword = Cryptography.encrypt(encodedPassword, key);

            if (register) {
                client.connection.send(new RegisterRequestPacket(userName, encryptedPassword));
            } else {
                client.connection.send(new LoginRequestPacket(userName, encryptedPassword));
            }

            waiting = true;

        }

    }

    @Override
    public void end() {

    }
}
