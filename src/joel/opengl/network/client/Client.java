package joel.opengl.network.client;

import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.TestPacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;

public class Client {

    public Client(String host, int port) {
        try {

            System.out.println("Connecting...");
            Socket socket = new Socket(host,port);

            if (!socket.isConnected()) {
                System.out.println("Failed to connect.");
                return;
            }

            System.out.println("Connected.");
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            Random random = new Random();

            byte[] strBytes = new byte[random.nextInt(100) + 50];
            random.nextBytes(strBytes);

            byte[] bytes = new byte[1];
            random.nextBytes(bytes);
            Packet packet = new TestPacket(
                    random.nextInt(),
                    random.nextLong(),
                    bytes[0],
                    random.nextDouble(),
                    random.nextFloat(),
                    random.nextBoolean(),
                    new String(strBytes, StandardCharsets.UTF_8),
                    Optional.of("fdsf"),
                    Optional.empty(),
                    random.nextInt(65000));
            System.out.println("Sending packet:");
            packet.handle(null);
            send(packet, output);
            System.out.println("Sent packet.");
            System.out.println("Disconnecting...");
            socket.close();
            System.out.println("Disconnected.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean send(Packet packet, DataOutputStream output){
        PacketDataSerializer data = new PacketDataSerializer();
        packet.writeData(data);
        data.trimBuffer();
        byte[] buffer = data.getData();
        int length = buffer.length;
        int id = packet.id.id;
        byte[] bytes = new byte[length + 4];
        bytes[0] = (byte) (id >>> 8);
        bytes[1] = (byte) (id >>> 0);
        bytes[2] = (byte) (length >>> 8);
        bytes[3] = (byte) (length >>> 0);
        for (int i = 0; i < length; i++) bytes[i + 4] = buffer[i];

        try {
            output.write(bytes);
            output.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
