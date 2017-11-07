package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class CollisionState<A extends Collider<A>, B extends Collider<B>>
{
    public CollisionComponent<A> aComponent;// The collision component for the A collider.
    public CollisionComponent<B> bComponent; // The collision component for the B collider.
    public Vector3f collisionPoint; // The point of contact between the two colliders. Stored as a separate variable because the collision point may not lie on the velocity vector.
    public float collisionTime; // The distance between two ticks that the collision happened. 0.5 for example means the collision happened exactly half way between two ticks.
    public float collisionDistance; // The distance along the velocity vector that the collision will happen. This is stord separately because the velocity might change. If the velocity is constant this can be calculated from collisionTime
    public boolean didHit; // Whether or not there was a collision. Other variables shouldn't be accessed if this is false as they may be null or undefined.

    public CollisionState()
    {
        this.didHit = false;
        this.collisionPoint = new Vector3f();
        this.collisionTime = 1.0F; // Collision time starts at the end of the tick, as some collision detection algorithms try to find the lowest value.
        this.collisionDistance = 0.0F;
    }

    public CollisionState(CollisionState<A, B> collision)
    {
        this.aComponent = new CollisionComponent<>(collision.aComponent);
        this.bComponent = new CollisionComponent<>(collision.bComponent);
        this.collisionPoint = collision.collisionPoint;
        this.collisionTime = collision.collisionTime;
        this.collisionDistance = collision.collisionDistance;
        this.didHit = collision.didHit;
    }

    @Override
    public String toString()
    {
        return "CollisionState{" + "aComponent=" + aComponent + ", bComponent=" + bComponent + ", didHit=" + didHit + '}';
    }

    public static class CollisionComponent<T extends Collider<T>>
    {
        public T collider; // The collider for this object.
        public Vector3f velocity; // The velocity of this object.
        public Vector3f position; // The position of this object.
        public Vector3f collisionPoint; // The first point of contact on the surface of this collider.
        public Vector3f collisionNormal; // The normal at the first point of contact on the surface..

        public CollisionComponent(T collider)
        {
            this.collider = collider;
            this.velocity = new Vector3f();
            this.position = new Vector3f();
            this.collisionPoint = new Vector3f();
            this.collisionNormal = new Vector3f();
        }

        public CollisionComponent(CollisionComponent<T> component)
        {
            this.collider = component.collider;
            this.velocity = new Vector3f(component.velocity);
            this.position = new Vector3f(component.position);
            this.collisionPoint = new Vector3f(component.collisionPoint);
            this.collisionNormal = new Vector3f(component.collisionNormal);
        }

        @Override
        public String toString()
        {
            return "CollisionComponent{" + "collider=" + collider + ", velocity=" + velocity + ", position=" + position + ", collisionPoint=" + collisionPoint + ", collisionNormal=" + collisionNormal + '}';
        }
    }
}
