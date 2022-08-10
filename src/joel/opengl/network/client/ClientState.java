package joel.opengl.network.client;

public abstract class ClientState {

    public Client client;
    public int tickRate;
    public double tickTime;

    public ClientState(Client client, int tickRate) {
        this.client = client;
        setTickRate(tickRate);
    }

    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
        tickTime = 1.0d / (double) tickRate;
    }

    abstract public void start();

    /**
     * @param delta time in seconds since last tick
     */
    abstract public void tick(double delta);
    abstract public void end();

}
