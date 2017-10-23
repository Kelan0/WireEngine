package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class Ray extends Collider
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
    public CollisionHandler getCollision(Collider collider, float epsilon)
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
