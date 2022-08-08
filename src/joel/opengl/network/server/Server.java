package joel.opengl.network.server;

import joel.opengl.database.SQLiteDatabase;
import joel.opengl.maths.security.Cryptography;
import joel.opengl.maths.security.RSAContainer;
import joel.opengl.network.Packet;
import joel.opengl.network.packets.handlers.PacketHandler;
import joel.opengl.network.server.packethandlers.AuthenticationPacketHandler;
import joel.opengl.network.server.packethandlers.TestPacketHandler;
import joel.opengl.scheduler.ScheduledTask;
import joel.opengl.scheduler.Scheduler;

import java.io.IOException;
import java.util.ArrayList;

public class Server {

    public static final int TICK_RATE = 60; // Ticks per second
    public static final long TICK_TIME = (1000L * 1000L * 1000L) / (long)TICK_RATE; // nanoseconds per tick

    private volatile boolean running = false, shuttingDown = false;

    public final ArrayList<PacketHandler> packetHandlers = new ArrayList<>();

    public ConnectionHandler connectionHandler;
    public Scheduler scheduler;

    public RSAContainer rsaContainer;

    public SQLiteDatabase sql;

    public Server() {
        scheduler = new Scheduler();
        resetRSAKey();
        registerPacketHandlers();
    }

    public void registerPacketHandlers() {
        packetHandlers.add(new TestPacketHandler(this));
        packetHandlers.add(new AuthenticationPacketHandler(this));
    }

    public boolean start(int port) {

        sql = new SQLiteDatabase("SQLiteDatabase.db");

        System.out.println("Starting server...");
        try {
            connectionHandler = new ConnectionHandler(this, port);
            connectionHandler.start();
            System.out.println("Server started on port " + port + ".");
        } catch (IOException e) {
            System.err.println("Failed to start server (port:" + port + ")");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void loop() {

        running = true;

        while (running) {

            long start = System.nanoTime();

            scheduler.checkScheduledTasks();
            ScheduledTask task;
            while ((task = scheduler.pendingTasks.poll()) != null) {
                task.run();
                if (System.nanoTime() - start > TICK_TIME) break; // Skip tasks if too much time has been spent on them this tick.
            }


            // Do server tick


            long remaining = TICK_TIME - (System.nanoTime() - start);
            if (remaining < 0) continue;
            try {
                Thread.sleep(remaining / 1000000L, (int) (remaining % 1000000L));
            } catch (InterruptedException e) {
                System.err.println("Server thread interrupted.");
                e.printStackTrace();
            }

        }

        shutDown();
        cleanUp();

    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public void shutDown() {
        running = false;
        shuttingDown = true;
    }

    public void cleanUp() {

        // Broadcast shutdown to connections

        try {
            scheduler.close(); // Should be called first to attempt to let scheduled tasks finish.
        } catch (InterruptedException e) {
            System.err.println("Scheduler interrupted while closing.");
            e.printStackTrace();
        }

        connectionHandler.close();
        sql.closeConnection();
    }

    public RSAContainer resetRSAKey() {
        rsaContainer = Cryptography.generateRSAKey();
        return rsaContainer;
    }

    /**
     *
     * @param packet
     * @return True if handler could be found to process packet, false otherwise.
     */
    public boolean handlePacket(Packet packet) {
        Class<? extends PacketHandler> packetHandler = packet.id.handler;
        for (PacketHandler handler : packetHandlers) {

            if (!packetHandler.isInstance(handler)) continue;

            new ScheduledTask() {
                @Override
                public void run() {
                    packet.handle(handler);
                }
            }.runTask(scheduler);
            return true;

        }
        return false;
    }

}
