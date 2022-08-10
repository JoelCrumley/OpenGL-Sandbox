package joel.opengl.network.client;

import joel.opengl.network.Packet;
import joel.opengl.network.client.packethandlers.AuthenticationPacketHandler;
import joel.opengl.network.client.packethandlers.ChatPacketHandler;
import joel.opengl.network.client.packethandlers.PlayerPacketHandler;
import joel.opengl.network.packets.UDPPortPacket;
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
    private double tickTime; // seconds per tick

    public int id;

    private ClientState currentState;

    public Client() {
        scheduler = new Scheduler();
        registerPacketHandlers();
    }

    public void registerPacketHandlers() {
        packetHandlers.add(new AuthenticationPacketHandler(this));
        packetHandlers.add(new ChatPacketHandler());
        packetHandlers.add(new PlayerPacketHandler(this));
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
        tickTime = (1.0d) / (double)tickRate;
    }

    public boolean start(String host, int port) {
        try {
            connection = new ClientConnection(this, host, port);
            connection.start();
            connection.send(new UDPPortPacket(connection.udpSocket.getLocalPort()));
            changeState(new AuthenticationState(this));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loop() {

        running = true;

        long lastUpdate, lastFrame;
        lastUpdate = lastFrame = System.nanoTime();

        while (running) {

            if (!connection.isAlive()) break;

            if (currentState != null && currentState instanceof RenderingState) {
                RenderingState state = (RenderingState) currentState;
                long now = System.nanoTime();
                double delta = secondsSince(lastFrame, now);
                if (delta > state.getFrameTime()) {
                    state.render(delta);
                    lastFrame = now;
                }
            }

            scheduler.checkScheduledTasks();
            // Skip tasks if too much time has been spent on them this tick.
            while (scheduler.runNextTask()) if (secondsSince(lastUpdate) > tickTime) break;


            long now = System.nanoTime();
            double delta = secondsSince(lastUpdate, now);
            if (delta > tickTime) { // Do update
                if (currentState != null) currentState.tick(delta);
                while (scheduler.runNextTask()) { }

                lastUpdate = now;
            }

            long remaining;
            if (currentState instanceof RenderingState) {
                RenderingState state = (RenderingState) currentState;
                remaining = Math.min(
                        secondsToNano(state.getFrameTime()) - (System.nanoTime() - lastFrame),
                        secondsToNano(tickTime) - (System.nanoTime() - lastUpdate));
            } else {
                remaining = secondsToNano(tickTime) - (System.nanoTime() - lastUpdate);
            }
            if (remaining < 0L) continue;
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

        if (currentState != null) currentState.end();

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

    public void sendPacket(Packet packet) {
        connection.send(packet);
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

    private long secondsToNano(double seconds) {
        return (long) (seconds * (1000.0 * 1000.0 * 1000.0));
    }

    private double nanoToSeconds(long nano) {
        return (double) nano / (1000.0 * 1000.0 * 1000.0);
    }

    private double secondsSince(long sysNanoTime, long start) {
        return nanoToSeconds(start - sysNanoTime);
    }

    private double secondsSince(long sysNanoTime) {
        return secondsSince(sysNanoTime, System.nanoTime());
    }

}
