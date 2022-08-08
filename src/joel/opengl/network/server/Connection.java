package joel.opengl.network.server;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;
import joel.opengl.network.Profile;
import joel.opengl.scheduler.ScheduledTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketException;

public class Connection {

    public Connection(int id, Socket socket, ConnectionHandler handler) throws IOException {
        this.id = id;
        this.socket = socket;
        this.handler = handler;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
    }

    public final int id;
    public final Socket socket;
    public final DataInputStream input;
    public final DataOutputStream output;
    private final ConnectionHandler handler;
    private Thread thread; // Thread listens for input from socket and passes info on

    public Profile profile;

    private volatile boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public boolean isAuthenticated() {
        return profile != null;
    }

    public boolean send(Packet packet){
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

        System.out.println("Sending packet id " + id + " dataSize " + length + " to connection " + this.id);

        try {
            output.write(bytes);
            output.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void start() {
        running = true;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {

                    if (!socket.isBound()) {
                        System.err.println("Socket is unbound for connection id:" + id);
                        handler.closeConnection(id);
                        break;
                    } else if (socket.isClosed()) {
                        System.err.println("Socket closed unexpectedly for connection id:" + id);
                        handler.closeConnection(id);
                        break;
                    } else if (!socket.isConnected()) {
                        System.err.println("Socket disconnected unexpectedly for connection id:" + id);
                        handler.closeConnection(id);
                        break;
                    }

                    try {

                        byte[] identifier = input.readNBytes(4);
                        if (!running) break;
                        if (identifier.length != 4) continue;
                        int packetID = ((identifier[0] & 255) << 8) + (identifier[1] & 255);
                        int dataSize = ((identifier[2] & 255) << 8) + (identifier[3] & 255);

                        byte[] buffer = input.readNBytes(dataSize);
                        PacketDataSerializer data = new PacketDataSerializer(buffer);

                        Packet packet = EnumPacket.get(packetID).packet.getDeclaredConstructor(PacketDataSerializer.class).newInstance(data);
                        System.out.println("Received packet id " + packetID + " dataSize " + dataSize + " from connection " + id);
                        packet.source = id;
                        handler.server.handlePacket(packet);

                    } catch (SocketException e) {
                        e.printStackTrace();
                        running = false;
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        thread.start();
    }

    public void close() {
        System.out.println("Closing connection id:" + id);
        running = false;

        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println("InterruptedException when joining thread for connection id:" + id);
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("IOException when closing socket for connection id:" + id);
            e.printStackTrace();
        }

        new ScheduledTask() {
            @Override
            public void run() {
                handler.server.broadcastMessage(profile.userName + " has disconnected. There are now " + handler.authenticatedConnections() + " users logged in.");
            }
        }.runTaskLater(handler.server.scheduler, 0.1f);

    }

}
