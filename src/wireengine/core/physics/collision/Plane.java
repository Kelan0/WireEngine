package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class Plane extends Collider
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
    public CollisionHandler getCollision(Collider collider, float epsilon)
    {
        epsilon = Math.abs(epsilon);

        CollisionHandler collision = new CollisionHandler();

        if (ColliderType.RAY.equals(collider.colliderType))
        {
            Ray ray = (Ray) collider;

            float f = Vector3f.dot(normal, ray.direction);

            if (f < -epsilon || f > epsilon)
            {
                float f1 = -(Vector3f.dot(normal, ray.position) + this.distance) / f;

                if (f1 >= 0.0F)
                {
                    collision.didHit = true;
                    collision.hitPosition.x = ray.getPosition().x + ray.getDirection().x * f1;
                    collision.hitPosition.y = ray.getPosition().y + ray.getDirection().y * f1;
                    collision.hitPosition.z = ray.getPosition().z + ray.getDirection().z * f1;
                    collision.hitNormal = this.getNormalAt(null);
                }
            }
        }

        if (ColliderType.SPHERE.equals(collider.colliderType))
        {
            Sphere sphere = (Sphere) collider;

            Vector3f closest = this.getClosestPoint(sphere.getPosition());

            if (sphere.pointIntersects(closest))
            {
                collision.didHit = true;
                collision.hitPosition = closest;
                collision.hitNormal = this.getNormalAt(null);
            }
        }

        return collision;
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
        return Vector3f.dot(this.normal, Vector3f.sub(point, (Vector3f) getNormalAt(null).scale(-distance), null));
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
