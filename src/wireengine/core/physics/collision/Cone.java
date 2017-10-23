package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class Cone extends Collider
{
    protected Plane plane; //A plane intersecting this cone orthogonal to the up-axis.
    protected Vector3f position;
    protected float baseRadius;
    protected float height;

    public Cone(Vector3f position, Vector3f up, float baseRadius, float height)
    {
        super(ColliderType.CONE);
        this.plane = new Plane(position, up);
        this.position = position;
        this.baseRadius = baseRadius;
        this.height = height;
    }

    @Override
    public CollisionHandler getCollision(Collider collider, float epsilon)
    {
        return null;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        Vector3f tip = (Vector3f) this.getUp().scale(this.height);

        Vector3f tipToPoint = Vector3f.sub(point, tip, null);

        float axisDistance = Vector3f.dot(tipToPoint, this.getUp()); //distance alon the up-vector.

        if (axisDistance >= -this.height * 0.5F && axisDistance < this.height * 0.5F)
        {
            float currentRadius = (axisDistance / this.height) * this.baseRadius;
            float radialDistanceSquared = Vector3f.sub(tipToPoint, (Vector3f) this.getUp().scale(axisDistance), null).lengthSquared();

            return radialDistanceSquared < currentRadius * currentRadius;
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

    public Vector3f getUp()
    {
        return new Vector3f(this.plane.getNormalAt(null));
    }

    public void setUp(Vector3f up)
    {
        this.plane.set(position, up);
    }

    @Override
    public void setPosition(Vector3f position)
    {
        this.position = position;
    }
}
