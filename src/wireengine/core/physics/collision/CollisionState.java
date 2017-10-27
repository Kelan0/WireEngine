package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.colliders.Plane;

/**
 * @author Kelan
 */
public class Collision<A extends Collider<A>, B extends Collider<B>>
{
    public CollisionComponent<A> aComponent;
    public CollisionComponent<B> bComponent;
    public Plane slidingPlane;
    public boolean didHit;

    public Collision()
    {
        this.didHit = false;
    }

    public void calculateSlidingPlane()
    {
        Vector3f origin = bComponent.collisionPoint;
        Vector3f normal = Vector3f.sub(bComponent.startPosition, origin, null).normalise(null);

        this.slidingPlane = new Plane(origin, normal);
    }

    @Override
    public String toString()
    {
        return "Collision{" + "aComponent=" + aComponent + ", bComponent=" + bComponent + ", slidingPlane=" + slidingPlane + ", didHit=" + didHit + '}';
    }

    public static class CollisionComponent<T extends Collider<T>>
    {
        public T collider; // The object being collided with.
        public Vector3f velocity; // The velocity of this object.
        public Vector3f startPosition; // The starting position of this object.
        public Vector3f collisionPoint; // The first point of contact. The centre of any line segment or area created by the first point if any.
        public Vector3f collisionNormal; // The normal at the contact point.

        public CollisionComponent(T collider)
        {
            this.collider = collider;
            this.velocity = new Vector3f();
            this.startPosition = new Vector3f();
            this.collisionPoint = new Vector3f();
            this.collisionNormal = new Vector3f();
        }

        @Override
        public String toString()
        {
            return "CollisionComponent{" + "collider=" + collider + ", velocity=" + velocity + ", startPosition=" + startPosition + ", collisionPoint=" + collisionPoint + ", collisionNormal=" + collisionNormal + '}';
        }
    }
}
