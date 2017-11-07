package wireengine.core.physics.collision;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.PhysicsObject;
import wireengine.core.physics.collision.colliders.*;
import wireengine.core.util.MathUtils;

import java.nio.FloatBuffer;

/**
 * @author Kelan
 */
public abstract class CollisionHandler<A extends Collider<A>, B extends Collider<B>>
{
    public CollisionState<A, B> collision;
    public Triangle collidingObject;

    private CollisionHandler(A aCollider, B bCollider)
    {
        this.collision = new CollisionState<>();
        this.collision.aComponent = new CollisionState.CollisionComponent<>(aCollider);
        this.collision.bComponent = new CollisionState.CollisionComponent<>(bCollider);
    }

    public synchronized Triangle getCollidingObject()
    {
        return collidingObject;
    }

    public abstract CollisionHandler<A, B> handleCollisions(PhysicsObject<A> aObject, PhysicsObject<B> bObject, double delta);

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

    public static <C extends Collider<C>> CollisionHandler<AxisAlignedBB, C> getHandler(AxisAlignedBB triangle, C collider)
    {
        return null;
    }

    public static <C extends Collider<C>> CollisionHandler<Ellipsoid, C> getHandler(Ellipsoid triangle, C collider)
    {
        return null;
    }

    public static <C extends Collider<C>> CollisionHandler<Frustum, C> getHandler(Frustum triangle, C collider)
    {
        return null;
    }

    public static <C extends Collider<C>> CollisionHandler<OrientedBB, C> getHandler(OrientedBB triangle, C collider)
    {
        return null;
    }

    public static <C extends Collider<C>> CollisionHandler<Plane, C> getHandler(Plane triangle, C collider)
    {
        return null;
    }

    public static <C extends Collider<C>> CollisionHandler<Ray, C> getHandler(Ray triangle, C collider)
    {
        return null;
    }

    public static <C extends Collider<C>> CollisionHandler<Sphere, C> getHandler(Sphere triangle, C collider)
    {
        return null;
    }

    public static <C extends Collider<C>> CollisionHandler<Triangle, C> getHandler(Triangle triangle, C collider)
    {
        return null;
    }

    public static <C extends Collider<C>> CollisionHandler<TriangleMesh, C> getHandler(TriangleMesh mesh, C collider)
    {
        if (collider instanceof Ellipsoid)
        {
            return (CollisionHandler<TriangleMesh, C>) new TriangleMeshEllipsoidHandler(mesh, (Ellipsoid) collider);
        }

        return null;
    }

    public static class TriangleEllipsoidHandler extends CollisionHandler<Triangle, Ellipsoid>
    {
        public TriangleEllipsoidHandler(Triangle aCollider, Ellipsoid bCollider)
        {
            super(aCollider, bCollider);
        }

        @Override
        //Can pass null physics object to this function. Collider will be treated as static. If both are null, no collision is detected.
        public CollisionHandler<Triangle, Ellipsoid> handleCollisions(PhysicsObject<Triangle> aObject, PhysicsObject<Ellipsoid> bObject, double delta)
        {
            return this;
        }
    }

    public static class TriangleMeshEllipsoidHandler extends CollisionHandler<TriangleMesh, Ellipsoid>
    {
        private int depth = 0;

        public TriangleMeshEllipsoidHandler(TriangleMesh aCollider, Ellipsoid bCollider)
        {
            super(aCollider, bCollider);
        }

        @Override
        public CollisionHandler<TriangleMesh, Ellipsoid> handleCollisions(PhysicsObject<TriangleMesh> aObject, PhysicsObject<Ellipsoid> bObject, double delta)
        {
            if ((aObject == null && bObject == null) || delta == 0.0)
            {
                return this;
            }

            Vector3f aPosition = aObject == null ? new Vector3f() : new Vector3f(aObject.getPosition());
            Vector3f bPosition = bObject == null ? new Vector3f() : new Vector3f(bObject.getPosition());
            Vector3f aVelocity = aObject == null ? new Vector3f() : (Vector3f) new Vector3f(aObject.getVelocity()).scale((float) (delta));
            Vector3f bVelocity = bObject == null ? new Vector3f() : (Vector3f) new Vector3f(bObject.getVelocity()).scale((float) (delta));

            this.setPosition(aPosition, bPosition);
            this.setVelocity(aVelocity, bVelocity);

            this.collision.bComponent.collider.transformPoints(aPosition, bPosition, aVelocity, bVelocity);

            Ellipsoid ellipsoid = this.collision.bComponent.collider;
            Vector3f position = new Vector3f(this.collision.bComponent.position);
            Vector3f velocity = new Vector3f(this.collision.bComponent.velocity);

            if (doCollision(ellipsoid, position, velocity))
            {
                ellipsoid.transformPoints(true, position, velocity);
                bObject.getVelocity().set((ReadableVector3f) velocity.scale((float) (1.0 / delta)));
            }
            return this;
        }

        public boolean doCollision(Ellipsoid ellipsoid, Vector3f position, Vector3f velocity)
        {
            float epsilon = 0.005F;

            for (int i = 0; i < 1; i++)
            {
                CollisionState<Triangle, Ellipsoid> collision = getClosestCollision(ellipsoid, position, velocity);

                if (collision.didHit)
                {

                } else
                {
                    return false;
                }
            }

            return true; //We went through all iterations without returning, therefor we must have collided.
        }

        private CollisionState<Triangle, Ellipsoid> getClosestCollision(Ellipsoid ellipsoid, Vector3f startPosition, Vector3f startVelocity)
        {
            CollisionState<Triangle, Ellipsoid> closestCollision = new CollisionState<>();
            for (Triangle triangle : this.collision.aComponent.collider.getTriangles())
            {
                Triangle eTriangle = new Triangle(triangle);

                ellipsoid.transformPoints(eTriangle.getP1(), eTriangle.getP2(), eTriangle.getP3());

                if (Vector3f.dot(startVelocity, eTriangle.getNormalAt(null)) > 0.0F) //Triangle normal and velocity are in the same direction. Skip this triangle.
                {
                    continue;
                }

                float t0, t1;

                Vector3f closestPoint = eTriangle.getClosestPoint(startPosition);
                Vector3f sphereToTriangle = Vector3f.sub(startPosition, closestPoint, null);

                float a = startVelocity.lengthSquared();
                float b = 2.0F * Vector3f.dot(startVelocity, sphereToTriangle);
                float c = sphereToTriangle.lengthSquared() - 1.0F;

                boolean flag = false;
                FloatBuffer roots = BufferUtils.createFloatBuffer(2);

                if (sphereToTriangle.lengthSquared() < 1.0F) //Overlapping
                {
                    t0 = t1 = 0.0F;
                    flag = true;
                } else if (MathUtils.getRoots(a, b, c, roots)) //Going to collide.
                {
                    t0 = roots.get();
                    t1 = roots.get();
                    flag = t0 >= 0.0F && t1 <= 1.0F;
                } else
                {
                    t0 = t1 = Float.NaN;
                }

                if (flag && !Float.isNaN(t0 + t1))
                {
                    float distance = t0 * startVelocity.length();
//                    float distance = collisionTime * collisionTime * ellipsoidVel.lengthSquared(); // squared, avoid the square root.

                    if (!closestCollision.didHit || closestCollision.collisionDistance > distance)
                    {
                        closestCollision.aComponent = new CollisionState.CollisionComponent<>(eTriangle);
                        closestCollision.bComponent = new CollisionState.CollisionComponent<>(ellipsoid);
                        closestCollision.didHit = true;
                        closestCollision.collisionDistance = distance;
                        closestCollision.collisionPoint = closestPoint;
                        closestCollision.collisionTime = t0;
                        //TODO render the closest triangle for debufgging. What is causing weird colliding?
                    }
                }
            }

            return closestCollision;
        }

        private boolean checkIntersection(CollisionState<Triangle, Ellipsoid> collision)
        {
            Vector3f triangleVel = new Vector3f(this.collision.aComponent.velocity);
            Vector3f trianglePos = new Vector3f(this.collision.aComponent.position);
            Vector3f ellipsoidVel = new Vector3f(this.collision.bComponent.velocity);
            Vector3f ellipsoidPos = new Vector3f(this.collision.bComponent.position);

            Ellipsoid ellipsoid = collision.bComponent.collider;
            Triangle triangle = new Triangle(collision.aComponent.collider);

            if (Vector3f.dot(triangle.getNormalAt(null), ellipsoidVel) <= 0.0F) // If we are moving towards the triangle we want to detect a collision.
            {
                ellipsoid.transformPoints(triangle.getP1(), triangle.getP2(), triangle.getP3());

                Vector3f collisionPoint = null;
                float collisionTime = 1.0F;
                float distanceToPlane = triangle.getSignedDistance(ellipsoidPos);
                float normalDotVelocity = Vector3f.dot(triangle.getNormalAt(null), ellipsoidVel);
                boolean planeIntersection, didHit = false;

                float t0, t1;

                if (normalDotVelocity == 0.0F) // Sphere is travelling paralell to the pane of the triangle.
                {
                    if (Math.abs(distanceToPlane) >= 1.0F) // The sphere is not intersecting the plane. Given the above statement, a collision is not possible.
                    {
                        return false; // No collision, ellipsoid is travelling parallel to the plane of the triangle and is not intersecting.
                    } else
                    {
                        t0 = t1 = 0.0F;
                        planeIntersection = true; // Sphere and plane are intersecting.
                    }
                } else
                {
                    float f0 = ((+1.0F - distanceToPlane) / normalDotVelocity);
                    float f1 = ((-1.0F - distanceToPlane) / normalDotVelocity);

                    t0 = Math.min(f0, f1);
                    t1 = Math.max(f0, f1);

                    if (t0 > 1.0F || t1 < 0.0F)
                    {
                        return false; // Possible collision but it does not happen in this tick. Do not worry about it now.
                    }

                    t0 = MathUtils.clamp(t0, 0.0F, 1.0F);
                    t1 = MathUtils.clamp(t1, 0.0F, 1.0F);

                    planeIntersection = false;
                }

                if (!planeIntersection)
                {
                    Vector3f v1 = (Vector3f) new Vector3f(ellipsoidVel).scale(t0);
                    Vector3f v2 = Vector3f.sub(v1, triangle.getNormalAt(null), null);

                    collisionPoint = Vector3f.add(ellipsoidPos, v2, null);
                    if (pointInsideTriangle(collisionPoint, triangle))
//                    if (triangle.pointIntersects(collisionPoint))
                    {
                        collisionTime = t0;
                        didHit = true;
                    }
                }

                if (!didHit)
                {
                    Vector3f[] points = new Vector3f[]{triangle.getP1(), triangle.getP2(), triangle.getP3()};

                    // Loops through every pair of vertices in the triangle and checks for a collision with both the vertices and the edges.
                    for (int i = 0, j = points.length - 1; i < points.length; j = i++)
                    {
                        Vector3f vertex = points[i];
                        Vector3f vertexToPos = Vector3f.sub(ellipsoidPos, vertex, null);
                        Vector3f edge = Vector3f.sub(points[j], vertex, null);
                        float edgeDotVelocity = Vector3f.dot(edge, ellipsoidVel);
                        float edgeDotPosToVertex = Vector3f.dot(edge, vertexToPos.negate(null));
                        float root, a, b, c;

                        a = ellipsoidVel.lengthSquared();
                        b = 2.0F * Vector3f.dot(ellipsoidVel, vertexToPos);
                        c = vertexToPos.lengthSquared() - 1.0F;
                        root = getLowestRoot(a, b, c, collisionTime);

                        // Check for a collision with this vertex of the triangle.
                        if (!Float.isNaN(root))
                        {
                            collisionTime = root;
                            didHit = true;
                            collisionPoint = new Vector3f(vertex); //Copy the vertex so that editing collision.collisionPoint outside this function does not change the vertex.
                        }


                        a = edge.lengthSquared() * -ellipsoidVel.lengthSquared() + (edgeDotVelocity * edgeDotVelocity);
                        b = edge.lengthSquared() * (2.0F * Vector3f.dot(ellipsoidVel, vertexToPos.negate(null))) - (2.0F * edgeDotVelocity * edgeDotPosToVertex);
                        c = edge.lengthSquared() * (1.0F - vertexToPos.negate(null).lengthSquared()) + (edgeDotPosToVertex * edgeDotPosToVertex);
                        root = getLowestRoot(a, b, c, collisionTime);

                        // Check for a collision with this edge of the triangle. We check both because we don'collision.collisionTime know what happened first.
                        if (!Float.isNaN(root))
                        {
                            float f = (edgeDotVelocity * root - edgeDotPosToVertex) / edge.lengthSquared();
                            if (f >= 0.0F && f <= 1.0F)
                            {
                                collisionTime = root;
                                didHit = true;
                                collisionPoint = Vector3f.add(vertex, (Vector3f) edge.scale(f), null);
                            }
                        }
                    }
                }

                if (didHit)
                {
                    float distance = collisionTime * ellipsoidVel.length();
//                    float distance = collisionTime * collisionTime * ellipsoidVel.lengthSquared(); // squared, avoid the square root.

                    if (!collision.didHit || distance < collision.collisionDistance)
                    {
                        collision.didHit = true;
                        collision.collisionDistance = distance;
                        collision.collisionPoint = collisionPoint;
                        collision.collisionTime = collisionTime;
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean pointInsideTriangle(Vector3f p, Triangle triangle)
        {
            //Implementation adapted from https://www.braynzarsoft.net/viewtutorial/q16390-31-sliding-camera-collision-detection
            Vector3f a = triangle.getP1();
            Vector3f b = triangle.getP2();
            Vector3f c = triangle.getP3();
            Vector3f cp1, cp2;

            cp1 = Vector3f.cross(Vector3f.sub(c, b, null), Vector3f.sub(p, b, null), null);
            cp2 = Vector3f.cross(Vector3f.sub(c, b, null), Vector3f.sub(a, b, null), null);
            if (Vector3f.dot(cp1, cp2) >= 0.0F)
            {
                cp1 = Vector3f.cross(Vector3f.sub(c, a, null), Vector3f.sub(p, a, null), null);
                cp2 = Vector3f.cross(Vector3f.sub(c, a, null), Vector3f.sub(b, a, null), null);
                if (Vector3f.dot(cp1, cp2) >= 0.0F)
                {
                    cp1 = Vector3f.cross(Vector3f.sub(b, a, null), Vector3f.sub(p, a, null), null);
                    cp2 = Vector3f.cross(Vector3f.sub(b, a, null), Vector3f.sub(c, a, null), null);
                    if (Vector3f.dot(cp1, cp2) >= 0.0F)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        private float getLowestRoot(float a, float b, float c, float threshold)
        {
            float determinant = b * b - 4.0F * a * c;
            if (determinant < 0.0F)
            {
                return Float.NaN;
            }

            float sqrtD = (float) Math.sqrt(determinant);
            float temp1 = (-b - sqrtD) / (2.0F * a);
            float temp2 = (-b + sqrtD) / (2.0F * a);
            float x1 = Math.min(temp1, temp2);
            float x2 = Math.max(temp1, temp2);

            if (x1 > 0 && x1 < threshold)
            {
                return x1;
            }

            if (x2 > 0 && x2 < threshold)
            {
                return x2;
            }

            return Float.NaN;
        }
    }
}
