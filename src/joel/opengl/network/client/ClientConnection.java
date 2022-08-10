package joel.opengl.network.client;

import joel.opengl.network.EnumPacket;
import joel.opengl.network.Packet;
import joel.opengl.network.PacketDataSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientConnection {

    public ClientConnection(Client client, String host, int port) throws IOException {
        this.client = client;
        this.host = host;
        this.port = port;

        socket = new Socket(host, port);
        output = new DataOutputStream(socket.getOutputStream());
        input = new DataInputStream(socket.getInputStream());
        address = socket.getInetAddress();
        udpSocket = new DatagramSocket();
    }

    private Client client;
    public String host;
    public int port;
    public final Socket socket;
    public final DataInputStream input;
    public final DataOutputStream output;
    public final InetAddress address;
    public final DatagramSocket udpSocket;
    private Thread tcpInThread; // Thread listens for input from socket and passes info on
    private Thread udpInThread;
    private ExecutorService outThread;

    private volatile boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public void send(Packet packet) {

        if (packet.getProtocol() == Packet.Protocol.TCP) {

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

            outThread.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        output.write(bytes);
                        output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else if (packet.getProtocol() == Packet.Protocol.UDP) {

            PacketDataSerializer data = new PacketDataSerializer();
            packet.writeData(data);
            data.trimBuffer();
            byte[] buffer = data.getData();
            int length = buffer.length;
            int id = packet.id.id;
            byte[] bytes = new byte[length + 2];
            bytes[0] = (byte) (id >>> 8);
            bytes[1] = (byte) (id >>> 0);
            for (int i = 0; i < length; i++) bytes[i + 2] = buffer[i];

            outThread.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        DatagramPacket p = new DatagramPacket(bytes, bytes.length, address, port);
                        udpSocket.send(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }

    public void start() {
        running = true;

        outThread = Executors.newSingleThreadExecutor();
        tcpInThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {

                    if (!socket.isBound()) {
                        System.err.println("Socket is unbound for connection");
                        break;
                    } else if (socket.isClosed()) {
                        System.err.println("Socket closed unexpectedly for connection");
                        break;
                    } else if (!socket.isConnected()) {
                        System.err.println("Socket disconnected unexpectedly for connection");
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
//                        System.out.println("Received packet id " + packetID + " dataSize " + dataSize);
                        client.handlePacket(packet);

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
        tcpInThread.start();

        udpInThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int length = 1024;
                byte[] buffer = new byte[length];
                DatagramPacket packet;

                byte[] data;
                while (running) {

                    try {
                        packet = new DatagramPacket(buffer, length);
                        udpSocket.receive(packet);
                        if (!running) break;

                        int packetID = ((packet.getData()[0] & 255) << 8) + (packet.getData()[1] & 255);
                        data = new byte[packet.getLength() - 2];
                        for (int i = 0; i < data.length; i++) data[i] = packet.getData()[i + 2];

                        PacketDataSerializer packetData = new PacketDataSerializer(data);

                        Packet ownPacket = EnumPacket.get(packetID).packet.getDeclaredConstructor(PacketDataSerializer.class).newInstance(packetData);
                        client.handlePacket(ownPacket);
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
        udpInThread.start();

    }

    public boolean isAlive() {
        if (running) return true;
        close();
        return false;
    }

    public void close() {
        System.out.println("Closing connection.");
        running = false;

        outThread.shutdown();

        try {
            tcpInThread.join(2000L);
            udpInThread.join(2000L);
        } catch (InterruptedException e) {
            System.err.println("InterruptedException when joining thread for connection");
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("IOException when closing socket for connection");
            e.printStackTrace();
        }
    }

}










