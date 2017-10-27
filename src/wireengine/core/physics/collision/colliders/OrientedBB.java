package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionState;
import wireengine.core.rendering.Axis;
import wireengine.core.util.MathUtils;

/**
 * @author Kelan
 */
public class OrientedBB extends Collider<OrientedBB>
{
    protected Vector3f position;
    protected Vector3f dimensions;
    protected Axis axis;

    public OrientedBB(Vector3f position, Vector3f dimensions, Axis axis)
    {
        super(ColliderType.OBB);
        this.position = position;
        this.dimensions = dimensions;
        this.axis = axis;
    }

    @Override
    public CollisionState.CollisionComponent<OrientedBB> getCollision(AxisAlignedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<OrientedBB> getCollision(Ellipsoid collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<OrientedBB> getCollision(Frustum collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<OrientedBB> getCollision(OrientedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<OrientedBB> getCollision(Plane collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<OrientedBB> getCollision(Ray collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<OrientedBB> getCollision(Sphere collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<OrientedBB> getCollision(Triangle collider)
    {
        return null;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        point = this.axis.negate(null).transform(point, null); // Translate point to the OBB's relative coordinate system.
        Vector3f halfDimensions = (Vector3f) dimensions.scale((float) 0.5);
        Vector3f min = Vector3f.sub(this.position, halfDimensions, null);
        Vector3f max = Vector3f.add(this.position, halfDimensions, null);

        return point.x > min.x && point.x < max.x && point.y > min.y && point.y < max.y && point.z > min.z && point.z < max.z;
    }

    @Override
    public Vector3f getClosestPoint(Vector3f point)
    {
        point = this.axis.negate(null).transform(point, null); // Translate point to the OBB's relative coordinate system.
        Vector3f halfDimensions = (Vector3f) dimensions.scale((float) 0.5);
        Vector3f min = Vector3f.sub(this.position, halfDimensions, null);
        Vector3f max = Vector3f.add(this.position, halfDimensions, null);

        Vector3f closest = new Vector3f();

        closest.x = MathUtils.clamp(point.x, min.x, max.x);
        closest.y = MathUtils.clamp(point.y, min.y, max.y);
        closest.z = MathUtils.clamp(point.z, min.z, max.z);

        return closest;
    }

    @Override
    public Vector3f getNormalAt(Vector3f point)
    {
        Vector3f normal = new Vector3f();

        float min = Float.MAX_VALUE;

        point = Vector3f.sub(point, this.position, null);

        float xDistance = Math.abs(this.dimensions.x - Math.abs(point.x));
        if (xDistance < min)
        {
            min = xDistance;
            normal = (Vector3f) axis.getX().scale(MathUtils.sign(point.x));
        }

        float yDistance = Math.abs(this.dimensions.y - Math.abs(point.y));
        if (yDistance < min)
        {
            min = yDistance;
            normal = (Vector3f) axis.getY().scale(MathUtils.sign(point.y));
        }

        float zDistance = Math.abs(this.dimensions.z - Math.abs(point.z));
        if (zDistance < min)
        {
            min = zDistance;
            normal = (Vector3f) axis.getZ().scale(MathUtils.sign(point.z));
        }

        return normal;
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

    public Vector3f getDimensions()
    {
        return dimensions;
    }

    public void setDimensions(Vector3f dimensions)
    {
        this.dimensions = dimensions;
    }

    public Axis getAxis()
    {
        return axis;
    }

    public void setAxis(Axis axis)
    {
        this.axis = axis;
    }
}
