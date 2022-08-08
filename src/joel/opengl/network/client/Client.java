package joel.opengl.network.client;

import joel.opengl.network.Packet;
import joel.opengl.network.client.packethandlers.AuthenticationPacketHandler;
import joel.opengl.network.packets.handlers.PacketHandler;
import joel.opengl.scheduler.ScheduledTask;
import joel.opengl.scheduler.Scheduler;

import java.io.IOException;
import java.util.ArrayList;

public class Client {

    private volatile boolean running = false, shuttingDown = false;

    public final ArrayList<PacketHandler> packetHandlers = new ArrayList<>();

    public Scheduler scheduler;

    public ClientConnection connection;

    public String userName;

    private int tickRate; // ticks per second
    private long tickTime; // nanoseconds per tick

    private ClientState currentState;

    public Client() {
        scheduler = new Scheduler();
        registerPacketHandlers();
    }

    public void registerPacketHandlers() {
        packetHandlers.add(new AuthenticationPacketHandler(this));
    }

    public ClientState getCurrentState() {
        return currentState;
    }

    public void changeState(ClientState newState) {
        if (currentState != null) currentState.end();
        setTickRate(newState.tickRate);
        currentState = newState;
        newState.start();
        System.out.println("Changed to state " + newState.toString());
    }

    public void setTickRate(int tickRate) {
        if (tickRate < 1) tickRate = 1;
        this.tickRate = tickRate;
        tickTime = (1000L * 1000L * 1000L) / (long)tickRate;
    }

    public boolean start(String host, int port) {
        try {
            connection = new ClientConnection(this, host, port);
            connection.start();
            changeState(new AuthenticationState(this));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loop() {

        running = true;

        while (running) {

            long start = System.nanoTime();

            scheduler.checkScheduledTasks();
            ScheduledTask task;
            while ((task = scheduler.pendingTasks.poll()) != null) {
                task.run();
                if (System.nanoTime() - start > tickTime) break; // Skip tasks if too much time has been spent on them this tick.
            }


            if (currentState != null) currentState.tick();


            long remaining = tickTime - (System.nanoTime() - start);
            if (remaining < 0) continue;
            try {
                Thread.sleep(remaining / 1000000L, (int) (remaining % 1000000L));
            } catch (InterruptedException e) {
                System.err.println("Client thread interrupted.");
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

        // Send disconnect to server if necessary

        try {
            scheduler.close(); // Should be called first to attempt to let scheduled tasks finish.
        } catch (InterruptedException e) {
            System.err.println("Scheduler interrupted while closing.");
            e.printStackTrace();
        }

        System.out.println("Disconnecting...");
        connection.close();
        System.out.println("Disconnected.");

    }

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
