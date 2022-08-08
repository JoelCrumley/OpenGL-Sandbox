package joel.opengl.network.server;

import joel.opengl.network.Packet;
import joel.opengl.scheduler.ScheduledTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ConnectionHandler {

    private final ConnectionHandler instance;

    public ConnectionHandler(Server server, int port) throws IOException {
        this.instance = this;
        this.server = server;
        this.port = port;

        this.connections = Collections.synchronizedList(new ArrayList<Connection>());

        this.socket = new ServerSocket(port);
    }

    public final Server server;
    public final int port;
    public final List<Connection> connections;
    private volatile int connectionCount = 0;
    private Thread thread; // Listens for and handles new connection requests

    private volatile ServerSocket socket;
    private volatile boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public void start() {
        running = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {

                    try {
                        Socket userSocket = socket.accept();
                        if (server.isShuttingDown()) {
                            socket.close();
                            break;
                        }
                        if (userSocket == null) continue;
                        Connection connection = new Connection(connectionCount++, userSocket, instance);
                        connection.start();

                        synchronized (connections) {
                            connections.add(connection);
                        }

                        System.out.println("Connection established id:" + connection.id);
                    } catch (SocketException e) {
                        System.out.println("Socket exception running:" + running + " closed:" + socket.isClosed() + " bound:" + socket.isBound());
                        if (!running || socket.isClosed() || !socket.isBound()) break;
                        System.err.println("Socket exception not related to server closing.");
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.err.println("IOException when accepting socket.");
                        e.printStackTrace();
                    }

                }
                running = false;

            }
        });
        thread.start();
    }

    public int connections() {
        synchronized (connections) {
            return connections.size();
        }
    }

    public int authenticatedConnections() {
        int i = 0;
        synchronized (connections) {
            for (Connection connection : connections) if (connection.isAuthenticated()) i++;
        }
        return i;
    }

    public void broadcastPacket(Packet packet, boolean requireAuthentication) {
        new ScheduledTask() {
            @Override
            public void run() {

                if (!isRunning()) return;
                synchronized (connections) {
                    for (Connection connection : connections) {
                        if (requireAuthentication && !connection.isAuthenticated()) continue;
                        if (!connection.isRunning()) continue;
                        connection.send(packet);
                    }
                }

            }
        }.runTask(server.scheduler);
    }

    public void broadcastPacket(Packet packet) {
        broadcastPacket(packet, true);
    }

    public Connection getConnection(int id) {
        synchronized (connections) {
            for (Connection connection : connections) if (connection.id == id) return connection;
            return null;
        }
    }

    public void checkConnections() {
        synchronized (connections) {
            Iterator<Connection> it = connections.iterator();
            while (it.hasNext()) {
                Connection connection = it.next();
                if (!connection.isRunning()) {
                    connection.close();
                    it.remove();
                }
            }
        }
    }

    public void closeConnection(int id) {
        synchronized (connections) {
            Iterator<Connection> it = connections.iterator();
            while (it.hasNext()) {
                Connection connection = it.next();
                if (connection.id == id) {
                    connection.close();
                    it.remove();
                }
            }
        }
    }

    public void close() {
        running = false;

        synchronized (connections) {
            Iterator<Connection> it = connections.iterator();
            while (it.hasNext()) {
                Connection connection = it.next();
                connection.close();
                it.remove();
            }
        }

        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println("InterruptedException when joining ConnectionHandler thread.");
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("IOException when closing server socket.");
            e.printStackTrace();
        }
    }

}
