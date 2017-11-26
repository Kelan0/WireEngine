package wireengine.core.rendering.geometry;

import javafx.util.Pair;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.WireEngine;
import wireengine.core.physics.collision.colliders.Triangle;

import java.util.*;

/**
 * @author Kelan
 */
public class MeshData
{
    public static final int FLOAT_SIZE_BYTES = Float.BYTES;
    public static final int INT_SIZE_BYTES = Integer.BYTES;

    public static final int FLOATS_PER_POSITION = 3;
    public static final int FLOATS_PER_NORMAL = 3;
    public static final int FLOATS_PER_TEXTURE = 2;
    public static final int FLOATS_PER_COLOUR = 4;

    public static final int VERTEX_SIZE_FLOATS = FLOATS_PER_POSITION + FLOATS_PER_NORMAL + FLOATS_PER_TEXTURE + FLOATS_PER_COLOUR;
    public static final int VERTEX_SIZE_BYTES = VERTEX_SIZE_FLOATS * FLOAT_SIZE_BYTES;

    public static final int POSITION_OFFSET_FLOATS = 0;
    public static final int NORMAL_OFFSET_FLOATS = POSITION_OFFSET_FLOATS + FLOATS_PER_POSITION;
    public static final int TEXTURE_OFFSET_FLOATS = NORMAL_OFFSET_FLOATS + FLOATS_PER_NORMAL;
    public static final int COLOUR_OFFSET_FLOATS = TEXTURE_OFFSET_FLOATS + FLOATS_PER_TEXTURE;

    public static final int POSITION_OFFSET_BYTES = POSITION_OFFSET_FLOATS * FLOAT_SIZE_BYTES;
    public static final int NORMAL_OFFSET_BYTES = NORMAL_OFFSET_FLOATS * FLOAT_SIZE_BYTES;
    public static final int TEXTURE_OFFSET_BYTES = TEXTURE_OFFSET_FLOATS * FLOAT_SIZE_BYTES;
    public static final int COLOUR_OFFSET_BYTES = COLOUR_OFFSET_FLOATS * FLOAT_SIZE_BYTES;

    public static final int POSITION_STRIDE_BYTES = VERTEX_SIZE_BYTES;
    public static final int NORMAL_STRIDE_BYTES = VERTEX_SIZE_BYTES;
    public static final int TEXTURE_STRIDE_BYTES = VERTEX_SIZE_BYTES;
    public static final int COLOUR_STRIDE_BYTES = VERTEX_SIZE_BYTES;

    // Package private variables, accessible by GLMesh.
    float epsilon;
    Vector3f cumulativeVertex;
    Map<Material, List<Face3>> materialFaceMap = new HashMap<>();
    Map<Material, List<Pair<Integer, Integer>>> materialRenderMap = new HashMap<>();
    List<Vertex> vertexList;
    List<Integer> indexList;
    List<Face3> faceList;
    GLMesh renderableMesh;
    boolean hasNormals = true;
    boolean hasTextures = true;
    boolean hasColours = true;

    public MeshData(float epsilon)
    {
        this.cumulativeVertex = new Vector3f();
        this.vertexList = new ArrayList<>();
        this.indexList = new ArrayList<>();
        this.faceList = new ArrayList<>();
        this.epsilon = epsilon;
    }

    public MeshData()
    {
        this(0.0001F);
    }

    public int getNumVertices()
    {
        return vertexList.size();
    }

    public int getNumIndices()
    {
        return indexList.size();
    }

    public int getNumFaces()
    {
        return faceList.size();
    }

    public List<Face3> getFaces(Material material)
    {
        if (material != null)
        {
            return this.materialFaceMap.computeIfAbsent(material, m -> new ArrayList<>());
        }
        return new ArrayList<>();
    }

    public List<Pair<Integer, Integer>> getMaterialRenderMap(Material material)
    {
        if (material != null)
        {
            return this.materialRenderMap.computeIfAbsent(material, m -> new ArrayList<>());
        }
        return new ArrayList<>();
    }

    public Vector3f getCentre()
    {
        return new Vector3f(cumulativeVertex.x / (float) vertexList.size(), cumulativeVertex.y / (float) vertexList.size(), cumulativeVertex.z / (float) vertexList.size());
    }

    public List<Vertex> getVertices()
    {
        return this.vertexList;
    }

    public List<Vector3f> getGeometrics()
    {
        List<Vector3f> list = new ArrayList<>();

        for (Vertex v : this.getVertices())
        {
            list.add(v.getPosition());
        }

        return list;
    }

    public List<Vector3f> getNormals()
    {
        List<Vector3f> list = new ArrayList<>();

        for (Vertex v : this.getVertices())
        {
            list.add(v.getNormal());
        }

        return list;
    }

    public List<Vector2f> getTextures()
    {
        List<Vector2f> list = new ArrayList<>();

        for (Vertex v : this.getVertices())
        {
            list.add(v.getTexture());
        }

        return list;
    }

    public List<Vector4f> getColours()
    {
        List<Vector4f> list = new ArrayList<>();

        for (Vertex v : this.getVertices())
        {
            list.add(v.getColour());
        }

        return list;
    }

    public List<Integer> getIndices()
    {
        return indexList;
    }

    public List<Face3> getFaces()
    {
        return this.faceList;
    }

    public GLMesh getRenderableMesh()
    {
        return renderableMesh;
    }

    public MeshData withNormals(boolean normals)
    {
        this.hasNormals = normals;
        return this;
    }

    public MeshData withTextures(boolean textures)
    {
        this.hasTextures = textures;
        return this;
    }

    public MeshData withColours(boolean colours)
    {
        this.hasColours = colours;
        return this;
    }

    public MeshData transform(Transformation transformation)
    {
        return transform(transformation.getMatrix(null));
    }

    public MeshData transform(Matrix4f matrix)
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
            WireEngine.getLogger().warning("Cannot setTransform mesh by null matrix");
        }

        return this;
    }

    public MeshData recentre()
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

    public MeshData subdivideFaces(int divisions)
    {
        for (int i = 0; i < divisions; i++)
        {
            int numIndices = getNumIndices();
            List<Vertex> vertices = new ArrayList<>(this.vertexList);
            List<Integer> indices = new ArrayList<>(this.indexList);
            this.vertexList.clear();
            this.indexList.clear();

            for (int j = 0; j < numIndices; )
            {
                Vertex v1 = vertices.get(indices.get(j++));
                Vertex v2 = vertices.get(indices.get(j++));
                Vertex v3 = vertices.get(indices.get(j++));

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
        }

        return this.compile();
    }

    public MeshData spherifyMesh(float radius)
    {
        for (Face3 face : this.faceList)
        {
            for (Vertex vertex : face.vertices)
            {
                vertex.position.normalise(vertex.normal);
                vertex.position = (Vector3f) vertex.normal.scale(radius);
            }
        }

        return this.compile();
    }

    public MeshData addMesh(MeshData mesh)
    {
        for (Integer index : mesh.indexList)
        {
            this.addVertex(new Vertex(mesh.vertexList.get(index)), false);
        }

        return this;
    }

    public MeshData addFaces(Face[] faces)
    {
        for (Face face : faces)
        {
            addFace(face);
        }

        return this;
    }

    public MeshData addFace(Face face)
    {
        return addFace(face, true);
    }

    public MeshData addFace(Face face, boolean addMaterial)
    {
        if (face != null)
        {
            if (face instanceof Face4)
            {
                addFace(((Face4) face).getF1(), addMaterial);
                addFace(((Face4) face).getF2(), addMaterial);
            }

            if (face instanceof Face3)
            {
                this.addVertex(((Face3) face).getV1());
                this.addVertex(((Face3) face).getV2());
                this.addVertex(((Face3) face).getV3());

                if (addMaterial && face.getMaterial() != null)
                {
                    getFaces(face.getMaterial()).add((Face3) face);
                }
            }
        }

        return this;
    }

    public MeshData addVertices(Vertex[] vertices)
    {
        for (Vertex vertex : vertices)
        {
            addVertex(vertex);
        }

        return this;
    }

    public MeshData addVertex(Vertex vertex)
    {
        return addVertex(vertex, true);
    }

    public MeshData addVertex(Vertex vertex, boolean findExisting)
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

    public MeshData setupFaces()
    {
        //Sort all of the vertices so that all faceList using a material are together in the indices.
        this.vertexList.clear();
        this.indexList.clear();

        int startIndex = 0;
        for (Map.Entry<Material, List<Face3>> entry : this.materialFaceMap.entrySet())
        {
            Material material = entry.getKey();
            List<Face3> faces = entry.getValue();

            if (material != null)
            {
                int endIndex = startIndex;
                for (Face3 face : faces)
                {
                    this.faceList.add(face);
                    for (Vertex v : face.getVertices())
                    {
                        addVertex(v);
                        endIndex++;
                    }
                }

                getMaterialRenderMap(material).add(new Pair<>(startIndex, endIndex));
                startIndex = endIndex;
            }
        }

        return this;
    }

    public MeshData compile()
    {
        this.setupFaces();

        if (WireEngine.isRenderThread())
        {
            if (this.renderableMesh == null)
            {
                this.renderableMesh = new GLMesh(this);
            } else
            {
                this.renderableMesh.compile(this);
            }
        }

        return this;
    }

    public static class Vertex
    {
        Vector3f position;
        Vector3f normal;
        Vector2f texture;
        Vector4f colour;
        private int index;

        public Vertex(Vector3f position, Vector3f normal, Vector2f texture, Vector4f colour)
        {
            this.position = position;
            this.normal = normal;
            this.texture = texture;
            this.colour = colour;
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
            this.colour = new Vector4f(data[8], data[9], data[10], data[11]);
        }

        public Vertex(Vertex vertex)
        {
            this(new Vector3f(vertex.position), new Vector3f(vertex.normal), new Vector2f(vertex.texture), new Vector4f(vertex.colour));
            this.index = vertex.index;
        }

        public Vertex(Vector3f position, Vector3f normal, Vector2f texture)
        {
            this(position, normal, texture, new Vector4f(1.0F, 1.0F, 1.0F, 1.0F));
        }

        public Vertex(Vector3f position, Vector3f normal)
        {
            this(position, normal, new Vector2f(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F));
        }

        public Vertex transform(Transformation transformation)
        {
            Matrix4f matrix = transformation.getMatrix(null);
            Vector4f pos = new Vector4f(this.position.x, this.position.y, this.position.z, 1.0F);
            Matrix4f.transform(matrix, pos, pos);
            this.position.set(pos);

            return this;
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

        public Vector4f getColour()
        {
            return colour;
        }

        public void setColour(Vector4f colour)
        {
            this.colour = colour;
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
            return new float[]{position.x, position.y, position.z, normal.x, normal.y, normal.z, texture.x, texture.y, colour.x, colour.y, colour.z, colour.w};
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

        public boolean equalsColour(Vertex other)
        {
            if (other != null)
            {
                return this.colour.equals(other.colour);
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

        Vertex[] getVertices();

        Triangle getCollidable(Vector3f translate);
    }

    public static class Face3 implements Face
    {
        Material material = Material.NO_MATERIAL;
        Vertex[] vertices = new Vertex[3];

        public Face3(Vertex v1, Vertex v2, Vertex v3)
        {
            this(v1, v2, v3, 0.0001F);
        }

        public Face3(Vertex v1, Vertex v2, Vertex v3, float epsilon)
        {
            this.set(v1, v2, v3);
        }

        public Face3(float[] data)
        {
            if (data.length != VERTEX_SIZE_FLOATS * getVertices().length)
            {
                IllegalArgumentException e = new IllegalArgumentException("Cannot construct face from incomplete data");
                WireEngine.getLogger().warning(e.getLocalizedMessage());
                throw e;
            }

            Vertex v1 = new Vertex(Arrays.copyOfRange(data, VERTEX_SIZE_FLOATS * 0, VERTEX_SIZE_FLOATS * 1));
            Vertex v2 = new Vertex(Arrays.copyOfRange(data, VERTEX_SIZE_FLOATS * 1, VERTEX_SIZE_FLOATS * 2));
            Vertex v3 = new Vertex(Arrays.copyOfRange(data, VERTEX_SIZE_FLOATS * 2, VERTEX_SIZE_FLOATS * 3));

            this.set(v1, v2, v3);
        }

        public void set(Vertex v1, Vertex v2, Vertex v3)
        {
            if (v1 == null || v2 == null || v3 == null)
            {
                throw new IllegalArgumentException("Cannot construct triangle with a null vertex");
            }
            this.vertices[0] = v1;
            this.vertices[1] = v2;
            this.vertices[2] = v3;
        }

        public Vertex getV1()
        {
            return this.vertices[0];
        }

        public Face3 setV1(Vertex v1)
        {
            this.vertices[0] = v1;
            return this;
        }

        public Vertex getV2()
        {
            return this.vertices[1];
        }

        public Face3 setV2(Vertex v2)
        {
            this.vertices[1] = v2;
            return this;
        }

        public Vertex getV3()
        {
            return this.vertices[2];
        }

        public Face3 setV3(Vertex v3)
        {
            this.vertices[2] = v3;
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
            Vector3f a = Vector3f.sub(getV2().position, getV1().position, null);
            Vector3f b = Vector3f.sub(getV3().position, getV1().position, null);
            return Vector3f.cross(a, b, null).normalise(null);
        }

        @Override
        public Vertex[] getVertices()
        {
            return this.vertices;
        }

        @Override
        public Triangle getCollidable(Vector3f translate)
        {
            Vector3f a = Vector3f.add(getV1().position, translate, null);
            Vector3f b = Vector3f.add(getV2().position, translate, null);
            Vector3f c = Vector3f.add(getV3().position, translate, null);

            Vector3f e1 = Vector3f.sub(a, b, null);
            Vector3f e2 = Vector3f.sub(b, c, null);
            Vector3f e3 = Vector3f.sub(c, a, null);

            if (e1.lengthSquared() == 0.0F || e2.lengthSquared() == 0.0F || e3.lengthSquared() == 0.0F)
            {
                return null;
            } else
            {
                return new Triangle(a, b, c);
            }
        }

        public Vertex[] getSharedVertices(Face3 other, float epsilon)
        {
            List<Vertex> shared = new ArrayList<>(3);

            for (Vertex v1 : this.vertices)
            {
                for (Vertex v2 : other.vertices)
                {
                    if (v1.equals(v2, epsilon))
                    {
                        shared.add(v1);
                    }
                }
            }

            return shared.toArray(new Vertex[shared.size()]);
        }

        @Override
        public String toString()
        {
            return "Face3{" + "v1=" + getV1() + ", v2=" + getV2() + ", v3=" + getV3() + '}';
        }
    }

    public static class Face4 implements Face
    {
        Material material;
        private Vertex[] vertices = new Vertex[4]; //TODO: fill this array and invalidate this face if the two faceList are not connected by 2 vertices.
        Face3 f1;
        Face3 f2;

        public Face4(Face3 f1, Face3 f2, float epsilon)
        {
            this.f1 = f1;
            this.f2 = f2;
        }

        public Face4(Face3 f1, Face3 f2)
        {
            this(f1, f2, 0.0001F);
        }

        public Face4(float[] data)
        {
            if (data.length != VERTEX_SIZE_FLOATS * getVertices().length)
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
        public Vertex[] getVertices()
        {
            return this.vertices;
        }

        @Override
        public Triangle getCollidable(Vector3f translate)
        {
            return null;
        }

        @Override
        public String toString()
        {
            return "Face4{" + "f1=" + f1 + ", f2=" + f2 + '}';
        }
    }
}
