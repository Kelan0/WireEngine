package wireengine.core.rendering.geometry;

import javafx.util.Pair;
import org.lwjgl.BufferUtils;
import wireengine.core.WireEngine;
import wireengine.core.rendering.renderer.ShaderProgram;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static wireengine.core.rendering.geometry.MeshData.*;

/**
 * @author Kelan
 */
public class GLMesh
{
    public static final int ATTRIBUTE_LOCATION_POSITION = 0;
    public static final int ATTRIBUTE_LOCATION_NORMAL = 1;
    public static final int ATTRIBUTE_LOCATION_TEXTURE = 2;
    public static final int ATTRIBUTE_LOCATION_COLOUR = 3;

    public MeshData mesh;
    private int vao;
    private int vertexBuffer;
    private int instanceBuffer;
    private int indexBuffer;
    private List<Integer> attributeLocations = new ArrayList<>();

    public GLMesh(MeshData mesh)
    {
        this.create(mesh.hasNormals, mesh.hasTextures, mesh.hasColours);
        this.compile(mesh);
    }

    public GLMesh()
    {
        this.create(true, true, true);
    }

    public static void upload(Buffer buffer, int bufferTarget, int bufferId, int elementSizeBytes, int reserveElements, int offset, int drawMode)
    {
        glBindBuffer(bufferTarget, bufferId);

        int currentElements = buffer == null ? 0 : buffer.capacity();
        reserveElements = Math.max(reserveElements, currentElements);

        if (reserveElements >= currentElements)
        {
            glBufferData(bufferTarget, reserveElements * elementSizeBytes, drawMode);
        }

        if (currentElements > 0)
        {
            if (buffer instanceof IntBuffer)
                glBufferSubData(bufferTarget, offset, (IntBuffer) buffer);
            if (buffer instanceof FloatBuffer)
                glBufferSubData(bufferTarget, offset, (FloatBuffer) buffer);
        }

        glBindBuffer(bufferTarget, 0);
    }

    public void draw(ShaderProgram shader)
    {
        drawInstanced(shader, -1);
    }

    public void drawInstanced(ShaderProgram shader, int numInstances)
    {
        if (WireEngine.isRenderThread())
        {
            bindBuffers(true);

            for (Map.Entry<Material, List<Pair<Integer, Integer>>> entry : this.mesh.materialRenderMap.entrySet())
            {
                Material material = entry.getKey();

                if (material != null)
                {
                    material.bind(shader);
                    List<Pair<Integer, Integer>> indexList = entry.getValue();

                    for (Pair<Integer, Integer> faces : indexList)
                    {
                        int start = faces.getKey();
                        int end = faces.getValue();

                        if (numInstances < 0)
                        {
                            glDrawElements(GL_TRIANGLES, (end - start) + 1, GL_UNSIGNED_INT, INT_SIZE_BYTES * start);
                        } else
                        {
                            glDrawElementsInstanced(GL_TRIANGLES, (end - start) + 1, GL_UNSIGNED_INT, INT_SIZE_BYTES * start, numInstances);
                        }
                    }
                }
            }

            bindBuffers(false);
        }
    }

    private void bindBuffers(boolean bind)
    {
        if (bind)
        {
            glBindVertexArray(vao);
            for (int attribute : this.attributeLocations)
            {
                glEnableVertexAttribArray(attribute);
            }

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        } else
        {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

            for (int attribute : this.attributeLocations)
            {
                glDisableVertexAttribArray(attribute);
            }
            glBindVertexArray(0);
        }
    }

    public void addInstancedAttribute(int attribute, int numFloats, int dataLength, int offset)
    {
        attribute = Math.max(attribute, 0) + 4;
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, instanceBuffer);

        glVertexAttribPointer(attribute, numFloats, GL_FLOAT, false, dataLength * FLOAT_SIZE_BYTES, offset * FLOAT_SIZE_BYTES);
        glVertexAttribDivisor(attribute, 1);

        this.attributeLocations.add(attribute);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void setInstanceData(FloatBuffer data)
    {
        upload(data, GL_ARRAY_BUFFER, instanceBuffer, FLOAT_SIZE_BYTES, 0, 0, GL_STREAM_DRAW);
    }

    private GLMesh create(boolean normals, boolean textures, boolean colours)
    {
        this.checkThread();
        this.vao = glGenVertexArrays();
        this.vertexBuffer = glGenBuffers();
        this.indexBuffer = glGenBuffers();

        glBindVertexArray(this.vao);

        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBuffer);

        glVertexAttribPointer(ATTRIBUTE_LOCATION_POSITION, FLOATS_PER_POSITION, GL_FLOAT, false, POSITION_STRIDE_BYTES, POSITION_OFFSET_BYTES);
        this.attributeLocations.add(ATTRIBUTE_LOCATION_POSITION);

        if (normals)
        {
            glVertexAttribPointer(ATTRIBUTE_LOCATION_NORMAL, FLOATS_PER_NORMAL, GL_FLOAT, false, NORMAL_STRIDE_BYTES, NORMAL_OFFSET_BYTES);
            this.attributeLocations.add(ATTRIBUTE_LOCATION_NORMAL);
        }

        if (textures)
        {
            glVertexAttribPointer(ATTRIBUTE_LOCATION_TEXTURE, FLOATS_PER_TEXTURE, GL_FLOAT, false, TEXTURE_STRIDE_BYTES, TEXTURE_OFFSET_BYTES);
            this.attributeLocations.add(ATTRIBUTE_LOCATION_TEXTURE);
        }

        if (colours)
        {
            glVertexAttribPointer(ATTRIBUTE_LOCATION_COLOUR, FLOATS_PER_COLOUR, GL_FLOAT, false, COLOUR_STRIDE_BYTES, COLOUR_OFFSET_BYTES);
            this.attributeLocations.add(ATTRIBUTE_LOCATION_COLOUR);
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Reserve data
        this.upload(null, GL_ARRAY_BUFFER, this.vertexBuffer, FLOAT_SIZE_BYTES, 2000, 0, GL_DYNAMIC_DRAW);
        this.upload(null, GL_ELEMENT_ARRAY_BUFFER, this.indexBuffer, INT_SIZE_BYTES, 2000, 0, GL_DYNAMIC_DRAW);
        glBindVertexArray(0);
        return this;
    }

    public GLMesh compile(MeshData mesh)
    {
        this.checkThread();
        this.checkMesh(mesh);

        this.mesh = mesh;
        float[] vertices = new float[mesh.vertexList.size() * VERTEX_SIZE_FLOATS];
        int[] indices = new int[mesh.indexList.size()];

        int counter = 0;
        for (int i = 0; i < mesh.vertexList.size(); i++)
        {
            Vertex vertex = mesh.vertexList.get(i);
            float[] vertexData = vertex.getData();
            for (int j = 0; j < VERTEX_SIZE_FLOATS; j++)
            {
                vertices[counter++] = vertexData[j];
            }
        }

        for (int i = 0; i < mesh.indexList.size(); i++)
        {
            indices[i] = mesh.indexList.get(i);
        }

        FloatBuffer vertexData = (FloatBuffer) BufferUtils.createFloatBuffer(vertices.length).put(vertices).flip();
        IntBuffer indexData = (IntBuffer) BufferUtils.createIntBuffer(indices.length).put(indices).flip();

        glBindVertexArray(this.vao);
        upload(vertexData, GL_ARRAY_BUFFER, vertexBuffer, FLOAT_SIZE_BYTES, 0, 0, GL_DYNAMIC_DRAW);
        upload(indexData, GL_ELEMENT_ARRAY_BUFFER, indexBuffer, INT_SIZE_BYTES, 0, 0, GL_DYNAMIC_DRAW);
        glBindVertexArray(0);

        return this;
    }

    public void cleanup()
    {
        if (WireEngine.isRenderThread())
        {
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            glDeleteBuffers(vertexBuffer);
            glDeleteBuffers(indexBuffer);
            glDeleteVertexArrays(vao);
        }
    }

    public int getVao()
    {
        return vao;
    }

    public int getVertexBuffer()
    {
        return vertexBuffer;
    }

    public int getIndexBuffer()
    {
        return indexBuffer;
    }

    private void checkMesh(MeshData mesh)
    {
        if (mesh == null)
        {
            throw new IllegalArgumentException("Cannot construct and compile OpenGL data from a null mesh");
        }
    }

    private void checkThread()
    {
        if (!WireEngine.isRenderThread())
        {
            throw new IllegalStateException("Cannot construct OpenGL data on a thread without OpenGL context.");
        }
    }
}
