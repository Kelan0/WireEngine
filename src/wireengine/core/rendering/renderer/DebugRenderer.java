package wireengine.core.rendering.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.rendering.ShaderProgram;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static wireengine.core.rendering.geometry.Mesh.*;

/**
 * @author Kelan
 */
public class DebugRenderer
{
    private static final DebugRenderer instance = new DebugRenderer();

    private int vao;
    private int vbo;
    private int primitive = -1;
    private int numVertices = 0;
    private boolean running = false;
    private List<Float> data = new ArrayList<>();
    private Vector3f currentNormal = new Vector3f();
    private Vector2f currentTexture = new Vector2f();
    private Vector4f currentColour = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F); //If no colour is specified, use white.

    private DebugRenderer()
    {
    }

    public static DebugRenderer getInstance()
    {
        return instance;
    }

    public void init()
    {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glVertexAttribPointer(ATTRIBUTE_LOCATION_POSITION, FLOATS_PER_POSITION, GL_FLOAT, false, POSITION_STRIDE_BYTES, POSITION_OFFSET_BYTES);
        glVertexAttribPointer(ATTRIBUTE_LOCATION_NORMAL, FLOATS_PER_NORMAL, GL_FLOAT, false, NORMAL_STRIDE_BYTES, NORMAL_OFFSET_BYTES);
        glVertexAttribPointer(ATTRIBUTE_LOCATION_TEXTURE, FLOATS_PER_TEXTURE, GL_FLOAT, false, TEXTURE_STRIDE_BYTES, TEXTURE_OFFSET_BYTES);
        glVertexAttribPointer(ATTRIBUTE_LOCATION_COLOUR, FLOATS_PER_COLOUR, GL_FLOAT, false, COLOUR_STRIDE_BYTES, COLOUR_OFFSET_BYTES);

        upload(null, GL_ARRAY_BUFFER, vbo, FLOAT_SIZE_BYTES, this.numVertices, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    private void reset()
    {
        this.primitive = -1;
        this.numVertices = 0;
        this.currentNormal = new Vector3f();
        this.currentTexture = new Vector2f();
        this.currentColour = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.data.clear();
    }

    public void begin(int primitive)
    {
        this.reset();
        this.primitive = primitive;
        this.running = true;
    }

    public void addVertex(Vector3f v)
    {
        this.data.add(v.x);
        this.data.add(v.y);
        this.data.add(v.z);
        this.data.add(currentNormal.x);
        this.data.add(currentNormal.y);
        this.data.add(currentNormal.z);
        this.data.add(currentTexture.x);
        this.data.add(currentTexture.y);
        this.data.add(currentColour.x);
        this.data.add(currentColour.y);
        this.data.add(currentColour.z);
        this.data.add(currentColour.w);
        numVertices++;
    }

    public void addNormal(Vector3f v)
    {
        this.currentNormal.x = v.x;
        this.currentNormal.y = v.y;
        this.currentNormal.z = v.z;
    }

    public void addTexture(Vector2f v)
    {
        this.currentTexture.x = v.x;
        this.currentTexture.y = v.y;
    }

    public void addColour(Vector4f v)
    {
        this.currentColour.x = v.x;
        this.currentColour.y = v.y;
        this.currentColour.z = v.z;
        this.currentColour.w = v.w;
    }

    public void end(ShaderProgram shaderProgram)
    {
        glDisable(GL_TEXTURE_2D);
        shaderProgram.setUniformBoolean("useTexture", false);
        if (this.primitive >= 0 && this.primitive < 10)
        {
            glBindVertexArray(vao);
            glEnableVertexAttribArray(ATTRIBUTE_LOCATION_POSITION);
            glEnableVertexAttribArray(ATTRIBUTE_LOCATION_NORMAL);
            glEnableVertexAttribArray(ATTRIBUTE_LOCATION_TEXTURE);
            glEnableVertexAttribArray(ATTRIBUTE_LOCATION_COLOUR);

            FloatBuffer buffer = this.createBuffer();
            upload(buffer, GL_ARRAY_BUFFER, vbo, FLOAT_SIZE_BYTES, 0, 0);

//            System.out.println("render");
//            while (buffer.hasRemaining())
//            {
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < VERTEX_SIZE_FLOATS; i++)
//                {
//                    sb.append(buffer.get()).append(", ");
//                }
//
//                System.out.println(sb.append("\b\b"));
//            }

            glDrawArrays(this.primitive, 0, numVertices);

            glDisableVertexAttribArray(ATTRIBUTE_LOCATION_POSITION);
            glDisableVertexAttribArray(ATTRIBUTE_LOCATION_NORMAL);
            glDisableVertexAttribArray(ATTRIBUTE_LOCATION_TEXTURE);
            glDisableVertexAttribArray(ATTRIBUTE_LOCATION_COLOUR);
            glBindVertexArray(0);
        }
        glEnable(GL_TEXTURE_2D);

        this.reset();
        this.running = false;
    }

    private FloatBuffer createBuffer()
    {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(this.data.size());

        for (float f : this.data)
        {
            buffer.put(f);
        }

        buffer.flip();
        return buffer;
    }
}
