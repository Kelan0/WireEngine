package wireengine.core.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import wireengine.core.GameSettings;
import wireengine.core.WireEngine;

import static wireengine.core.WireEngine.engine;

/**
 * @author Kelan
 */
public class Window
{
    private long windowId;
    private int width;
    private int height;

    public void init()
    {
//        GLFWErrorCallback.createPrint(System.err);

        WireEngine.getLogger().info("Initializing GLFW");
        if (!GLFW.glfwInit())
        {
            throw new RuntimeException("Failed to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);

        WireEngine.getLogger().info("Creating window");
        GameSettings gameSettings = engine().getGameSettings();
        this.width = gameSettings.getWindowWidth();
        this.height = gameSettings.getWindowHeight();
        windowId = GLFW.glfwCreateWindow(width, height, "WireEngine Demo", 0L, 0L);

        if (windowId <= 0)
        {
            throw new RuntimeException("Failed to create window handle");
        }

        GLFW.glfwSetKeyCallback(windowId, InputHandler.keyboard);
        GLFW.glfwSetMouseButtonCallback(windowId, InputHandler.mouse);
        GLFW.glfwSetScrollCallback(windowId, InputHandler.scrollWheel);

        GLFW.glfwMakeContextCurrent(windowId);
        GL.createCapabilities();
        GLFW.glfwSwapInterval(0);

        InputHandler.init(this.windowId);
        WireEngine.getLogger().info("Successfully created window object and initialized OpenGL on " + Thread.currentThread().getName());
    }

    public void update(double delta)
    {
        GL.createCapabilities();
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);

        if (!this.shouldClose())
        {
            GLFW.glfwSwapBuffers(windowId);
            InputHandler.update(delta);
        }
    }

    public void cleanup()
    {
        WireEngine.getLogger().info("Cleaning up window");
//        Callbacks.glfwFreeCallbacks(windowId);

        GLFW.glfwDestroyWindow(windowId);
        GLFW.glfwTerminate();
    }

    public void showWindow(boolean show)
    {
        GLFW.glfwShowWindow(windowId);
    }

    public boolean shouldClose()
    {
        return GLFW.glfwWindowShouldClose(windowId);
    }

    public void closeWindow()
    {
        GLFW.glfwSetWindowShouldClose(windowId, true);
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
