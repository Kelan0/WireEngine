package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionState;
import wireengine.core.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class TriangleMesh extends Collider<TriangleMesh>
{
    protected Vector3f position;
    protected List<Triangle> triangles = new ArrayList<>();

    public TriangleMesh(Vector3f position, List<Triangle> triangles)
    {
        super(ColliderType.TRIANGLE_MESH);
        this.position = position;
        this.triangles = triangles;
    }

    @Override
    public CollisionState.CollisionComponent<TriangleMesh> getCollision(AxisAlignedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<TriangleMesh> getCollision(Ellipsoid collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<TriangleMesh> getCollision(Frustum collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<TriangleMesh> getCollision(OrientedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<TriangleMesh> getCollision(Plane collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<TriangleMesh> getCollision(Ray collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<TriangleMesh> getCollision(Sphere collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<TriangleMesh> getCollision(Triangle collider)
    {
        return null;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        for (Triangle tri : triangles)
        {
            if (tri.getSignedDistance(point) <= 0.0F && tri.pointIntersects(tri.plane.getClosestPoint(point)))
            {
                //Point is behind the triangle and the point projected onto the triangles plane is inside the triangle means there is an intersection.
                return true;
            }
        }

        return false;
    }

    @Override
    public Vector3f getClosestPoint(Vector3f point)
    {
        Triangle triangle = getClosestTriangle(point);

        if (triangle != null)
        {
            return triangle.getClosestPoint(point);
        }

        return new Vector3f(point); // No closest point, just return itself.
    }

    @Override
    public Vector3f getNormalAt(Vector3f point)
    {
        Triangle triangle = getClosestTriangle(point);

        if (triangle != null)
        {
            return triangle.getNormalAt(null);
        }

        return new Vector3f(); //Zero-length normal. Everything that calls this should assume the vector returned is normalized.
    }

    public Triangle getClosestTriangle(Vector3f point)
    {
        float f = Float.MAX_VALUE;
        Triangle triangle = null;

        for (Triangle tri : triangles)
        {
            if (tri != null)
            {
                Vector3f v = tri.getClosestPoint(point);

                float f1 = MathUtils.distanceSquared(point, v);

                if (f1 < f)
                {
                    triangle = tri;
                    f = f1;
                }
            }
        }

        return new Triangle(triangle.p1, triangle.p2, triangle.p3);
    }

    @Override
    public Vector3f getPosition()
    {
        return position;
    }

    @Override
    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public List<Triangle> getTriangles()
    {
        return triangles;
    }
}
