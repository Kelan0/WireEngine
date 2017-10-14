package wireengine.core.window;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.util.vector.Vector2f;
import wireengine.core.WireEngine;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Kelan on 11/04/2016.
 */
public final class InputHandler
{
    private static long window;
    private static final int KEYBOARD_SIZE = 512;
    private static final int MOUSE_SIZE = 16;

    private static int[] keyStates = new int[KEYBOARD_SIZE];
    private static boolean[] activeKeys = new boolean[KEYBOARD_SIZE];

    private static int[] mouseButtonStates = new int[MOUSE_SIZE];
    private static boolean[] activeMouseButtons = new boolean[MOUSE_SIZE];
    private static long lastMouseNS = 0;
    private static long mouseDoubleClickPeriodNS = 1000000000 / 4;

    private static int NO_STATE = -1;

    private static double scrollY;
    private static double scrollX;
    private static boolean grabCursor = false;

    protected static GLFWKeyCallback keyboard = new GLFWKeyCallback()
    {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods)
        {
            activeKeys[key] = action != GLFW_RELEASE;
            keyStates[key] = action;
        }
    };
    protected static GLFWMouseButtonCallback mouse = new GLFWMouseButtonCallback()
    {
        @Override
        public void invoke(long window, int button, int action, int mods)
        {
            activeMouseButtons[button] = action != GLFW_RELEASE;
            mouseButtonStates[button] = action;
        }
    };
    protected static GLFWScrollCallback scrollWheel = new GLFWScrollCallback()
    {
        @Override
        public void invoke(long window, double x, double y)
        {
            scrollX = x;
            scrollY = y;
        }
    };


    protected static void init(long window)
    {
        InputHandler.window = window;

        resetKeyboard();
        resetMouse();
    }

    protected static void update(double delta)
    {
        resetKeyboard();
        resetMouse();

        glfwPollEvents();
        WireEngine.engine().handleInput(delta);

        if (grabCursor)
            glfwSetCursorPos(window, WireEngine.engine().getRenderEngine().getWindow().getWidth() / 2, WireEngine.engine().getRenderEngine().getWindow().getHeight() / 2);
    }

    private static void resetKeyboard()
    {
        for (int i = 0; i < keyStates.length; i++)
        {
            keyStates[i] = NO_STATE;
        }
    }

    private static void resetMouse()
    {
        for (int i = 0; i < mouseButtonStates.length; i++)
        {
            mouseButtonStates[i] = NO_STATE;
        }

        long now = System.nanoTime();

        if (now - lastMouseNS > mouseDoubleClickPeriodNS)
            lastMouseNS = 0;
    }

    public static boolean keyDown(int key)
    {
        return activeKeys[key];
    }

    public static boolean keyPressed(int key)
    {
        return keyStates[key] == GLFW_PRESS;
    }

    public static boolean keyReleased(int key)
    {
        return keyStates[key] == GLFW_RELEASE;
    }

    public static boolean mouseButtonDown(int button)
    {
        return activeMouseButtons[button];
    }

    public static boolean mouseButtonPressed(int button)
    {
        return mouseButtonStates[button] == GLFW_RELEASE;
    }

    public static boolean mouseButtonReleased(int button)
    {
        boolean flag = mouseButtonStates[button] == GLFW_RELEASE;

        if (flag)
            lastMouseNS = System.nanoTime();

        return flag;
    }

    public static boolean mouseButtonDoubleClicked(int button)
    {
        long last = lastMouseNS;
        boolean flag = mouseButtonReleased(button);

        long now = System.nanoTime();

        if (flag && now - last < mouseDoubleClickPeriodNS)
        {
            lastMouseNS = 0;
            return true;
        }

        return false;
    }

    public static Vector2f cursorPosition()
    {
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, x, y);

        return Vector2f.sub(new Vector2f((float) x.get(0), (float) y.get(0)), new Vector2f(WireEngine.engine().getRenderEngine().getWindow().getWidth() / 2, WireEngine.engine().getRenderEngine().getWindow().getHeight() / 2), null);
    }

    public static double getMouseScrollY()
    {
        return scrollY;
    }

    public static double getMouseScrollX()
    {
        return scrollX;
    }

    public static void grabCursor(boolean grabbed)
    {
        if (grabbed && !grabCursor)
        {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
//            glfwSetCursor(window, GLFW_CURSOR_HIDDEN);
        }

        if (!grabbed && grabCursor)
        {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
//            glfwSetCursor(window, GLFW_CURSOR_NORMAL);
        }

        grabCursor = grabbed;
    }

    public static boolean isCursorGrabbed()
    {
        return grabCursor;
    }
}
