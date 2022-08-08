package joel.opengl.test;

import joel.opengl.network.client.Client;
import joel.opengl.network.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientTest {

    public ClientTest() { }

    public void run() {

        String host;
        int port;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Input host address.");
            host = reader.readLine();

            while (true) {
                System.out.println("Input port.");

                try {
                    port = Integer.parseInt(reader.readLine());
                    break;
                } catch (NumberFormatException e) {
                    continue;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Client client = new Client();
        if (client.start(host, port)) client.loop();

    }

}
