package joel.opengl.test;

import joel.opengl.entity.EntityHandler;
import joel.opengl.entity.components.TransformComponent;
import joel.opengl.maths.Vec4f;
import joel.opengl.newRendering.ColouredCubeMeshComponent;
import joel.opengl.newRendering.CubeVertex;
import joel.opengl.newRendering.Renderer;
import joel.opengl.window.KeyboardCallback;
import joel.opengl.window.MouseMoveCallback;
import joel.opengl.window.ResizeCallback;
import joel.opengl.window.Window;
import org.lwjgl.opengl.GLUtil;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class Test3D {

    public Test3D() { }

    public void run() {
        init();
        loop();
        cleanUp();
    }

    private EntityHandler entityHandler;
    private Window window;
    private Renderer renderer;

    private int entity;
    private TransformComponent transform;
    private ColouredCubeMeshComponent cube;

    private boolean fullscreen = false;

    private double lastFrame;

    public void init() {
        entityHandler = new EntityHandler();
        window = new Window(1280, 720, "3D Test");
        renderer = new Renderer(window, entityHandler, 0.1f, 100.0f, 90.0f);

        initCallbacks();

        GLUtil.setupDebugMessageCallback();

        {
            renderer.camera.moveBy(0.0f, 0.0f, 2.0f);

            entity = entityHandler.createEntity();
            transform = new TransformComponent(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            cube = new ColouredCubeMeshComponent(renderer, new Vec4f(0.9f, 0.9f, 0.9f, 1.0f));
            cube.setColour(CubeVertex.NEGATIVE_XYZ, new Vec4f(0.0f, 0.0f, 0.0f, 1.0f));
            cube.setColour(CubeVertex.NEGATIVE_YZ_POSITIVE_X, new Vec4f(1.0f, 0.0f, 0.0f, 1.0f));
            cube.setColour(CubeVertex.NEGATIVE_XZ_POSITIVE_Y, new Vec4f(0.0f, 1.0f, 0.0f, 1.0f));
            cube.setColour(CubeVertex.NEGATIVE_XY_POSITIVE_Z, new Vec4f(0.0f, 0.0f, 1.0f, 1.0f));
            cube.setColour(CubeVertex.NEGATIVE_Z_POSITIVE_XY, new Vec4f(1.0f, 1.0f, 0.0f, 1.0f));
            cube.setColour(CubeVertex.NEGATIVE_Y_POSITIVE_XZ, new Vec4f(1.0f, 0.0f, 1.0f, 1.0f));
            cube.setColour(CubeVertex.NEGATIVE_X_POSITIVE_YZ, new Vec4f(0.0f, 1.0f, 1.0f, 1.0f));
            cube.setColour(CubeVertex.POSITIVE_XYZ, new Vec4f(1.0f, 1.0f, 1.0f, 1.0f));
            entityHandler.setComponent(entity, transform, cube);

            entity = entityHandler.createEntity();
            transform = new TransformComponent(3.0f, 0.0f, 0.0f, 0.4f, 0.2f, 0.1f, 0.4f, 4.0f, 1.7f);
            entityHandler.setComponent(entity, transform, cube);
        }

    }

    public void loop() {
        while (!window.shouldClose()) {

            double startFrame = glfwGetTime();
            double delta = startFrame - lastFrame;
            lastFrame = startFrame;

            window.pollEvents();

            doMovement(delta);

            renderer.render();
        }
    }

    private static final float MOVE_SPEED = 2.0f;
    private static final float ROT_SPEED = 1.5f;
    private static final float MOUSE_SENSITIVITY = 0.001f;

    private void doMovement(double delta) {

        transform.rotation.add(0.0f, 0.01f, 0.0f);
        transform.changed = true;

        float moveSpeed = (float) (delta * MOVE_SPEED);
        float rotSpeed = (float) (delta * ROT_SPEED);
        if (window.isKeyDown[GLFW_KEY_D]) renderer.camera.moveRight(moveSpeed);
        if (window.isKeyDown[GLFW_KEY_A]) renderer.camera.moveRight(-moveSpeed);
        if (window.isKeyDown[GLFW_KEY_W]) renderer.camera.moveForward(moveSpeed);
        if (window.isKeyDown[GLFW_KEY_S]) renderer.camera.moveForward(-moveSpeed);
        if (window.isKeyDown[GLFW_KEY_SPACE]) renderer.camera.moveUp(moveSpeed);
        if (window.isKeyDown[GLFW_KEY_LEFT_SHIFT]) renderer.camera.moveUp(-moveSpeed);
        if (window.isKeyDown[GLFW_KEY_E]) renderer.camera.addRoll(-rotSpeed);
        if (window.isKeyDown[GLFW_KEY_Q]) renderer.camera.addRoll(+rotSpeed);
        if (window.isKeyDown[GLFW_KEY_LEFT]) renderer.camera.addYaw(rotSpeed);
        if (window.isKeyDown[GLFW_KEY_RIGHT]) renderer.camera.addYaw(-rotSpeed);
        if (window.isKeyDown[GLFW_KEY_UP]) renderer.camera.addPitch(rotSpeed);
        if (window.isKeyDown[GLFW_KEY_DOWN]) renderer.camera.addPitch(-rotSpeed);

    }

    private void cleanUp() {
        renderer.cleanUp();
        window.destroy();
    }

    private void createRandomEntity(Random random) {
        int entity = entityHandler.createEntity();
        TransformComponent transform =
                new TransformComponent(
                        (random.nextFloat()-0.5f) * 16.0f, (random.nextFloat()-0.5f) * 16.0f, (random.nextFloat()-0.5f) * 16.0f,
                        random.nextFloat() * 6.283f, random.nextFloat() * 6.283f, random.nextFloat() * 6.283f,
                        random.nextFloat() + 0.25f, random.nextFloat() + 0.25f, random.nextFloat() + 0.25f);
        entityHandler.setComponent(entity, transform, cube);
    }

    private void initCallbacks() {

        window.disableCursor();
        window.setMouseMoveCallback(new MouseMoveCallback() {
            @Override
            public void mouseMove(double fromX, double fromY, double toX, double toY) {
                double dx = toX - fromX, dy = toY - fromY;
                renderer.camera.addYaw((float) (-dx * MOUSE_SENSITIVITY)).addPitch((float) (-dy * MOUSE_SENSITIVITY));
            }
        });

        window.setKeyCallback(GLFW_KEY_ESCAPE, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                window.close();
            }
        });

        window.setKeyCallback(GLFW_KEY_F, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                if (fullscreen) {
                    window.setWindowed(1280, 720);
                } else {
                    window.setBorderlessFullscreen();
                }
                fullscreen = !fullscreen;
            }
        });

        window.setResizeCallback(new ResizeCallback() {
            @Override
            public void resize(int width, int height, int oldWidth, int oldHeight) {
                renderer.camera.setAspectRatio(width, height);
                glViewport(0, 0, width, height);
            }
        });
        window.setResizable(true);

        window.setKeyCallback(GLFW_KEY_1, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                Random random = new Random();
                for (int i = 0; i < 10; i++) {
                    createRandomEntity(random);
                }
                System.out.println("Entity count: " + entityHandler.getEntityCount());
            }
        });
        window.setKeyCallback(GLFW_KEY_2, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                Random random = new Random();
                for (int i = 0; i < 100; i++) {
                    createRandomEntity(random);
                }
                System.out.println("Entity count: " + entityHandler.getEntityCount());
            }
        });
        window.setKeyCallback(GLFW_KEY_3, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                Random random = new Random();
                for (int i = 0; i < 1000; i++) {
                    createRandomEntity(random);
                }
                System.out.println("Entity count: " + entityHandler.getEntityCount());
            }
        });
        window.setKeyCallback(GLFW_KEY_4, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                Random random = new Random();
                for (int i = 0; i < 10000; i++) {
                    createRandomEntity(random);
                }
                System.out.println("Entity count: " + entityHandler.getEntityCount());
            }
        });
        window.setKeyCallback(GLFW_KEY_5, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                Random random = new Random();
                for (int i = 0; i < 100000; i++) {
                    createRandomEntity(random);
                }
                System.out.println("Entity count: " + entityHandler.getEntityCount());
            }
        });
    }

    public static void main(String[] args) {
        new Test3D().run();
    }

}
