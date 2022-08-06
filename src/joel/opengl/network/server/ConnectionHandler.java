package joel.opengl.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class ConnectionHandler {

    private final ConnectionHandler instance;

    public ConnectionHandler(Server server, int port) {
        this.instance = this;
        this.server = server;
        this.port = port;
    }

    public final Server server;
    public final int port;
    private final ArrayList<Connection> connections = new ArrayList<>();
    private volatile int connectionCount = 0;
    private Thread thread; // Listens for and handles new connection requests

    private volatile ServerSocket socket;
    private volatile boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public boolean init() {
        try {
            socket = new ServerSocket(port);
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

                    try {
                        Socket userSocket = socket.accept();
                        if (userSocket == null) continue;
                        Connection connection = new Connection(connectionCount++, userSocket, instance);
                        connections.add(connection);
                        connection.start();
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

            }
        });
        thread.start();
    }

    public void closeConnection(int id) {
        Iterator<Connection> it = connections.iterator();
        while (it.hasNext()) {
            Connection connection = it.next();
            if (connection.id == id) {
                connection.close();
                it.remove();
            }
        }
    }

    public void close() {
        running = false;
        Iterator<Connection> it = connections.iterator();
        while (it.hasNext()) {
            Connection connection = it.next();
            connection.close();
            it.remove();
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
