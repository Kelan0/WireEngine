package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.colliders.*;

/**
 * @author Kelan
 */
public abstract class Collider<T extends Collider<T>>
{
    public final ColliderType colliderType;

    protected Collider(ColliderType colliderType)
    {
        this.colliderType = colliderType;
    }

    public Vector3f getCentre()
    {
        return getPosition();
    }

    public abstract CollisionState.CollisionComponent<T> getCollision(AxisAlignedBB collider);

    public abstract CollisionState.CollisionComponent<T> getCollision(Ellipsoid collider);

    public abstract CollisionState.CollisionComponent<T> getCollision(Frustum collider);

    public abstract CollisionState.CollisionComponent<T> getCollision(OrientedBB collider);

    public abstract CollisionState.CollisionComponent<T> getCollision(Plane collider);

    public abstract CollisionState.CollisionComponent<T> getCollision(Ray collider);

    public abstract CollisionState.CollisionComponent<T> getCollision(Sphere collider);

    public abstract CollisionState.CollisionComponent<T> getCollision(Triangle collider);

    public abstract boolean pointIntersects(Vector3f collider);

    public abstract Vector3f getClosestPoint(Vector3f collider);

    public abstract Vector3f getNormalAt(Vector3f collider);

    public abstract Vector3f getPosition();

    public abstract void setPosition(Vector3f position);

    public ColliderType getColliderType()
    {
        return colliderType;
    }

    public enum ColliderType
    {
        PLANE, SPHERE, CYLINDER, CONE, QUAD, TRIANGLE, AABB, OBB, FRUSTUM, RAY, ELIPSOID, TRIANGLE_MESH, COMPOSITE
    }
}
