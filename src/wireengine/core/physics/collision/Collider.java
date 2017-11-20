package wireengine.core.physics.collision;

import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.geometry.GLMesh;
import wireengine.core.rendering.geometry.Model;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.core.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class Collider
{
    private Transformation transformation;
    private final List<LinkedVertex> vertices = new ArrayList<>();
    private final List<Triangle> triangles = new ArrayList<>();

    public Collider(List<Triangle> triangles, Transformation transformation)
    {
        this.transformation = transformation;

        final float epsilon = 0.0001F;

        for (Triangle triangle : triangles)
        {
            if (triangle != null)
            {
                this.triangles.add(triangle);

                Vector3f[] triVerts = new Vector3f[]{new Vector3f(triangle.getP1()), new Vector3f(triangle.getP2()), new Vector3f(triangle.getP3())};

                for (Vector3f pos : triVerts)
                {
                    LinkedVertex vertex = new LinkedVertex(pos, new int[0]);
                    int index = Utils.getCloseIndex(this.vertices, pos, epsilon);

                    if (index < 0)
                    {
                        this.vertices.add(vertex);
                    }
                }
            }
        }

        for (Triangle triangle : this.triangles) //Link every vertex in a triangle to the other two vertices.
        {
            int index0 = Utils.getCloseIndex(this.vertices, new Vector3f(triangle.getP1()), epsilon);
            int index1 = Utils.getCloseIndex(this.vertices, new Vector3f(triangle.getP2()), epsilon);
            int index2 = Utils.getCloseIndex(this.vertices, new Vector3f(triangle.getP3()), epsilon);

            if (index0 >= 0)
                vertices.get(index0).addLink(index1).addLink(index2);

            if (index1 >= 0)
                vertices.get(index1).addLink(index0).addLink(index2);

            if (index2 >= 0)
                vertices.get(index2).addLink(index0).addLink(index1);
        }
    }

    public Collider(List<Vector3f> vertices, List<Integer> indices, Transformation transformation)
    {
        this.transformation = transformation;
//        List<Triangle> triangles = new ArrayList<>();
//
//        for (int i = 0; i < indices.length;)
//        {
//            Vector3f v0 = vertices.get(i++);
//            Vector3f v1 = vertices.get(i++);
//            Vector3f v2 = vertices.get(i++);
//
//            triangles.add(new Triangle(v0, v1, v2));
//        }
//
//        this.set(triangles);

        for (int i = 0; i < indices.size();)
        {
            this.vertices.add(new LinkedVertex(vertices.get(indices.get(i++))));
            this.vertices.add(new LinkedVertex(vertices.get(indices.get(i++))));
            this.vertices.add(new LinkedVertex(vertices.get(indices.get(i++))));
        }

        for (int i = 0; i < indices.size();)
        {
            int i0 = indices.get(i++);
            int i1 = indices.get(i++);
            int i2 = indices.get(i++);

            this.vertices.get(i0).addLink(i1).addLink(i2);
            this.vertices.get(i1).addLink(i0).addLink(i2);
            this.vertices.get(i2).addLink(i0).addLink(i1);
        }
    }

    public Collider(GLMesh mesh, Transformation transformation)
    {
        this(mesh.getGeometrics(), mesh.getIndices(), transformation);
    }

    public Collider(Model model)
    {
        this(model.getMesh(), model.getTransformation());
    }

    public Vector3f getFurthestVertex(Vector3f direction)
    {
        int currentIndex = 0;
        float currentDistance = Vector3f.dot(vertices.get(currentIndex), direction);

        while (true)
        {
            int newIndex = currentIndex;
            float newDistance = currentDistance;
            LinkedVertex vertex = this.vertices.get(currentIndex);

            for (int i = 0; i < vertex.links.length; i++)
            {
                int index = vertex.links[i];
                float distance = Vector3f.dot(this.vertices.get(index), direction);

                if (distance > newDistance)
                {
                    newIndex = index;
                    newDistance = distance;
                }
            }
            if (newIndex == currentIndex)
            {
                break;
            } else
            {
                currentIndex = newIndex;
                currentDistance = newDistance;
            }
        }

        return this.vertices.get(currentIndex);
    }

    public Transformation getTransformation()
    {
        return transformation;
    }

    public void setTransformation(Transformation transformation)
    {
        this.transformation = transformation;
    }

    public int getNumTriangles()
    {
        return triangles.size();
    }

    public int getNumVertices()
    {
        return vertices.size();
    }

    public List<LinkedVertex> getVertices()
    {
        return vertices;
    }

    public List<Triangle> getTriangles()
    {
        return triangles;
    }

    private class LinkedVertex extends Vector3f
    {
        public int[] links;

        public LinkedVertex()
        {
            super();
        }

        public LinkedVertex(ReadableVector3f src)
        {
            super(src);
        }

        public LinkedVertex(float x, float y, float z)
        {
            super(x, y, z);
        }

        public LinkedVertex(int[] links)
        {
            this();
            this.links = links;
        }

        public LinkedVertex(ReadableVector3f src, int[] links)
        {
            this(src);
            this.links = links;
        }

        public LinkedVertex(float x, float y, float z, int[] links)
        {
            this(x, y, z);
            this.links = links;
        }


        public LinkedVertex addLink(int index)
        {

            if (index >= 0 && !containsLink(index))
            {
                int[] newLinks = new int[this.links.length + 1];
                for (int i = 1; i < newLinks.length; i++)
                {
                    newLinks[i] = this.links[i - 1];
                }
                newLinks[0] = index;
                this.links = newLinks;
            }

            return this;
        }

        public boolean containsLink(int index)
        {
            for (int i = 0; i < this.links.length; i++)
            {
                if (this.links[i] == index)
                {
                    return true;
                }
            }

            return false;
        }

        public int getNumLinks()
        {
            return this.links.length;
        }
    }
}
