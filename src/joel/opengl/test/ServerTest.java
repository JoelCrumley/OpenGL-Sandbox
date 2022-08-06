package joel.opengl.test;

import joel.opengl.network.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerTest {

    public ServerTest() { }

    public void run() {

        int port;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

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

        Server server = new Server(port);

        while (true) { }

    }

}
