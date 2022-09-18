package joel.opengl.network.client;

import joel.opengl.maths.Maths;
import joel.opengl.maths.Vec2f;
import joel.opengl.maths.Vec4f;
import joel.opengl.network.Packet;
import joel.opengl.network.packets.*;
import joel.opengl.oldRendering.Quad2D;
import joel.opengl.oldRendering.OldRenderer;
import joel.opengl.window.KeyboardCallback;
import joel.opengl.window.Window;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class TestRenderingState extends RenderingState {

    public TestRenderingState(Client client) {
        super(client, 10);
        this.players = new ArrayList<>();
    }

    private Window window;
    private OldRenderer renderer;
    private TestPlayerShader shader;
    private final ArrayList<Player> players;

    private Player user;
    private Quad2D quad;

    private double lastSecond;
    private int frameCount, updateCount, drawCalls;
    private volatile boolean up, left, down, right, grow, shrink, newColour, positionChanged, sizeChanged;

    @Override
    public void start() {
        window = new Window(1280, 720, "Test Rendering State");
        renderer = new OldRenderer();
        setupCallbacks();
        shader = new TestPlayerShader();
        quad = new Quad2D(new Vec2f(0.0f, 0.0f), new Vec2f(2.0f, 2.0f));

        client.sendPacket(new PlayerDataPacket());
        lastSecond = glfwGetTime();
    }

    @Override
    public void tick(double delta) {

        if (glfwWindowShouldClose(window.id)) {
            client.shutDown();
            return;
        }

//        Vec2f displacement = new Vec2f(0.0f, 0.0f);
//        if (up ^ down) {
//            if (up) displacement.add(0.0f, 1.0f);
//            if (down) displacement.add(0.0f, -1.0f);
//        }
//        if (left ^ right) {
//            if (left) displacement.add(-1.0f, 0.0f);
//            if (right) displacement.add(1.0f, 0.0f);
//        }
//        if ((up ^ down) || (left ^ right)) {
//            user.setTargetPosition((Vec2f) displacement.multiply(Player.SPEED * (float) tickTime).add(user.position), tickTime);
//            client.sendPacket(new PlayerMovePacket(client.id, user.position));
//        }
//
//        if (grow ^ shrink) {
//            if (grow) user.setTargetSize(user.size * (float) (Math.pow(Player.SIZE_SPEED, 1.0d / tickRate)), tickTime);
//            else if (shrink) user.setTargetSize(user.size / (float) (Math.pow(Player.SIZE_SPEED, 1.0d / tickRate)), tickTime);
//            client.sendPacket(new PlayerSizePacket(client.id, user.size));
//        }

        if (positionChanged) client.sendPacket(new PlayerMovePacket(client.id, user.position));

        if (sizeChanged) client.sendPacket(new PlayerSizePacket(client.id, user.size));

        if (newColour) {
            Random random = new Random();
            client.sendPacket(new PlayerColourPacket(client.id, new Vec4f(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1.0f)));
        }

        newColour = positionChanged = sizeChanged = false;

        updateCount++;

        double currentTime = glfwGetTime();

        if (currentTime - lastSecond > 1.0d) {
            lastSecond = currentTime;
            window.setTitle("Test Rendering State (" + frameCount + " FPS, " + updateCount + " UPS, " + drawCalls + " DCs)");
            frameCount = 0;
            updateCount = 0;
        }

    }

    private void preRender(double delta) {
        glfwPollEvents();

        Vec2f displacement = new Vec2f(0.0f, 0.0f);
        if (up ^ down) {
            if (up) displacement.add(0.0f, 1.0f);
            if (down) displacement.add(0.0f, -1.0f);
        }
        if (left ^ right) {
            if (left) displacement.add(-1.0f, 0.0f);
            if (right) displacement.add(1.0f, 0.0f);
        }
        if ((up ^ down) || (left ^ right)) {
            user.setTargetPosition((Vec2f) displacement.multiply(Player.SPEED * delta).add(user.position), delta);
            positionChanged = true;
        }

        if (grow ^ shrink) {
            if (grow) user.setTargetSize(user.size * (float) (Math.pow(Player.SIZE_SPEED, delta)), delta);
            else if (shrink) user.setTargetSize(user.size / (float) (Math.pow(Player.SIZE_SPEED, delta)), delta);
            sizeChanged = true;
        }
    }

    @Override
    public void render(double delta) {
        preRender(delta);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.bind();
        double currentTime = glfwGetTime();
        shader.pushResolution(window.getWidth(), window.getHeight());
        for (Player player : players) {
            Vec2f position = player.getDisplayPosition(currentTime);
            shader.pushTranslation(position.x(), position.y());
            shader.pushSize(player.getDisplaySize(currentTime));
            shader.pushColour(player.colour.x(), player.colour.y(), player.colour.z(), player.colour.w());
            renderer.render(quad);
        }
        shader.unbind();

        glfwSwapBuffers(window.id);

        frameCount++;
        drawCalls = renderer.getDrawCalls();
    }

    @Override
    public void end() {
        window.destroy();
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        client.sendPacket(new PlayerDisconnectPacket(client.id));
    }

    private void setupCallbacks() {
        window.setKeyCallback(GLFW_KEY_W, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                up = action != Action.RELEASE;
            }
        });

        window.setKeyCallback(GLFW_KEY_A, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                left = action != Action.RELEASE;
            }
        });

        window.setKeyCallback(GLFW_KEY_S, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                down = action != Action.RELEASE;
            }
        });

        window.setKeyCallback(GLFW_KEY_D, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                right = action != Action.RELEASE;
            }
        });

        window.setKeyCallback(GLFW_KEY_Q, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                shrink = action != Action.RELEASE;
            }
        });

        window.setKeyCallback(GLFW_KEY_E, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                grow = action != Action.RELEASE;
            }
        });

        window.setKeyCallback(GLFW_KEY_R, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action == Action.PRESS) newColour = true;
            }
        });

        window.setKeyCallback(GLFW_KEY_ESCAPE, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                glfwSetWindowShouldClose(window.id, true);
            }
        });

        window.setKeyCallback(GLFW_KEY_SPACE, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                Packet packet = new PlayerMovePacket(client.id, user.position);
                for (int i = 0; i < 100; i++) client.sendPacket(packet);
            }
        });

    }

    public void setPlayerPosition(int id, Vec2f position) {
        for (Player player : players) {
            if (player.id != id) continue;
            player.setTargetPosition(position, tickTime);
            return;
        }
    }

    public void setPlayerSize(int id, float size) {
        for (Player player : players) {
            if (player.id != id) continue;
            player.setTargetSize(size, tickTime);
            return;
        }
    }

    public void setPlayerColour(int id, Vec4f colour) {
        for (Player player : players) {
            if (player.id != id) continue;
            player.colour = colour;
            return;
        }
    }

    public void setPlayerData(int id, Vec2f position, float size, Vec4f colour) {
        for (Player player : players) {
            if (player.id != id) continue;
            player.setTargetPosition(position, tickTime);
            player.setTargetSize(size, tickTime);
            player.colour = colour;
            return;
        }
        Player player = new Player(id, position, size, colour);
        players.add(player);
        if (id == client.id) user = player;
    }

    public void removePlayer(int id) {
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player player = it.next();
            if (player.id == id) it.remove();
        }
    }

    public class Player {

        public static final float SPEED = 400.0f, MIN_SIZE = 1.0f, MAX_SIZE = 256.0f, SIZE_SPEED = 4.0f;

        public final int id;
        public Vec2f oldPosition, position;
        public float oldSize, size;
        public double positionStartTime, positionEndTime, sizeStartTime, sizeEndTime;
        public Vec4f colour;

        public Player(int id, Vec2f position, float size, Vec4f colour) {
            this.id = id;
            this.position = position.clone();
            this.oldPosition = position.clone();
            positionStartTime = positionEndTime = sizeStartTime = sizeEndTime = glfwGetTime();
            this.oldSize = this.size = size;
            this.colour = colour;
        }

        public void setTargetSize(float size, double travelTime) {
            double now = glfwGetTime();
            size = Maths.clamp(size, MIN_SIZE, MAX_SIZE);
            oldSize = getDisplaySize(now);
            sizeStartTime = now;
            sizeEndTime = sizeStartTime + travelTime;
            this.size = size;
        }

        public float getDisplaySize(double currentTime) {
            if (currentTime > sizeEndTime) {
                return size;
            } else {
                float t = (float) ((currentTime - sizeStartTime) / (sizeEndTime - sizeStartTime));
                return oldSize + t * (size - oldSize);
            }
        }

        public void setTargetPosition(Vec2f pos, double travelTime) {
            double now = glfwGetTime();
            oldPosition = getDisplayPosition(now);
            positionStartTime = now;
            positionEndTime = positionStartTime + travelTime;
            position = pos;
        }

        public Vec2f getDisplayPosition(double currentTime) {
            if (currentTime > positionEndTime) {
                return position;
            } else {
                double t = (currentTime - positionStartTime) / (positionEndTime - positionStartTime);
                return (Vec2f) oldPosition.clone().add(position.clone().subtract(oldPosition).multiply(t));
            }
        }

    }

}
