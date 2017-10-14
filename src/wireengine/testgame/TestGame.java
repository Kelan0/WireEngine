package wireengine.testgame;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.GameSettings;
import wireengine.core.WireEngine;
import wireengine.core.event.events.TickEvent;
import wireengine.core.rendering.renderer.WorldRenderer;
import wireengine.core.util.MathUtils;
import wireengine.core.window.InputHandler;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static wireengine.core.WireEngine.engine;

/**
 * @author Kelan
 */
public class TestGame extends Game
{
    private WorldRenderer worldRenderer;
    private String[] gameSettings;

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

    public static void main(String[] args)
    {
        WireEngine.createEngine(new TestGame(args));

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
