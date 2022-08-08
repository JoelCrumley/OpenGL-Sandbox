package joel.opengl.network.client;

public abstract class ClientState {

    public Client client;
    public int tickRate;

    public ClientState(Client client, int tickRate) {
        this.client = client;
        this.tickRate = tickRate;
    }

    abstract public void start();
    abstract public void tick();
    abstract public void end();

}
