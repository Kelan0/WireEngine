package wireengine.testgame;

import sun.security.action.GetPropertyAction;
import wireengine.core.GameSettings;
import wireengine.core.WireEngine;
import wireengine.core.event.events.TickEvent;
import wireengine.core.level.Level;
import wireengine.core.level.LevelLoader;
import wireengine.core.rendering.renderer.WorldRenderer;
import wireengine.core.util.StringUtils;
import wireengine.core.window.InputHandler;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static wireengine.core.WireEngine.engine;

/**
 * @author Kelan
 */
public class TestGame extends Game
{
    private WorldRenderer worldRenderer;
    private String[] gameSettings;
    private Level level;
    private LevelLoader levelLoader;

    public TestGame(String[] gameSettings)
    {
        this.gameSettings = gameSettings;
    }

    @Override
    public void preInit()
    {
        GameSettings settings = engine().getGameSettings();
        settings.parse(this.gameSettings);
        this.worldRenderer = new WorldRenderer(settings.getWindowWidth(), settings.getWindowHeight(), 70.0F);

        WireEngine.engine().getRenderEngine().addRenderer(this.worldRenderer);
        try
        {
            this.levelLoader = new LevelLoader();
            this.level = levelLoader.loadLevel("res/level/level.dat");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void postInit()
    {

    }

    @Override
    public void onRender(TickEvent.RenderTickEvent event)
    {
//        worldRenderer.getShader().useProgram(true);
//        testQuad.draw();
//        worldRenderer.getShader().useProgram(false);
    }

    @Override
    public void onPhysics(TickEvent.PhysicsTickEvent event)
    {

    }

    @Override
    public void handleInput(double delta)
    {
        player.handleInput(delta);
        if (InputHandler.keyReleased(GLFW_KEY_ESCAPE))
        {
            InputHandler.grabCursor(!InputHandler.isCursorGrabbed());
        }

        if (InputHandler.keyReleased(GLFW_KEY_F1))
        {
            worldRenderer.setPolygonMode(worldRenderer.getPolygonMode() + 1);
        }
    }

    @Override
    public void cleanup()
    {

    }

    @Override
    public Level getLevel()
    {
        return level;
    }

    public static void main(String[] args)
    {
        WireEngine.createEngine(new TestGame(args));
    }
}
