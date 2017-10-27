package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionState;

/**
 * @author Kelan
 */
public class Frustum extends Collider<Frustum>
{
    protected Vector3f position;

    //TODO: Implement this.
    public Frustum(Vector3f position)
    {
        super(ColliderType.FRUSTUM);
        this.position = position;
    }

    @Override
    public CollisionState.CollisionComponent<Frustum> getCollision(AxisAlignedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Frustum> getCollision(Ellipsoid collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Frustum> getCollision(Frustum collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Frustum> getCollision(OrientedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Frustum> getCollision(Plane collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Frustum> getCollision(Ray collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Frustum> getCollision(Sphere collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Frustum> getCollision(Triangle collider)
    {
        return null;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        return false;
    }

    @Override
    public Vector3f getClosestPoint(Vector3f point)
    {
        return null;
    }

    @Override
    public Vector3f getNormalAt(Vector3f point)
    {
        return null;
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
}
