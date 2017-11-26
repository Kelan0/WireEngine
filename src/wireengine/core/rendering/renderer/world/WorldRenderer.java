package wireengine.core.rendering.renderer.world;

import org.lwjgl.util.vector.Matrix4f;
import wireengine.core.rendering.FrameBuffer;
import wireengine.core.rendering.IRenderable;
import wireengine.core.rendering.renderer.ShaderProgram;
import wireengine.core.rendering.geometry.GLMesh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author Kelan
 */
public class WorldRenderer extends Renderer3D
{
    private int polygonMode = GL_FILL;
    private boolean orthographicProjection = false;
    private List<IRenderable> renderList = new ArrayList<>();
    private FrameBuffer sceneBuffer;

    public WorldRenderer(int width, int height, float fov, FrameBuffer sceneBuffer)
    {
        super(10, width, height, fov, 0.05F, 2048.0F);
        this.sceneBuffer = sceneBuffer;
    }

    @Override
    public void init()
    {
        try
        {
            this.shader.addShader(GL_VERTEX_SHADER, "res/shaders/world/vertex.glsl");
            this.shader.addShader(GL_FRAGMENT_SHADER, "res/shaders/world/fragment.glsl");
            this.shader.addAttribute(GLMesh.ATTRIBUTE_LOCATION_POSITION, "vertexPosition");
            this.shader.addAttribute(GLMesh.ATTRIBUTE_LOCATION_NORMAL, "vertexNormal");
            this.shader.addAttribute(GLMesh.ATTRIBUTE_LOCATION_TEXTURE, "vertexTexture");
            this.shader.addAttribute(GLMesh.ATTRIBUTE_LOCATION_COLOUR, "vertexColour");

            this.shader.createProgram();

            this.sceneBuffer.init();

            for (IRenderable renderable : this.renderList)
            {
                renderable.initRenderable();
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void render(double delta, double time)
    {
        if (this.sceneBuffer != null)
        {
            this.sceneBuffer.bind(true);
        }

        preRender(delta);

        for (IRenderable renderable : this.renderList)
        {
            renderable.render(delta, this.shader);
        }

        postRender(delta);

        if (this.sceneBuffer != null)
        {
            this.sceneBuffer.bind(false);
        }
    }

    public void preRender(double delta)
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glPolygonMode(GL_FRONT_AND_BACK, polygonMode);
        glEnable(GL_DEPTH_TEST);
        createProjection(false);

        this.shader.useProgram(true);
        this.shader.setUniformMatrix4f("projectionMatrix", projectionMatrix);
        this.shader.setUniformBoolean("useLight", this.polygonMode == GL_FILL);
    }

    public void postRender(double delta)
    {
        this.shader.useProgram(false);
    }

    @Override
    public void cleanup()
    {

    }

    public boolean addRenderable(IRenderable renderable)
    {
        if (renderable == null || this.renderList.contains(renderable))
        {
            return false;
        }

        return this.renderList.add(renderable);
    }

    public ShaderProgram getShader()
    {
        return this.shader;
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
