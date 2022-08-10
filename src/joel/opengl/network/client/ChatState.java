package joel.opengl.network.client;

import joel.opengl.network.packets.ChatPacket;
import joel.opengl.scheduler.ScheduledTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatState extends ClientState {

    public static final int TICK_RATE = 5;

    public ChatState(Client client) {
        super(client, TICK_RATE);
    }

    private Thread inputThread;
    private volatile boolean running;

    @Override
    public void start() {

        inputThread = new Thread(new Runnable() {
            @Override
            public void run() {

                running = true;

                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (running) {

                    try {
                        String message = reader.readLine();
                        if (message == null) continue;
                        if (message.equalsIgnoreCase("secret")) {
                            System.out.println("Starting TestRenderingState");
                            new ScheduledTask() {
                                @Override
                                public void run() {
                                    client.changeState(new TestRenderingState(client));
                                }
                            }.runTask(client.scheduler);
                            break;
                        }
                        client.sendPacket(new ChatPacket(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        inputThread.start();

    }

    @Override
    public void tick(double delta) {

    }

    @Override
    public void end() {

        running = false;
        try {
            inputThread.join();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException when trying to join inputThread");
            e.printStackTrace();
        }

    }

}
