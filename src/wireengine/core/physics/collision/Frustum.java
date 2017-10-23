package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class Frustum extends Collider
{
    protected Vector3f position;

    //TODO: Implement this.
    public Frustum(Vector3f position)
    {
        super(ColliderType.FRUSTUM);
        this.position = position;
    }

    @Override
    public CollisionHandler getCollision(Collider collider, float epsilon)
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
