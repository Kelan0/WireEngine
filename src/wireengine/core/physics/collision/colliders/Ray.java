package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionState;

/**
 * @author Kelan
 */
public class Ray extends Collider<Ray>
{
    protected Vector3f position; //Ray origin. Called it position for consistency.
    protected Vector3f direction;

    public Ray(Vector3f position, Vector3f direction)
    {
        super(ColliderType.RAY);
        this.set(position, direction);
    }

    private void set(Vector3f position, Vector3f direction)
    {
        this.position = position;
        if (direction.lengthSquared() != 1.0F)
        {
            this.direction = direction.normalise(null);
        } else
        {
            this.direction = direction;
        }
    }

    @Override
    public CollisionState.CollisionComponent<Ray> getCollision(AxisAlignedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ray> getCollision(Ellipsoid collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ray> getCollision(Frustum collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ray> getCollision(OrientedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ray> getCollision(Plane collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ray> getCollision(Ray collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ray> getCollision(Sphere collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ray> getCollision(Triangle collider)
    {
        return null;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        return Vector3f.dot(Vector3f.sub(point, this.position, null).normalise(null), direction) == 1.0F;
    }

    @Override
    public Vector3f getClosestPoint(Vector3f point)
    {
        return this.position;// TODO calculate this and use it for pointIntersects(Vector3f) with epsilon. (point distance < epsilon = intersection)
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
        this.set(position, direction);
    }

    public Vector3f getDirection()
    {
        return direction;
    }

    public void setDirection(Vector3f direction)
    {
        this.set(position, direction);
    }
}
