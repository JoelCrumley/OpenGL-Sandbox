package joel.opengl.window;

import static org.lwjgl.glfw.GLFW.*;

public abstract class KeyboardCallback {

    public enum Action {
        PRESS(GLFW_PRESS), RELEASE(GLFW_RELEASE), REPEAT(GLFW_REPEAT);

        public final int glfwCode;
        private Action(int glfwCode) {
            this.glfwCode = glfwCode;
        }

        public static Action fromGLFW(int code) {
            for (Action action : Action.values()) if (action.glfwCode == code) return action;
            return null;
        }
    }

    public abstract void keyEvent(Action action, boolean shift, boolean control, boolean alt, boolean superMod, boolean capsLock, boolean numLock);

}
