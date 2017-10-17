package wireengine.core.rendering.renderer;

import org.lwjgl.opengl.GL;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.WireEngine;
import wireengine.core.level.Level;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.geometry.Mesh;
import wireengine.core.rendering.geometry.MeshHelper;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.testgame.Game;

import java.io.IOException;
import java.util.Random;

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
            GL.createCapabilities();
            worldShader.addShader(GL_VERTEX_SHADER, "res/shaders/vertex.glsl");
            worldShader.addShader(GL_FRAGMENT_SHADER, "res/shaders/fragment.glsl");
            worldShader.addAttribute(Mesh.ATTRIBUTE_LOCATION_POSITION, "vertexPosition");
            worldShader.addAttribute(Mesh.ATTRIBUTE_LOCATION_NORMAL, "vertexNormal");
            worldShader.addAttribute(Mesh.ATTRIBUTE_LOCATION_TEXTURE, "vertexTexture");

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
        game.getPlayer().render(worldShader);
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
}
