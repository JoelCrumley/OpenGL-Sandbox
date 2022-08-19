package joel.opengl.test;

import joel.opengl.entity.EntityHandler;
import joel.opengl.entity.components.TransformComponent;
import joel.opengl.maths.Vec4f;
import joel.opengl.newRendering.ColouredCubeMeshComponent;
import joel.opengl.newRendering.CubeVertex;
import joel.opengl.newRendering.Renderer;
import joel.opengl.window.KeyboardCallback;
import joel.opengl.window.Window;
import org.lwjgl.opengl.GLUtil;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

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

    public void init() {
        entityHandler = new EntityHandler();
        window = new Window(1280, 720, "3D Test");
        renderer = new Renderer(window, entityHandler, 0.1f, 100.0f, 90.0f);

        initCallbacks();

        GLUtil.setupDebugMessageCallback();


        {
            renderer.camera.position.add(0.0f, 0.0f, 2.0f);

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
            window.pollEvents();

            transform.rotation.add(0.0f, 0.01f, 0.0f);
            transform.changed = true;

            renderer.render();
        }
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
        window.setKeyCallback(GLFW_KEY_Q, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.position.add(-0.5f, 0.0f, 0.0f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_W, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.position.add(0.5f, 0.0f, 0.0f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_A, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.position.add(0.0f, -0.5f, 0.0f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_S, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.position.add(0.0f, 0.5f, 0.0f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_Z, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.position.add(0.0f, 0.0f, -0.5f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_X, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.position.add(0.0f, 0.0f, 0.5f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_E, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.rotation.add(-0.1f, 0.0f, 0.0f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_R, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.rotation.add(0.1f, 0.0f, 0.0f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_D, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.rotation.add(0.0f, -0.1f, 0.0f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_F, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.rotation.add(0.0f, 0.1f, 0.0f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_C, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.rotation.add(0.0f, 0.0f, -0.1f);
                renderer.camera.changed = true;
            }
        });
        window.setKeyCallback(GLFW_KEY_V, new KeyboardCallback() {
            @Override
            public void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock) {
                if (action != Action.PRESS) return;
                renderer.camera.rotation.add(0.0f, 0.0f, 0.1f);
                renderer.camera.changed = true;
            }
        });
    }

    public static void main(String[] args) {
        new Test3D().run();
    }

}
