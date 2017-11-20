package wireengine.testgame;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.GameSettings;
import wireengine.core.WireEngine;
import wireengine.core.entity.EntityObject;
import wireengine.core.entity.Player;
import wireengine.core.event.events.TickEvent;
import wireengine.core.level.Level;
import wireengine.core.level.LevelLoader;
import wireengine.core.rendering.geometry.MeshBuilder;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.core.rendering.renderer.WorldRenderer;
import wireengine.core.util.Constants;
import wireengine.core.util.MathUtils;
import wireengine.core.window.InputHandler;

import java.io.IOException;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static wireengine.core.WireEngine.engine;

/**
 * @author Kelan
 */
public class TestGame extends Game
{
    private static TestGame instance;
    private WorldRenderer worldRenderer;
    private String[] gameSettings;
    private Level level;
    private Player player;
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
            this.level = levelLoader.loadLevel("res/level/level.dat"); //TODO read and write level data to a file.
            this.player = new Player();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        this.worldRenderer.addRenderable(this.level);
        WireEngine.engine().getPhysicsEngine().addTickable(this.level);

        this.level.addEntity(player);

        Random random = WireEngine.engine().getRandom();

        for (int i = 0; i < 8; i++)
        {
            Vector3f position = new Vector3f(random.nextFloat() * 8.0F, Math.abs(random.nextFloat() * 8.0F), random.nextFloat() * 8.0F);
            Quaternion rotation = MathUtils.axisAngleToQuaternion(new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()).normalise(null), (float) (random.nextFloat() * Constants.PI * 2.0F), null);
            Vector3f scale = new Vector3f(random.nextFloat() + 1.0F, random.nextFloat() + 1.0F, random.nextFloat() + 1.0F);

            MeshBuilder mesh = new MeshBuilder.CuboidBuilder(scale);
            this.level.addEntity(new EntityObject("box" + i, 20.0F, mesh, new Transformation(position, rotation)));
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

    public WorldRenderer getWorldRenderer()
    {
        return worldRenderer;
    }

    public static TestGame getInstance()
    {
        return instance;
    }

    public static void main(String[] args) throws InterruptedException
    {
        TestGame.instance = new TestGame(args);
        WireEngine.createEngine(TestGame.instance);
    }
}
