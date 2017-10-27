package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionState;

/**
 * @author Kelan
 */
public class Sphere extends Collider<Sphere>
{
    protected Vector3f position;
    protected float radius;

    public Sphere(Vector3f position, float radius)
    {
        super(ColliderType.SPHERE);
        this.position = position;
        this.radius = radius;
    }

    @Override
    public CollisionState.CollisionComponent<Sphere> getCollision(AxisAlignedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Sphere> getCollision(Ellipsoid collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Sphere> getCollision(Frustum collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Sphere> getCollision(OrientedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Sphere> getCollision(Plane collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Sphere> getCollision(Ray collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Sphere> getCollision(Sphere collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Sphere> getCollision(Triangle collider)
    {
        return null;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        return Vector3f.sub(point, this.getCentre(), null).lengthSquared() < this.getRadius() * this.getRadius();
    }

    @Override
    public Vector3f getClosestPoint(Vector3f point)
    {
        return (Vector3f) this.getNormalAt(point).scale(this.getRadius());
    }

    @Override
    public Vector3f getNormalAt(Vector3f point)
    {
        return (Vector3f) Vector3f.sub(point, this.getCentre(), null).normalise();
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

    public float getRadius()
    {
        return radius;
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
    }
}
