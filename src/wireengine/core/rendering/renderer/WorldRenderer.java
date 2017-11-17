package wireengine.core.rendering.renderer;

import org.lwjgl.util.vector.Matrix4f;
import wireengine.core.WireEngine;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.geometry.Mesh;
import wireengine.testgame.Game;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 * @author Kelan
 */
public class WorldRenderer extends Renderer3D
{
    private ShaderProgram worldShader;
    private int polygonMode = GL_FILL;
    private boolean orthographicProjection = false;

    public WorldRenderer(int width, int height, float fov)
    {
        super(0, width, height, fov, 0.05F, 2048.0F);
        this.worldShader = new ShaderProgram();
    }

    @Override
    public void init()
    {
        try
        {
            worldShader.addShader(GL_VERTEX_SHADER, "res/shaders/vertex.glsl");
            worldShader.addShader(GL_FRAGMENT_SHADER, "res/shaders/fragment.glsl");
            worldShader.addAttribute(Mesh.ATTRIBUTE_LOCATION_POSITION, "vertexPosition");
            worldShader.addAttribute(Mesh.ATTRIBUTE_LOCATION_NORMAL, "vertexNormal");
            worldShader.addAttribute(Mesh.ATTRIBUTE_LOCATION_TEXTURE, "vertexTexture");
            worldShader.addAttribute(Mesh.ATTRIBUTE_LOCATION_COLOUR, "vertexColour");

            worldShader.createProgram();

            WireEngine.engine().getGame().getLevel().init();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void render(double delta, double time)
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glPolygonMode(GL_FRONT_AND_BACK, polygonMode);
        createProjection(false);
        worldShader.useProgram(true);
        worldShader.setUniformMatrix4f("projectionMatrix", projectionMatrix);

        Game game = WireEngine.engine().getGame();
        game.getPlayer().render(delta, worldShader);
        if (game.getLevel() != null)
        {
            game.getLevel().render(worldShader, delta, time);
        }

        worldShader.useProgram(false);
    }

    @Override
    public void cleanup()
    {

    }

    public ShaderProgram getShader()
    {
        return worldShader;
    }

    public int getPolygonMode()
    {
        return polygonMode;
    }

    public void setPolygonMode(int polygonMode)
    {
        this.polygonMode = (polygonMode - GL_POINT) % 3 + GL_POINT;
    }

    public void setOrthographicProjection(boolean orthographic)
    {
        this.orthographicProjection = orthographic;
    }

    public boolean isOrthographicProjection()
    {
        return orthographicProjection;
    }
    @Override
    public void createProjection(boolean immediate)
    {
        if (!orthographicProjection)
        {
            super.createProjection(immediate);
        } else
        {

            float aspect = (float) width / (float) height;
            float tangent = (float) (Math.tan(Math.toRadians(fov * 0.5)));

            float right = -1.0F;
            float left = 1.0F;
            float top = 1.0F * aspect;
            float bottom = -1.0F * aspect;

            if (immediate)
            {
                glFrustum(left, right, bottom, top, near, far);
            } else
            {
                if (projectionMatrix == null)
                {
                    projectionMatrix = new Matrix4f();
                }

                projectionMatrix.setZero();
                projectionMatrix.m00 = 2.0F / (right - left);
                projectionMatrix.m11 = 2.0F / (top - bottom);
                projectionMatrix.m22 = -2.0F / (far - near);
                projectionMatrix.m33 = 1.0F;
                projectionMatrix.m30 = -(right + left) / (right - left);
                projectionMatrix.m31 = -(top + bottom) / (top - bottom);
                projectionMatrix.m31 = -(far + near) / (far - near);
            }
        }
    }
}
