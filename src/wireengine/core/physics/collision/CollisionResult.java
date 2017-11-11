package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.PhysicsObject;

/**
 * @author Kelan
 */
public class CollisionResult
{
    /**
     * One of the colliding objects in this collision.
     */
    public PhysicsObject a;

    /**
     * One of the colliding objects in this collision.
     */
    public PhysicsObject b;

    /**
     * The point in the world where the first contact between
     * these two objects will happen.
     */
    public Vector3f collisionPoint;

    /**
     * The normal of the collision to be used in the collision
     * reaction. This is not necessarily the same as the normal
     * on the surface of the shape.
     */
    public Vector3f collisionNormal;

    /**
     * The number of ticks in the future that the collision will
     * happen in.
     */
    public float collisionTime;

    /**
     * True if a and b are currently intersecting.
     */
    public boolean isIntersecting;

    /**
     * True if a and b will collide with each other in the future.
     */
    public boolean willCollide;

    public CollisionResult(PhysicsObject a, PhysicsObject b, Vector3f collisionPoint, Vector3f collisionNormal, float collisionTime)
    {
        this.a = a;
        this.b = b;
        this.collisionPoint = collisionPoint;
        this.collisionNormal = collisionNormal;
        this.collisionTime = collisionTime;
    }

    public CollisionResult(PhysicsObject a, PhysicsObject b)
    {
        this(a, b, new Vector3f(), new Vector3f(), -1.0F);
    }
}
