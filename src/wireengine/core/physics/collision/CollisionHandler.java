package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.colliders.Ellipsoid;
import wireengine.core.physics.collision.colliders.Plane;
import wireengine.core.physics.collision.colliders.Triangle;

/**
 * @author Kelan
 */
public abstract class CollisionHandler<A extends Collider<A>, B extends Collider<B>>
{
    public CollisionState<A, B> collision;

    private CollisionHandler(A aCollider, B bCollider)
    {
        this.collision = new CollisionState<>();
        this.collision.aComponent = new CollisionState.CollisionComponent<>(aCollider);
        this.collision.bComponent = new CollisionState.CollisionComponent<>(bCollider);
    }

    public abstract CollisionHandler<A, B> updateCollision();

    public abstract CollisionState<A, B> getCorrectedState();

    public void setVelocity(Vector3f aVelocity, Vector3f bVelocity)
    {
        this.collision.aComponent.velocity = aVelocity;
        this.collision.bComponent.velocity = bVelocity;
    }

    public void setPosition(Vector3f aPosition, Vector3f bPosition)
    {
        this.collision.aComponent.position = aPosition;
        this.collision.bComponent.position = bPosition;
    }

    public CollisionState<A, B> getCollision()
    {
        return collision;
    }

    public static <C extends Collider<C>> CollisionHandler<Triangle, C> getHandler(Triangle triangle, C collider)
    {
        if (collider instanceof Ellipsoid)
        {
            return (CollisionHandler<Triangle, C>) new TriangleEllipsoidHandler(triangle, (Ellipsoid) collider);
        } //TODO: fix this.

        return null;
    }

    public static class TriangleEllipsoidHandler extends CollisionHandler<Triangle, Ellipsoid>
    {
        public TriangleEllipsoidHandler(Triangle aCollider, Ellipsoid bCollider)
        {
            super(aCollider, bCollider);
        }

        @Override
        public CollisionHandler<Triangle, Ellipsoid> updateCollision()
        {
            Vector3f closestOnTriangle = collision.aComponent.collider.getClosestPoint(collision.bComponent.collider.getCentre());
            Vector3f closestOnEllipsoid = collision.bComponent.collider.getClosestPoint(closestOnTriangle);

            if (collision.bComponent.collider.pointIntersects(closestOnTriangle))
            {
                collision.aComponent.collisionPoint = closestOnTriangle;
                collision.aComponent.collisionNormal = collision.aComponent.collider.getNormalAt(null);

                collision.bComponent.collisionPoint = closestOnEllipsoid;
                collision.bComponent.collisionNormal = collision.bComponent.collider.getNormalAt(closestOnEllipsoid);


                collision.didHit = true;
            }

            collision.calculateSlidingPlanes();

            return this;
        }

        @Override
        public CollisionState<Triangle, Ellipsoid> getCorrectedState()
        {
            CollisionState<Triangle, Ellipsoid> collision = new CollisionState<>(this.collision);

            collision.aComponent.velocity = getCorrectedVelocity(collision.aComponent);
            collision.bComponent.velocity = getCorrectedVelocity(collision.bComponent);
//            System.out.println(collision.aComponent.velocity + ", " + collision.bComponent.velocity);
            return collision;
        }

        private Vector3f getCorrectedVelocity(CollisionState.CollisionComponent<?> component)
        {
            Vector3f velocity = component.velocity;
            Plane slidingPlane = component.slidingPlane;

            if (slidingPlane != null)
            {
                Vector3f startPos = component.position;
                Vector3f endPos = Vector3f.add(startPos, component.velocity, null);

                float signedDisance = slidingPlane.getSignedDistance(endPos);
//                if (signedDisance < 0.0F) //The position after moving by the given velocity is behind the sliding plane. We need to slide.
                {
                    endPos = slidingPlane.getClosestPoint(endPos);
                    Vector3f.sub(endPos, (Vector3f) slidingPlane.getNormalAt(null).scale(signedDisance), null);
                    velocity = Vector3f.sub(endPos, startPos, null);
                }
            }

            return velocity;
        }
    }
}
