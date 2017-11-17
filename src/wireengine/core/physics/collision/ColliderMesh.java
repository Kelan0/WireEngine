package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.geometry.Mesh;
import wireengine.core.rendering.geometry.Model;
import wireengine.core.rendering.geometry.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kelan
 */
public class ColliderMesh
{
    private Triangle[] triangles;
    private Transformation transformation;

    public ColliderMesh(Triangle[] triangles, Transformation transformation)
    {
        this.set(triangles, transformation);
    }

    public ColliderMesh(Mesh mesh, Transformation transformation)
    {
        List<Triangle> triangles = new ArrayList<>();

        for (Mesh.Face3 f3 : mesh.getFaceList())
        {
            Triangle triangle = f3.getCollidable(new Vector3f());
            triangles.add(triangle);
        }

        this.set(triangles.toArray(new Triangle[triangles.size()]), transformation);
    }

    public ColliderMesh(Model model)
    {
        this(model.getMesh(), model.getTransformation());
    }

    public ColliderMesh set(Triangle[] triangles, Transformation transformation)
    {
        if (triangles == null)
        {
            throw new IllegalArgumentException("Cannot construct collidable mesh from null triangle list.");
        }

        this.triangles = triangles;
        this.transformation = new Transformation();

        return this;
    }

    public Vector3f getFurthestVertex(Vector3f direction)
    {
        // Time complexity of this function is O(n), not the best but it will do for now.
        float max = Float.NEGATIVE_INFINITY;
        Vector3f vertex = null;

        for (int i = 0; i < this.vertices.length; i++)
        {
            Vector3f v = this.vertices[i];
            float f = Vector3f.dot(v, direction);

            if (f > max)
            {
                max = f;
                vertex = v;
            }
        }

        return vertex;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColliderMesh that = (ColliderMesh) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(triangles, that.triangles);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(triangles);
    }

    public int getNumTriangles()
    {
        return triangles.length;
    }

    public Triangle[] getTriangles()
    {
        return triangles;
    }
}
