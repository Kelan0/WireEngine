package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionState;

/**
 * @author Kelan
 */
public class Plane extends Collider<Plane>
{
    protected Vector3f normal;
    protected float distance; // The constant for the plane's equation.

    public Plane(Vector3f position, Vector3f normal)
    {
        super(ColliderType.PLANE);
        this.set(position, normal);
    }

    public Plane(Vector3f normal, float distance)
    {
        this(normal.normalise(normal), new Vector3f(normal.x * distance, normal.y * distance, normal.z * distance));
    }

    public void set(Vector3f position, Vector3f normal)
    {
        if (normal == null || position == null)
        {
            throw new IllegalStateException("Cannot construct a plane from null data.");
        }

        if (normal.lengthSquared() == 0.0F || position.lengthSquared() == Float.NaN)
        {
            throw new IllegalStateException("Cannot construct a plane from a zero-length normal vector.");
        }

        this.normal = new Vector3f(normal);
        if (this.normal.lengthSquared() != 1.0F)
        {
            this.normal.normalise();
        }

        this.distance = -Vector3f.dot(this.normal, position);
    }

    @Override
    public CollisionState.CollisionComponent<Plane> getCollision(AxisAlignedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Plane> getCollision(Ellipsoid collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Plane> getCollision(Frustum collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Plane> getCollision(OrientedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Plane> getCollision(Plane collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Plane> getCollision(Ray collider)
    {
//        CollisionState.CollisionComponent<Plane> collision = new CollisionState.CollisionComponent<>(this);
//        float f = Vector3f.dot(normal, collider.direction);
//        float epsilon = 0.001F;
//
//        if (f < -epsilon || f > epsilon)
//        {
//            float f1 = -(Vector3f.dot(normal, collider.position) + this.distance) / f;
//
//            if (f1 >= 0.0F)
//            {
//                Vector3f hitPoint = new Vector3f();
//                hitPoint.x = collider.getPosition().x + collider.getDirection().x * f1;
//                hitPoint.y = collider.getPosition().y + collider.getDirection().y * f1;
//                hitPoint.z = collider.getPosition().z + collider.getDirection().z * f1;
//                collision.collisionPoint = hitPoint;
//                collision.collisionNormal = this.getNormalAt(null);
//                return collision;
//            }
//        }

        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Plane> getCollision(Sphere collider)
    {
        CollisionState.CollisionComponent<Plane> collision = new CollisionState.CollisionComponent<>(this);
        Vector3f closest = this.getClosestPoint(collider.getPosition());

        if (collider.pointIntersects(closest))
        {
//            float r = collider.radius;
//            float d = this.getSignedDistance(collider.getCentre());
//
//            collision.didHit = true;
//            collision.intersection = new Circle(closest, this.getNormalAt(null), (float) Math.sqrt(r * r - d * d)); //Can safely assume this isn't a square root of a negative number, as the closest point on the plane is inside this sphere.

            collision.collisionPoint = closest;
            collision.collisionNormal = this.getNormalAt(closest);

            return collision;
        }

        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Plane> getCollision(Triangle collider)
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
        return Vector3f.add(point, (Vector3f) getNormalAt(null).scale(-getSignedDistance(point)), null);
    }

    @Override
    public Vector3f getNormalAt(Vector3f point)
    {
        return new Vector3f(this.normal);
    }

    @Override
    public Vector3f getPosition()
    {
        return new Vector3f(normal.x * distance, normal.y * distance, normal.z * distance);
    }

    @Override
    public void setPosition(Vector3f position)
    {
        this.set(position, this.normal);
    }

    public float getSignedDistance(Vector3f point)
    {
        return (Vector3f.dot(normal, point) + this.distance); // / normal.lengthSquared();
    }

    public void setNormal(Vector3f normal)
    {
        this.set(new Vector3f(normal.x * this.distance, normal.y * this.distance, normal.z * this.distance), normal);
    }

    public float getDistance()
    {
        return distance;
    }

    public void setDistance(float distance)
    {
        this.distance = distance;
    }
}
