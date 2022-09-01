package joel.opengl.test;

import joel.opengl.entity.Component;
import joel.opengl.entity.EntityHandler;
import joel.opengl.entity.components.TransformComponent;
import joel.opengl.maths.Quaternion;
import joel.opengl.maths.Vec3f;
import joel.opengl.maths.Vec4f;
import joel.opengl.newRendering.ColouredCubeMeshComponent;
import joel.opengl.newRendering.CubeVertex;
import joel.opengl.newRendering.PlayerControllerComponent;
import joel.opengl.newRendering.Renderer;
import joel.opengl.window.KeyboardCallback;
import joel.opengl.window.MouseMoveCallback;
import joel.opengl.window.ResizeCallback;
import joel.opengl.window.Window;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GLUtil;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;

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

    private int player, rotatingCube, floor;
    private ColouredCubeMeshComponent axisCube;
    private TransformComponent rotatingTransform;
    private PlayerControllerComponent controller;

    private boolean fullscreen = false;

    private double lastFrame;

    public void init() {
        entityHandler = new EntityHandler();
        window = new Window(1280, 720, "3D Test");
        renderer = new Renderer(window, entityHandler, 0.1f, 100.0f, 90.0f);

        System.out.println("GL_MAX_TEXTURE_IMAGE_UNITS: " + glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS));
        System.out.println("GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS: " + glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS));

        initCallbacks();

        GLUtil.setupDebugMessageCallback();

//        pink cube
//        cube.setColour(CubeVertex.NEGATIVE_XYZ, new Vec4f(1.0f, 0.5f, 1.0f, 1.0f));
//        cube.setColour(CubeVertex.NEGATIVE_YZ_POSITIVE_X, new Vec4f(1.0f, 0.6f, 0.7f, 1.0f));
//        cube.setColour(CubeVertex.NEGATIVE_XZ_POSITIVE_Y, new Vec4f(1.0f, 0.2f, 0.9f, 1.0f));
//        cube.setColour(CubeVertex.NEGATIVE_XY_POSITIVE_Z, new Vec4f(1.0f, 0.3f, 0.7f, 1.0f));
//        cube.setColour(CubeVertex.NEGATIVE_Z_POSITIVE_XY, new Vec4f(1.0f, 0.4f, 0.8f, 1.0f));
//        cube.setColour(CubeVertex.NEGATIVE_Y_POSITIVE_XZ, new Vec4f(1.0f, 0.7f, 0.9f, 1.0f));
//        cube.setColour(CubeVertex.NEGATIVE_X_POSITIVE_YZ, new Vec4f(1.0f, 0.1f, 0.7f, 1.0f));
//        cube.setColour(CubeVertex.POSITIVE_XYZ, new Vec4f(1.0f, 0.3f, 1.0f, 1.0f));

        {
            renderer.camera.moveTo(0.0f, 1.0f, 3.0f);
        }

        { // Rotating cube
            rotatingCube = entityHandler.createEntity();
            rotatingTransform = new TransformComponent(3.0f, 2.0f, -3.0f, Quaternion.rotationQuaternion(1.5f, new Vec3f(0.7f, 0.1f, -0.4f)), 0.6f, 0.2f, 2.1f);

            axisCube = new ColouredCubeMeshComponent(renderer, new Vec4f(1.0f, 1.0f, 1.0f, 1.0f));
            axisCube.setColour(CubeVertex.NEGATIVE_XYZ, new Vec4f(0.0f, 0.0f, 0.0f, 1.0f));
            axisCube.setColour(CubeVertex.NEGATIVE_YZ_POSITIVE_X, new Vec4f(1.0f, 0.0f, 0.0f, 1.0f));
            axisCube.setColour(CubeVertex.NEGATIVE_XZ_POSITIVE_Y, new Vec4f(0.0f, 1.0f, 0.0f, 1.0f));
            axisCube.setColour(CubeVertex.NEGATIVE_XY_POSITIVE_Z, new Vec4f(0.0f, 0.0f, 1.0f, 1.0f));
            axisCube.setColour(CubeVertex.NEGATIVE_Z_POSITIVE_XY, new Vec4f(1.0f, 1.0f, 0.0f, 1.0f));
            axisCube.setColour(CubeVertex.NEGATIVE_Y_POSITIVE_XZ, new Vec4f(1.0f, 0.0f, 1.0f, 1.0f));
            axisCube.setColour(CubeVertex.NEGATIVE_X_POSITIVE_YZ, new Vec4f(0.0f, 1.0f, 1.0f, 1.0f));
            axisCube.setColour(CubeVertex.POSITIVE_XYZ, new Vec4f(1.0f, 1.0f, 1.0f, 1.0f));

            entityHandler.setComponent(rotatingCube, rotatingTransform, axisCube);
        }


        {
            floor = entityHandler.createEntity();
            TransformComponent transform = new TransformComponent(0.0f, 0.0f, 0.0f, new Quaternion(), 10.0f, 0.0f, 10.0f);
            ColouredCubeMeshComponent cube = new ColouredCubeMeshComponent(renderer, new Vec4f(0.8f, 0.8f, 0.8f, 1.0f));
            entityHandler.setComponent(floor, transform, cube);
        }

        {
            player = entityHandler.createEntity();
            TransformComponent transform = new TransformComponent(0.0f, 0.5f, 0.0f, new Quaternion(), 0.5f, 0.5f, 0.5f);
            controller = new PlayerControllerComponent(transform, renderer.camera);
            entityHandler.setComponent(player, transform, axisCube, controller);
        }

    }

    public void loop() {
        while (!window.shouldClose()) {

            double startFrame = glfwGetTime();
            double delta = startFrame - lastFrame;
            lastFrame = startFrame;

            window.pollEvents();

            rotatingTransform.rotate(0.01f, new Vec3f(0.0f, 1.0f, 0.0f));

            doMovement(delta);

            renderer.render();
        }
    }

    private void rotateAllTransforms(float angle, Vec3f axis) {

        for (Component component : entityHandler.getBag(TransformComponent.class)) {
            TransformComponent transform = (TransformComponent) component;
            transform.rotate(angle, axis);
        }

    }

    private static float MOVE_SPEED = 2.0f;
    private static final float ROT_SPEED = 1.5f;
    private static final float MOUSE_SENSITIVITY = 0.001f;

    private void doMovement(double delta) {

        float moveSpeed = (float) (delta * MOVE_SPEED);
        float rotSpeed = (float) (delta * ROT_SPEED);
        if (window.isKeyDown[GLFW_KEY_D]) controller.moveRight(moveSpeed);
        if (window.isKeyDown[GLFW_KEY_A]) controller.moveRight(-moveSpeed);
        if (window.isKeyDown[GLFW_KEY_W]) controller.moveForward(moveSpeed);
        if (window.isKeyDown[GLFW_KEY_S]) controller.moveForward(-moveSpeed);
//        if (window.isKeyDown[GLFW_KEY_SPACE]) renderer.camera.moveUp(moveSpeed);
//        if (window.isKeyDown[GLFW_KEY_LEFT_SHIFT]) renderer.camera.moveUp(-moveSpeed);
        if (window.isKeyDown[GLFW_KEY_E]) renderer.camera.addRoll(-rotSpeed);
        if (window.isKeyDown[GLFW_KEY_Q]) renderer.camera.addRoll(+rotSpeed);
        if (window.isKeyDown[GLFW_KEY_LEFT]) controller.addYaw(rotSpeed);
        if (window.isKeyDown[GLFW_KEY_RIGHT]) controller.addYaw(-rotSpeed);
        if (window.isKeyDown[GLFW_KEY_UP]) controller.addPitch(rotSpeed);
        if (window.isKeyDown[GLFW_KEY_DOWN]) controller.addPitch(-rotSpeed);

        controller.forceUpdateCamera();

    }

    private void cleanUp() {
        renderer.cleanUp();
        window.destroy();
    }

    private void createRandomEntity(Random random) {
        int entity = entityHandler.createEntity();
        float distance = 64.0f;
        TransformComponent transform =
                new TransformComponent(
                        (random.nextFloat()-0.5f) * distance, (random.nextFloat()-0.5f) * distance, (random.nextFloat()-0.5f) * distance,
                        Quaternion.rotationQuaternion(random.nextFloat() * 3.1415f, new Vec3f((random.nextFloat()-0.5f), (random.nextFloat()-0.5f), (random.nextFloat()-0.5f))),
                        random.nextFloat() + 0.75f, random.nextFloat() + 0.75f, random.nextFloat() + 0.75f);
        entityHandler.setComponent(entity, transform, axisCube);
    }

    private void initCallbacks() {

        window.disableCursor();
        window.setMouseMoveCallback(new MouseMoveCallback() {
            @Override
            public void mouseMove(double fromX, double fromY, double toX, double toY) {
                if (controller == null) return;
                double dx = toX - fromX, dy = toY - fromY;
                controller.addYaw((float) (-dx * MOUSE_SENSITIVITY)).addPitch((float) (-dy * MOUSE_SENSITIVITY));
            }
        });

        window.setScrollCallback(new GLFWScrollCallback() {
            @Override
            public void invoke(long windowID, double xoffset, double yoffset) {
                if (window.isKeyDown[GLFW_KEY_LEFT_SHIFT]) {
                    Test3D.MOVE_SPEED += (float) (yoffset * 0.25f);
                } else {
                    if (controller == null) return;
                    controller.addZoom((float) (-yoffset * 0.25f));
                }
            }
        });

        window.setKeyCallback(GLFW_KEY_ESCAPE, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                window.close();
            }
        });

        window.setKeyCallback(GLFW_KEY_R, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.setPitch(0.0f).setYaw(0.0f).setRoll(0.0f).moveTo(0.0f, 0.0f, 0.0f);
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
