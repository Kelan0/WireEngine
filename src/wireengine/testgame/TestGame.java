package wireengine.testgame;

import wireengine.core.GameSettings;
import wireengine.core.WireEngine;
import wireengine.core.event.events.TickEvent;
import wireengine.core.level.Level;
import wireengine.core.level.LevelLoader;
import wireengine.core.rendering.geometry.MeshHelper;
import wireengine.core.rendering.renderer.WorldRenderer;
import wireengine.core.window.InputHandler;

import java.io.IOException;

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
            System.out.println(InputHandler.isCursorGrabbed());
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
//        WireEngine.createEngine(new TestGame(args));

        String line = "map_Kd -s 0.03125 0.03125 placeholder_orange.png";

        String[] split = line.split(" ");

        for (int i = 0; i < split.length; i++)
        {
            System.out.println("split[" + i + "] = " + split[i]);
        }
//        String line = "f 4/2/11 8/1/10 6/6/12";
//
//        int[] indices = new int[9];
//        int n = MeshHelper.readIndices(line, indices);
//
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < indices.length; i++)
//        {
//            sb.append(indices[i]).append(", ");
//        }
//        sb.append("\b\b\n");
//        int pointer = 0;
//        for (int i = 0; i < n; i++)
//        {
//            String type = "unknown";
//
//            if (n == 3)
//            {
//                type = i == 0 ? "vertex" : i == 1 ? "texture" : i == 2 ? "normal" : type;
//            }
//
//            if (n == 2)
//            {
//                type = i == 0 ? "vertex" : i == 1 ? "normal" : type;
//            }
//
//            if (n == 1)
//            {
//                type = i == 0 ? "vertex" : type;
//            }
//
//            sb.append(type + "=[");
//            for (int j = 0; j < 3; j++)
//            {
//                sb.append(indices[pointer++]).append(", ");
//            }
//            sb.append("\b\b]\n");
//        }
//
//        System.out.println(line);
//        System.out.println(sb);










        //testing MathUtils functions

//        Quaternion quat = new Quaternion();
//        quat.setFromAxisAngle(new Vector4f(0.0F, 1.0F, 0.0F, (float) Math.toRadians(45.0F)));
//
//        Matrix4f matrix = new Matrix4f();
//
//        matrix.m03 = matrix.m13 = matrix.m23 = matrix.m33 = matrix.m32 = matrix.m31 = matrix.m30 = 5.0F;
//
//        MathUtils.quaternionToMatrix4f(quat, matrix);
//        float[][] matrixArray = MathUtils.matrix4fToMatrixArray(matrix, null);
//
//        DecimalFormat df = new DecimalFormat("+0.000;-0.000");
//
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < 4; i++)
//        {
//            for (int j = 0; j < 4; j++)
//            {
//                sb.append(df.format(matrixArray[i][j])).append(", ");
//            }
//            sb.append("\n");
//        }
//        System.out.println(sb.toString());

//        Axis axis = new Axis().normalize(null).rotate(quat, null);
//
//        System.out.println(axis);
    }
}
