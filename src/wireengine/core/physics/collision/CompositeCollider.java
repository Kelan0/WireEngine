package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;

import java.util.List;

/**
 * @author Kelan
 */
public class CompositeCollider extends Collider
{
    protected Vector3f position;
    protected List<Collider> colliders;

    public CompositeCollider(Vector3f position, List<Collider> colliders)
    {
        super(ColliderType.COMPOSITE);
        this.position = position;
        this.colliders = colliders;
    }

    public List<Collider> getColliders()
    {
        return colliders;
    }

    @Override
    public CollisionHandler getCollision(Collider collider, float epsilon)
    {
        CollisionHandler collision = new CollisionHandler();
        for (Collider c : this.colliders)
        {
            CollisionHandler collision1 = c.getCollision(collider, epsilon);

            if (collision1.didHit)
            {
                collision = collision1;
            }
        }
        return collision;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        for (Collider c : this.colliders)
        {
            if (c.pointIntersects(point))
            {
                return true;
            }
        }
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
