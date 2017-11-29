package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.physics.collision.colliders.Ray;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.geometry.MeshData;
import wireengine.core.rendering.geometry.Model;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.core.util.MathUtils;
import wireengine.core.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kelan
 */
public class Collider
{
    private Transformation transformation;
    private final List<LinkedVertex> vertices = new ArrayList<>();
    private final List<Triangle> triangles = new ArrayList<>();
    private Vector3f centre = new Vector3f();

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
                    LinkedVertex vertex = new LinkedVertex(pos, new int[0], 0);
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

        recalculateCentre();
    }

    public Collider(List<Vector3f> vertices, List<Integer> indices, Transformation transformation)
    {
        this.transformation = transformation;

        for (Vector3f vertex : vertices)
        {
            this.vertices.add(new LinkedVertex(vertex, -1));
        }

        for (int index : indices)
        {
            this.vertices.get(index).index = index;
        }

        for (int i = 0; i < indices.size();)
        {
            LinkedVertex v0 = this.vertices.get(indices.get(i++));
            LinkedVertex v1 = this.vertices.get(indices.get(i++));
            LinkedVertex v2 = this.vertices.get(indices.get(i++));

            this.triangles.add(new Triangle(v0, v1, v2));
        }

        constructAdjacents();
        recalculateCentre();
    }

    public Collider(MeshData mesh, Transformation transformation)
    {
        this(mesh.getGeometrics(), mesh.getIndices(), transformation);
    }

    public Collider(Model model)
    {
        this(model.getMesh(), model.getTransformation());
    }

    public void constructAdjacents()
    {
        for (Triangle triangle : this.triangles)
        {
            LinkedVertex[] verts = new LinkedVertex[]{(LinkedVertex) triangle.getP1(), (LinkedVertex) triangle.getP2(), (LinkedVertex) triangle.getP3()};

            for (int i0 = 0; i0 < verts.length; i0++)
            {
                for (int i1 = 0; i1 < verts.length; i1++)
                {
                    if (i0 == i1)
                    {
                        continue;
                    }

                    this.vertices.get(verts[i0].index).addLink(verts[i1].index);
                }
            }
        }
    }

    public void recalculateCentre()
    {
        Vector3f newCentre = new Vector3f();

        for (Vector3f vertex : this.vertices)
        {
            Vector3f.add(vertex, newCentre, newCentre);
        }

        this.centre = (Vector3f) newCentre.scale(1.0F / this.vertices.size());
    }

    public void simplifyMesh()
    {
        // Remove duplicated vertices in the list of triangles. For example, two triangles might share a vertex at the same position, but two separate vertices for each triangle was added to the list.
    }

    public Vector3f getFurthestVertex(Vector3f direction)
    {
        direction = MathUtils.rotateVector3f(this.transformation.getRotation().negate(null), direction, null);

        int currentIndex = 0;
        float currentDistance = Vector3f.dot(this.vertices.get(currentIndex), direction);

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

        return this.getVertex(currentIndex, true);
    }

    private LinkedVertex getVertex(int index, boolean transformed)
    {
        if (index < 0 || index >= this.vertices.size())
        {
            return null;
        }

        LinkedVertex vertex = this.vertices.get(index);

        if (transformed)
        {
            LinkedVertex newVertex = new LinkedVertex(vertex);
            newVertex.set(newVertex.getTransformed(this.transformation));

            return newVertex;
        }

        return vertex;
    }

    public boolean rayIntersects(Ray ray, Vector3f destCollisionPoint)
    {
        return rayIntersects(ray, destCollisionPoint, false);
    }

    public boolean rayIntersects(Ray ray, Vector3f destCollisionPoint, boolean findClosest)
    {
        //TODO: make this function faster. Maybe use a hill-climbing algorithm to walk towards the ray and check if the ray intersects that triangle.

        Vector3f closestPoint = null;

        for (Triangle triangle : this.triangles)
        {
            Triangle newTri = triangle.getTransformed(this.transformation);

            Vector3f collisionPoint = new Vector3f();
            if (newTri.rayIntersects(ray, collisionPoint))
            {
                if (findClosest)
                {
                    if (closestPoint == null || MathUtils.distanceSquared(collisionPoint, ray.origin) < MathUtils.distanceSquared(closestPoint, ray.origin))
                    {
                        closestPoint = collisionPoint;
                    }
                } else
                {
                    closestPoint = collisionPoint;
                    break;
                }
            }
        }

        if (closestPoint != null)
        {
            destCollisionPoint.set(closestPoint);
            return true;
        }

        return false;
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

    public Vector3f getCentre()
    {
        return new Vector3f(centre); // copy it to avoid external classes editing it.
    }

    private class LinkedVertex extends Vector3f
    {
        public int index = -1;
        public int[] links = new int[0];

        public LinkedVertex(ReadableVector3f src, int[] links, int index)
        {
            super(src);
            this.links = links;
            this.index = index;
        }

        public LinkedVertex(ReadableVector3f src, int index)
        {
            this(src, new int[0], index);
        }

        public LinkedVertex(LinkedVertex src)
        {
            this(src, src.links, src.index);
        }

        public Vector3f getTransformed(Transformation transformation)
        {
            Vector4f transformed = new Vector4f(this.x, this.y, this.z, 1.0F);

            Matrix4f.transform(transformation.getMatrix(null), transformed, transformed);

            return new Vector3f(transformed);
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

        @Override
        public String toString()
        {
            return "LinkedVertex{" + this.x + ", " + this.y + ", " +  this.z + ", index=" + index + ", links=" + Arrays.toString(links) + '}';
        }
    }
}
