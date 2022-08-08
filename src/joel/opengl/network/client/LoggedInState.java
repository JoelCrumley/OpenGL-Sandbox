package joel.opengl.network.client;

import joel.opengl.network.packets.ChatPacket;
import joel.opengl.scheduler.ScheduledTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.spi.BreakIteratorProvider;

public class LoggedInState extends ClientState {

    public static final int TICK_RATE = 5;

    public LoggedInState(Client client) {
        super(client, TICK_RATE);
    }

    private ScheduledTask repeatingTask;

    private Thread inputThread;
    private volatile boolean running;

    @Override
    public void start() {
        repeatingTask = new ScheduledTask() {
            @Override
            public void run() {
                System.out.println("Still logged in");
            }
        };
        repeatingTask.runRepeatingTask(client.scheduler, 0L, 60.0f);

        inputThread = new Thread(new Runnable() {
            @Override
            public void run() {

                running = true;

                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (running) {

                    try {
                        String message = reader.readLine();
                        if (message == null) continue;
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
    public void tick() {

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

        repeatingTask.cancel();
    }

}
