package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.colliders.Plane;

/**
 * @author Kelan
 */
public class CollisionState<A extends Collider<A>, B extends Collider<B>>
{
    public CollisionComponent<A> aComponent;
    public CollisionComponent<B> bComponent;
    public boolean didHit;

    public CollisionState()
    {
        this.didHit = false;
    }

    public CollisionState(CollisionState<A, B> collision)
    {
        this.aComponent = new CollisionComponent<>(collision.aComponent);
        this.bComponent = new CollisionComponent<>(collision.bComponent);
        this.didHit = collision.didHit;
    }

    public void calculateSlidingPlanes()
    {
        aComponent.calculateSlidingPlane();
        bComponent.calculateSlidingPlane();
    }

    @Override
    public String toString()
    {
        return "CollisionState{" +
                "aComponent=" + aComponent +
                ", bComponent=" + bComponent +
                ", didHit=" + didHit +
                '}';
    }

    public static class CollisionComponent<T extends Collider<T>>
    {
        public T collider; // The object being collided with.
        public Vector3f velocity; // The velocity of this object.
        public Vector3f position; // The position of this object.
        public Vector3f collisionPoint; // The first point of contact. The centre of any line segment or area created by the first point if any.
        public Vector3f collisionNormal; // The normal at the contact point.
        public Plane slidingPlane;

        public CollisionComponent(T collider)
        {
            this.collider = collider;
            this.velocity = new Vector3f();
            this.position = new Vector3f();
            this.collisionPoint = new Vector3f();
            this.collisionNormal = new Vector3f();
            this.slidingPlane = null;
        }

        public CollisionComponent(CollisionComponent<T> component)
        {
            this.collider = component.collider;
            this.velocity = new Vector3f(component.velocity);
            this.position = new Vector3f(component.position);
            this.collisionPoint = new Vector3f(component.collisionPoint);
            this.collisionNormal = new Vector3f(component.collisionNormal);

            if (component.slidingPlane == null)
            {
                this.slidingPlane = null;
            } else
            {
                this.calculateSlidingPlane();
            }
        }

        public void calculateSlidingPlane()
        {
            Vector3f origin = collisionPoint;
            Vector3f normal = Vector3f.sub(position, origin, null).normalise(null);

            this.slidingPlane = new Plane(origin, normal);
        }

        @Override
        public String toString()
        {
            return "CollisionComponent{" +
                    "collider=" + collider +
                    ", velocity=" + velocity +
                    ", position=" + position +
                    ", collisionPoint=" + collisionPoint +
                    ", collisionNormal=" + collisionNormal +
                    ", slidingPlane=" + slidingPlane +
                    '}';
        }
    }
}
