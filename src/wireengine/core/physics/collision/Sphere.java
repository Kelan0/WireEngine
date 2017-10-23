package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class Sphere extends Collider
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
    public CollisionHandler getCollision(Collider collider, float epsilon)
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
