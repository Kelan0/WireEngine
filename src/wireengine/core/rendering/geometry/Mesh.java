package wireengine.core.rendering.geometry;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.WireEngine;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.util.MathUtils;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Kelan
 */
public class Mesh
{
    public static final int FLOAT_SIZE_BYTES = Float.BYTES;
    public static final int INT_SIZE_BYTES = Integer.BYTES;
    public static final int FLOATS_PER_POSITION = 3;
    public static final int FLOATS_PER_NORMAL = 3;
    public static final int FLOATS_PER_TEXTURE = 2;
    public static final int VERTEX_SIZE_FLOATS = FLOATS_PER_POSITION + FLOATS_PER_NORMAL + FLOATS_PER_TEXTURE;
    public static final int VERTEX_SIZE_BYTES = VERTEX_SIZE_FLOATS * FLOAT_SIZE_BYTES;

    public static final int POSITION_OFFSET_FLOATS = 0;
    public static final int NORMAL_OFFSET_FLOATS = POSITION_OFFSET_FLOATS + FLOATS_PER_POSITION;
    public static final int TEXTURE_OFFSET_FLOATS = NORMAL_OFFSET_FLOATS + FLOATS_PER_NORMAL;
    public static final int POSITION_OFFSET_BYTES = POSITION_OFFSET_FLOATS * FLOAT_SIZE_BYTES;
    public static final int NORMAL_OFFSET_BYTES = NORMAL_OFFSET_FLOATS * FLOAT_SIZE_BYTES;
    public static final int TEXTURE_OFFSET_BYTES = TEXTURE_OFFSET_FLOATS * FLOAT_SIZE_BYTES;
    public static final int POSITION_STRIDE_BYTES = VERTEX_SIZE_BYTES;
    public static final int NORMAL_STRIDE_BYTES = VERTEX_SIZE_BYTES;
    public static final int TEXTURE_STRIDE_BYTES = VERTEX_SIZE_BYTES;

    public static final int ATTRIBUTE_LOCATION_POSITION = 0;
    public static final int ATTRIBUTE_LOCATION_NORMAL = 1;
    public static final int ATTRIBUTE_LOCATION_TEXTURE = 2;

    private int numVertices;
    private int numIndices;

    private int vao;
    private int vbo;
    private int ibo;

    private Vector3f cumulativeVertex;
    private Map<Material, List<Face>> faces = new HashMap<>();
    List<Vertex> vertexList;
    List<Integer> indexList;

    private float epsilon;

    private Mesh(float epsilon)
    {
        this.cumulativeVertex = new Vector3f();
        this.vertexList = new ArrayList<>();
        this.indexList = new ArrayList<>();
        this.epsilon = epsilon;
    }

    public void draw(ShaderProgram shader)
    {
        Set<Material> materials = this.faces.keySet();
        if (materials.iterator().hasNext())
        {
            materials.iterator().next().bind(shader);
        }

        glBindVertexArray(vao);
        glEnableVertexAttribArray(ATTRIBUTE_LOCATION_POSITION);
        glEnableVertexAttribArray(ATTRIBUTE_LOCATION_NORMAL);
        glEnableVertexAttribArray(ATTRIBUTE_LOCATION_TEXTURE);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glDrawElements(GL_TRIANGLES, numIndices, GL_UNSIGNED_INT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(ATTRIBUTE_LOCATION_POSITION);
        glDisableVertexAttribArray(ATTRIBUTE_LOCATION_NORMAL);
        glDisableVertexAttribArray(ATTRIBUTE_LOCATION_TEXTURE);
        glBindVertexArray(0);
    }

    public void cleanup()
    {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
        glDeleteVertexArrays(vao);
    }

    public int getNumVertices()
    {
        return numVertices;
    }

    public int getNumIndices()
    {
        return numIndices;
    }

    public int getVao()
    {
        return vao;
    }

    public int getVbo()
    {
        return vbo;
    }

    public int getIbo()
    {
        return ibo;
    }

    public List<Face> getFaces(Material material)
    {
        if (material != null)
        {
            return this.faces.computeIfAbsent(material, m -> new ArrayList<>());
        }
        return new ArrayList<>();
    }

    public Vector3f getCentre()
    {
        return new Vector3f(cumulativeVertex.x / (float) vertexList.size(), cumulativeVertex.y / (float) vertexList.size(), cumulativeVertex.z / (float) vertexList.size());
    }

    public Mesh transform(Transformation transformation)
    {
        return transform(transformation.getMatrix(null));
    }

    public Mesh transform(Matrix4f matrix)
    {
        if (matrix != null)
        {
            for (Vertex vertex : vertexList)
            {
                Vector4f position = Matrix4f.transform(matrix, new Vector4f(vertex.position.x, vertex.position.y, vertex.position.z, 1.0F), null);

                vertex.position.x = position.x;
                vertex.position.y = position.y;
                vertex.position.z = position.z;
            }
        } else
        {
            WireEngine.getLogger().warning("Cannot transform mesh by null matrix");
        }

        return this;
    }

    public Mesh recentre()
    {
        Vector3f centre = getCentre();

        if (centre.lengthSquared() > epsilon) //If the centre is greater that the allowed distance from [0, 0, 0]
        {
            cumulativeVertex = new Vector3f();
            for (Vertex vertex : vertexList)
            {
                Vector3f.sub(vertex.position, centre, vertex.position);
                Vector3f.add(vertex.position, cumulativeVertex, cumulativeVertex);
            }
        }

        return this;
    }

    public Mesh subdivideFaces(int divisions)
    {
        List<Vertex> vertices = new ArrayList<>(this.vertexList);
        List<Integer> indices = new ArrayList<>(this.indexList);
        this.vertexList.clear();
        this.indexList.clear();

        for (int i = 0; i < getNumIndices(); )
        {
            Vertex v1 = vertices.get(indices.get(i++));
            Vertex v2 = vertices.get(indices.get(i++));
            Vertex v3 = vertices.get(indices.get(i++));

            Vertex v4 = new Vertex((Vector3f) Vector3f.add(v1.position, v2.position, null).scale(0.5F), new Vector3f(), new Vector2f());
            Vertex v5 = new Vertex((Vector3f) Vector3f.add(v2.position, v3.position, null).scale(0.5F), new Vector3f(), new Vector2f());
            Vertex v6 = new Vertex((Vector3f) Vector3f.add(v3.position, v1.position, null).scale(0.5F), new Vector3f(), new Vector2f());

            this.addFace(new Face3(v1, v4, v6));
            this.addFace(new Face3(v2, v5, v4));
            this.addFace(new Face3(v3, v6, v5));
            this.addFace(new Face3(v4, v5, v6));
        }

        vertices.clear();
        indices.clear();

        return compile();
    }

    public Mesh addFaces(Face[] faces)
    {
        for (Face face : faces)
        {
            addFace(face);
        }

        return this;
    }

    public Mesh addMesh(Mesh mesh)
    {
        for (Integer index : mesh.indexList)
        {
            this.addVertex(new Vertex(mesh.vertexList.get(index)), false);
        }

        return this;
    }

    public Mesh addFace(Face face)
    {
        if (face != null)
        {
            if (face instanceof Face4)
            {
                addFace(((Face4) face).getF1());
                addFace(((Face4) face).getF2());
            }

            if (face instanceof Face3)
            {
                this.addVertex(((Face3) face).getV1());
                this.addVertex(((Face3) face).getV2());
                this.addVertex(((Face3) face).getV3());

                if (face.getMaterial() != null)
                {
                    getFaces(face.getMaterial()).add(face);
                }
            }
        }

        return this;
    }

    public Mesh addVertices(Vertex[] vertices)
    {
        for (Vertex vertex : vertices)
        {
            addVertex(vertex);
        }

        return this;
    }

    public Mesh addVertex(Vertex vertex)
    {
        return addVertex(vertex, true);
    }

    public Mesh addVertex(Vertex vertex, boolean findExisting)
    {
        if (vertex != null)
        {
            boolean flag = true;

            if (findExisting)
            {
                for (int i = 0; i < vertexList.size(); i++)
                {
                    Vertex v = vertexList.get(i);

                    if (v == null)
                    {
                        this.vertexList.remove(i);
                    } else
                    {
                        if (v.equals(vertex, this.epsilon))
                        {
                            flag = false;
                            indexList.add(v.index);
                        }
                    }
                }
            }

            if (flag)
            {
                Integer index = vertexList.size();

                vertex.index = index;
                indexList.add(index);
                vertexList.add(vertex);
                Vector3f.add(vertex.position, cumulativeVertex, cumulativeVertex);
            }
        }

        return this;
    }

    public Mesh compile()
    {
        float[] vertices = new float[vertexList.size() * VERTEX_SIZE_FLOATS];
        int[] indices = new int[indexList.size()];

        int counter = 0;
        for (int i = 0; i < vertexList.size(); i++)
        {
            Vertex vertex = vertexList.get(i);
            float[] vertexData = vertex.getData();
            for (int j = 0; j < VERTEX_SIZE_FLOATS; j++)
            {
                vertices[counter++] = vertexData[j];
            }
        }

        for (int i = 0; i < indexList.size(); i++)
        {
            indices[i] = indexList.get(i);
        }

        FloatBuffer vertexData = (FloatBuffer) BufferUtils.createFloatBuffer(vertices.length).put(vertices).flip();
        IntBuffer indexData = (IntBuffer) BufferUtils.createIntBuffer(indices.length).put(indices).flip();

        this.numVertices = vertexList.size();
        this.numIndices = indexList.size();

        glBindVertexArray(this.vao);

        upload(vertexData, GL_ARRAY_BUFFER, vbo, FLOAT_SIZE_BYTES, 0, 0);
        upload(indexData, GL_ELEMENT_ARRAY_BUFFER, ibo, INT_SIZE_BYTES, 0, 0);

        glBindVertexArray(0);

        return this;
    }

    private void upload(Buffer buffer, int bufferTarget, int bufferId, int elementSizeBytes, int reserveElements, int offset)
    {
        glBindBuffer(bufferTarget, bufferId);

        int currentElements = buffer == null ? 0 : buffer.capacity();
        reserveElements = Math.max(reserveElements, currentElements);

        if (reserveElements >= currentElements)
        {
            glBufferData(bufferTarget, reserveElements * elementSizeBytes, GL_DYNAMIC_DRAW);
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

    public Mesh copy()
    {
        Mesh mesh = Mesh.create();

        mesh.vertexList = new ArrayList<>(this.vertexList);
        mesh.indexList = new ArrayList<>(this.indexList);
        mesh.numVertices = this.numVertices;
        mesh.numIndices = this.numIndices;
        return mesh;
    }

    public static Mesh create(float epsilon)
    {
        Mesh mesh = new Mesh(epsilon);

        mesh.vao = glGenVertexArrays();
        mesh.vbo = glGenBuffers();
        mesh.ibo = glGenBuffers();

        glBindVertexArray(mesh.vao);


        glBindBuffer(GL_ARRAY_BUFFER, mesh.vbo);
        glVertexAttribPointer(ATTRIBUTE_LOCATION_POSITION, FLOATS_PER_POSITION, GL_FLOAT, false, POSITION_STRIDE_BYTES, POSITION_OFFSET_BYTES);
        glVertexAttribPointer(ATTRIBUTE_LOCATION_NORMAL, FLOATS_PER_NORMAL, GL_FLOAT, false, NORMAL_STRIDE_BYTES, NORMAL_OFFSET_BYTES);
        glVertexAttribPointer(ATTRIBUTE_LOCATION_TEXTURE, FLOATS_PER_TEXTURE, GL_FLOAT, false, TEXTURE_STRIDE_BYTES, TEXTURE_OFFSET_BYTES);
        glBindBuffer(GL_ARRAY_BUFFER, 0);


        mesh.upload(null, GL_ARRAY_BUFFER, mesh.vbo, FLOAT_SIZE_BYTES, 2000, 0);
        mesh.upload(null, GL_ELEMENT_ARRAY_BUFFER, mesh.ibo, INT_SIZE_BYTES, 2000, 0);
        glBindVertexArray(0);

        return mesh;
    }

    public static Mesh create()
    {
        return create(0.0001F);
    }

    public static class Vertex
    {
        Vector3f position;
        Vector3f normal;
        Vector2f texture;

        private int index;

        public Vertex(Vector3f position, Vector3f normal, Vector2f texture)
        {
            this.position = position;
            this.normal = normal;
            this.texture = texture;
        }

        public Vertex(float[] data)
        {
            if (data.length != VERTEX_SIZE_FLOATS)
            {
                IllegalArgumentException e = new IllegalArgumentException("Cannot construct vertex from incomplete data");
                WireEngine.getLogger().warning(e.getLocalizedMessage());
                throw e;
            }

            this.position = new Vector3f(data[0], data[1], data[2]);
            this.normal = new Vector3f(data[3], data[4], data[5]);
            this.texture = new Vector2f(data[6], data[7]);
        }

        public Vertex(Vertex vertex)
        {
            this(new Vector3f(vertex.position), new Vector3f(vertex.normal), new Vector2f(vertex.texture));
            this.index = vertex.index;
        }

        public Vector3f getPosition()
        {
            return position;
        }

        public Vertex setPosition(Vector3f position)
        {
            this.position = position;
            return this;
        }

        public Vector3f getNormal()
        {
            return normal;
        }

        public Vertex setNormal(Vector3f normal)
        {
            this.normal = normal;
            return this;
        }

        public Vector2f getTexture()
        {
            return texture;
        }

        public Vertex setTexture(Vector2f texture)
        {
            this.texture = texture;
            return this;
        }

        public int getIndex()
        {
            return index;
        }

        public Vertex setIndex(int index)
        {
            this.index = index;
            return this;
        }

        public float[] getData()
        {
            return new float[]{position.x, position.y, position.z, normal.x, normal.y, normal.z, texture.x, texture.y};
        }

        public boolean equals(Vertex other, float epsilon)
        {
            return equalPosition(other, epsilon) && equalsNormal(other, epsilon) && equalsTexture(other, epsilon);
        }

        public boolean equalPosition(Vertex other, float epsilon)
        {
            if (other != null)
            {
                epsilon = Math.abs(epsilon);

                if (epsilon > 0.0F)
                {
                    return Vector3f.sub(this.position, other.position, null).lengthSquared() <= epsilon * epsilon;
                } else
                {
                    return this.position.equals(other.position);
                }
            }

            return false;
        }

        public boolean equalsNormal(Vertex other, float epsilon)
        {
            if (other != null)
            {
                epsilon = Math.abs(epsilon);

                if (epsilon > 0.0F)
                {
                    return Vector3f.sub(this.normal, other.normal, null).lengthSquared() <= epsilon * epsilon;
                } else
                {
                    return this.normal.equals(other.normal);
                }
            }

            return false;
        }

        public boolean equalsTexture(Vertex other, float epsilon)
        {
            if (other != null)
            {
                epsilon = Math.abs(epsilon);

                if (epsilon > 0.0F)
                {
                    return Vector2f.sub(this.texture, other.texture, null).lengthSquared() <= epsilon * epsilon;
                } else
                {
                    return this.texture.equals(other.texture);
                }
            }

            return false;
        }

        @Override
        public String toString()
        {
            return "Vertex[Index=" + index + ", " + position.toString().replace("Vector3f", "Position") + ", " + normal.toString().replace("Vector3f", "Normal") + ", " + texture.toString().replace("Vector2f", "Texture") + "]";
        }
    }

    public interface Face
    {
        Material getMaterial();

        Vector3f getNormal();

        int getNumVertices();
    }

    public static class Face3 implements Face
    {
        Material material;
        Vertex v1;
        Vertex v2;
        Vertex v3;

        public Face3(Vertex v1, Vertex v2, Vertex v3)
        {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
        }

        public Face3(float[] data)
        {
            if (data.length != VERTEX_SIZE_FLOATS * getNumVertices())
            {
                IllegalArgumentException e = new IllegalArgumentException("Cannot construct face from incomplete data");
                WireEngine.getLogger().warning(e.getLocalizedMessage());
                throw e;
            }

            this.v1 = new Vertex(Arrays.copyOfRange(data, VERTEX_SIZE_FLOATS * 0, VERTEX_SIZE_FLOATS * 1));
            this.v2 = new Vertex(Arrays.copyOfRange(data, VERTEX_SIZE_FLOATS * 1, VERTEX_SIZE_FLOATS * 2));
            this.v3 = new Vertex(Arrays.copyOfRange(data, VERTEX_SIZE_FLOATS * 2, VERTEX_SIZE_FLOATS * 3));
        }

        public Vertex getV1()
        {
            return v1;
        }

        public Face3 setV1(Vertex v1)
        {
            this.v1 = v1;
            return this;
        }

        public Vertex getV2()
        {
            return v2;
        }

        public Face3 setV2(Vertex v2)
        {
            this.v2 = v2;
            return this;
        }

        public Vertex getV3()
        {
            return v3;
        }

        public Face3 setV3(Vertex v3)
        {
            this.v3 = v3;
            return this;
        }

        @Override
        public Material getMaterial()
        {
            return material;
        }

        @Override
        public Vector3f getNormal()
        {
            Vector3f a = Vector3f.sub(v2.position, v1.position, null);
            Vector3f b = Vector3f.sub(v3.position, v1.position, null);
            return Vector3f.cross(a, b, null).normalise(null);
        }

        @Override
        public int getNumVertices()
        {
            return 3;
        }

        @Override
        public String toString()
        {
            return "Face3{" + "v1=" + v1 + ", v2=" + v2 + ", v3=" + v3 + '}';
        }
    }

    public static class Face4 implements Face
    {
        Material material;
        Face3 f1;
        Face3 f2;

        public Face4(Face3 f1, Face3 f2)
        {
            this.f1 = f1;
            this.f2 = f2;
        }

        public Face4(float[] data)
        {
            if (data.length != VERTEX_SIZE_FLOATS * getNumVertices())
            {
                IllegalArgumentException e = new IllegalArgumentException("Cannot construct quad from incomplete data");
                WireEngine.getLogger().warning(e.getLocalizedMessage());
                throw e;
            }

            Vertex v1 = new Vertex(Arrays.copyOfRange(data, VERTEX_SIZE_FLOATS * 0, VERTEX_SIZE_FLOATS * 1));
            Vertex v2 = new Vertex(Arrays.copyOfRange(data, VERTEX_SIZE_FLOATS * 1, VERTEX_SIZE_FLOATS * 2));
            Vertex v3 = new Vertex(Arrays.copyOfRange(data, VERTEX_SIZE_FLOATS * 2, VERTEX_SIZE_FLOATS * 3));
            Vertex v4 = new Vertex(Arrays.copyOfRange(data, VERTEX_SIZE_FLOATS * 3, VERTEX_SIZE_FLOATS * 4));

            this.f1 = new Face3(v1, v2, v3);
            this.f2 = new Face3(v1, v3, v4);
        }

        public Face4(Vertex v1, Vertex v2, Vertex v3, Vertex v4)
        {
            this(new Face3(v1, v2, v3), new Face3(v1, v3, v4));
        }

        public Face3 getF1()
        {
            return f1;
        }

        public Face4 setF1(Face3 f1)
        {
            this.f1 = f1;
            return this;
        }

        public Face3 getF2()
        {
            return f2;
        }

        public Face4 setF2(Face3 f2)
        {
            this.f2 = f2;
            return this;
        }

        @Override
        public Material getMaterial()
        {
            return material;
        }

        @Override
        public Vector3f getNormal()
        {
            return Vector3f.add(f1.getNormal(), f2.getNormal(), null).normalise(null);
        }

        @Override
        public int getNumVertices()
        {
            return 4;
        }

        @Override
        public String toString()
        {
            return "Face4{" + "f1=" + f1 + ", f2=" + f2 + '}';
        }
    }
}
