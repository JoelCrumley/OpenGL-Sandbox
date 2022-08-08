package joel.opengl.network.client;

public class LoggedInState extends ClientState {

    public static final int TICK_RATE = 5;

    public LoggedInState(Client client) {
        super(client, TICK_RATE);
    }

    private int timer;

    @Override
    public void start() {
        System.out.println("Logged in. Username: " + client.userName);
        timer = 0;
    }

    @Override
    public void tick() {
        timer++;
        if (timer % (4 * TICK_RATE) == 0) {
            System.out.println("still logged in");
            timer = 0;
        }
    }

    @Override
    public void end() {

    }
}
