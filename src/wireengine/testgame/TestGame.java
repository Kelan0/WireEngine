package wireengine.testgame;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.GameSettings;
import wireengine.core.WireEngine;
import wireengine.core.entity.EntityObject;
import wireengine.core.entity.Player;
import wireengine.core.event.events.TickEvent;
import wireengine.core.level.Level;
import wireengine.core.level.LevelLoader;
import wireengine.core.physics.Tensor;
import wireengine.core.rendering.FrameBuffer;
import wireengine.core.rendering.geometry.MeshData;
import wireengine.core.rendering.geometry.MeshHelper;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.core.rendering.renderer.gui.FontRenderer;
import wireengine.core.rendering.renderer.gui.GuiRenderer;
import wireengine.core.rendering.renderer.world.WorldRenderer;
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
    public static boolean allowRotation = true;
    private static TestGame instance;
    private WorldRenderer worldRenderer;
    private GuiRenderer guiRenderer;
    private String[] gameSettings;
    private Level level;
    private Player player;
    private LevelLoader levelLoader;
    private FontRenderer fontRenderer;

    public TestGame(String[] gameSettings)
    {
        this.gameSettings = gameSettings;
    }

    @Override
    public void preInit()
    {
        GameSettings settings = engine().getGameSettings();
        settings.parse(this.gameSettings);

        FrameBuffer sceneBuffer = new FrameBuffer(settings.getWindowWidth(), settings.getWindowHeight());
        this.worldRenderer = new WorldRenderer(settings.getWindowWidth(), settings.getWindowHeight(), 70.0F, sceneBuffer);
        this.guiRenderer = new GuiRenderer(1, settings.getWindowWidth(), settings.getWindowHeight(), sceneBuffer);
        this.fontRenderer = new FontRenderer(1, settings.getWindowWidth(), settings.getWindowHeight());

        WireEngine.engine().getRenderEngine().addRenderer(this.worldRenderer);
        WireEngine.engine().getRenderEngine().addRenderer(this.guiRenderer);
        WireEngine.engine().getRenderEngine().addRenderer(this.fontRenderer);

        try
        {
            this.levelLoader = new LevelLoader();
            this.level = levelLoader.loadLevel("res/level/level.dat"); //TODO read and write level data to a file.
            this.player = new Player();
            this.level.addPlayer(player);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        this.worldRenderer.addRenderable(this.level);
        WireEngine.engine().getPhysicsEngine().addTickable(this.level);

        Random random = WireEngine.engine().getRandom();

        Vector3f spawnArea = new Vector3f(20.0F, 4.0F, 20.0F);
        for (int i = 0; i < 15; i++)
        {
            Vector3f position = new Vector3f(random.nextFloat() * spawnArea.x - spawnArea.x * 0.5F, random.nextFloat() * spawnArea.y, random.nextFloat() * spawnArea.z - spawnArea.z * 0.5F);

            if (WireEngine.engine().getRandom().nextFloat() < 0.5)
            {
                Quaternion rotation;

                if (allowRotation)
                {
                    rotation = MathUtils.axisAngleToQuaternion(new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()).normalise(null), (float) (random.nextFloat() * Constants.PI * 2.0F), null);
                } else
                {
                    rotation = new Quaternion();
                }

                Vector3f scale = new Vector3f(random.nextFloat() * 2.0F, random.nextFloat() * 2.0F, random.nextFloat() * 2.0F);
                MeshData mesh = MeshHelper.createCuboid(Vector3f.add(scale, new Vector3f(0.5F, 0.5F, 0.5F), null));
                float mass = 1.0F + scale.x * scale.y * scale.z;

                Tensor tensor = new Tensor.Cuboid(mass, scale);

                EntityObject entity = new EntityObject("box" + i, mass, mesh, tensor, new Transformation(position, rotation));
//                entity.physicsObject.applyAngularAcceleration((Vector3f) new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()).normalise(null).scale(random.nextFloat() * 3.0F));
                this.level.addEntity(entity);
            } else
            {
                float radius = random.nextFloat() * 2.0F;
                MeshData mesh = MeshHelper.createIcosphere(random.nextInt(5), radius);

                float mass = (float) (1.0F + (4.0F / 3.0F) * Constants.PI * radius * radius * radius);

                Tensor tensor = new Tensor.Sphere(mass, radius);

                this.level.addEntity(new EntityObject("ball" + i, mass, mesh, tensor, new Transformation(position)));
            }
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

    public Level getLevel()
    {
        return level;
    }

    public Player getPlayer()
    {
        return player;
    }

    public static TestGame getInstance()
    {
        return instance;
    }

    public static void main(String[] args) throws InterruptedException, IOException
    {
        TestGame.instance = new TestGame(args);
        WireEngine.createEngine(TestGame.instance);

//        Random random = new Random(System.nanoTime());
//
//        Vector3f axis = new Vector3f(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).normalise(null);
//        float angle = random.nextFloat() * 10.0F;
//
//        Matrix3f rotation = MathUtils.quaternionToMatrix3f(MathUtils.axisAngleToQuaternion(axis, angle, null), null);
//        Matrix3f tensor = new Tensor.Cuboid(11.25F, new Vector3f(8.0F, 4.0F, 2.0F)).getWorldTensorInv();
//
//        System.out.println("Tensor:\n" + tensor);
//        System.out.println("Axis: " + axis + ", angle: " + angle + "rad");
//
//        Matrix3f r1 = Matrix3f.mul(Matrix3f.mul(rotation, tensor, null),  rotation.transpose(null), null);
//        Matrix3f r2 = Matrix3f.mul(rotation, Matrix3f.mul(tensor,  rotation.transpose(null), null), null);
//
//        System.out.println(r1 + "\n");
//        System.out.println(r2 + "\n");

//        FontData font = FontData.loadFont(new File("res/fonts/arial.fnt"));
//        String str = "test string\nnew line\n\n\na very long line to cause the line to break once the line length exceeds the maximum allowed length.";
//
//        Thread.sleep(100);
//        long a = System.nanoTime();
//        StaticText text = new StaticText(str, font, 3000, false);
//        long b = System.nanoTime();
//
//        System.out.println(((float) (b - a) / 1000000.0F) + "ms to build text");
//        for (Line line : text.getLines())
//        {
//            System.out.println(line);
//        }
    }
}
