package wireengine.core.rendering.renderer.gui;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.rendering.FrameBuffer;
import wireengine.core.rendering.geometry.GLMesh;
import wireengine.core.rendering.geometry.MeshData;
import wireengine.core.rendering.geometry.Texture;
import wireengine.core.rendering.renderer.AbstractRenderer;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author Kelan
 */
public class GuiRenderer extends AbstractRenderer
{
    private GLMesh quad;
    private FrameBuffer sceneBuffer;
    private Texture texture;

    public GuiRenderer(int priority, int width, int height, FrameBuffer sceneBuffer)
    {
        super(priority, width, height);
        this.sceneBuffer = sceneBuffer;
    }

    @Override
    public void init()
    {
        MeshData.Vertex v0 = new MeshData.Vertex(new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector2f(0.0F, 0.0F));
        MeshData.Vertex v1 = new MeshData.Vertex(new Vector3f(+1.0F, -1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector2f(1.0F, 0.0F));
        MeshData.Vertex v2 = new MeshData.Vertex(new Vector3f(+1.0F, +1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector2f(1.0F, 1.0F));
        MeshData.Vertex v3 = new MeshData.Vertex(new Vector3f(-1.0F, +1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector2f(0.0F, 1.0F));
        MeshData meshData = new MeshData().addFace(new MeshData.Face4(v0, v1, v2, v3)).compile();
        this.quad = meshData.getRenderableMesh();
        int w = 1024;
        int h = 1024;
        this.texture = new Texture();

        ByteBuffer data = BufferUtils.createByteBuffer(4 * w * h);

        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < h; j++)
            {
                byte r = (byte) 0;
                byte g = (byte) 0;
                byte b = (byte) 0;
                byte a = (byte) 0;
                data.put(r).put(g).put(b).put(a);
            }
        }
        data.flip();

        this.texture.upload(data, 4, w, h);

        try
        {
            this.shader.addShader(GL_VERTEX_SHADER, "res/shaders/gui/vertex.glsl");
            this.shader.addShader(GL_FRAGMENT_SHADER, "res/shaders/gui/fragment.glsl");
            this.shader.addAttribute(GLMesh.ATTRIBUTE_LOCATION_POSITION, "vertexPosition");
            this.shader.addAttribute(GLMesh.ATTRIBUTE_LOCATION_NORMAL, "vertexNormal");
            this.shader.addAttribute(GLMesh.ATTRIBUTE_LOCATION_TEXTURE, "vertexTexture");
            this.shader.addAttribute(GLMesh.ATTRIBUTE_LOCATION_COLOUR, "vertexColour");

            this.shader.createProgram();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void render(double delta, double time)
    {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        this.shader.useProgram(true);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.sceneBuffer.getColourBuffer());

        this.shader.setUniformVector1i("sampler", 0);
        this.quad.draw(this.shader);

        glEnable(GL_DEPTH_TEST);
        glDisable(GL_TEXTURE_2D);

        this.shader.useProgram(false);
    }

    @Override
    public void cleanup()
    {

    }

    @Override
    public void createProjection(boolean immediate)
    {

    }
}
