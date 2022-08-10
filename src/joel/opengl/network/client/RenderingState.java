package joel.opengl.network.client;

public abstract class RenderingState extends ClientState {

    public RenderingState(Client client, int tickRate) {
        super(client, tickRate);
    }

    private int targetFramerate;
    private double frameTime;

    public void setTargetFramerate(int fps) {
        targetFramerate = fps;
        frameTime = 1.0d / (double) targetFramerate;
    }

    public double getTargetFramerate() {
        return targetFramerate;
    }

    public double getFrameTime() {
        return frameTime;
    }
    /**
     * @param delta time in seconds since last render call
     */
    public abstract void render(double delta);

}
