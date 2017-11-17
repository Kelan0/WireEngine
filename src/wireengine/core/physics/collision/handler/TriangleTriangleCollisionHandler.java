package wireengine.core.physics.collision.handler;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.PhysicsObject;
import wireengine.core.physics.collision.CollisionHelper;
import wireengine.core.physics.collision.CollisionResult;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class TriangleTriangleCollisionHandler
{
    public static List<CollisionResult> getCollision(PhysicsObject aObject, Triangle[] staticColliders, double delta)
    {
        List<CollisionResult> collisions = new ArrayList<>();

//        for (Triangle ta : aObject.getColliders())
//        {
//            for (Triangle tb : staticColliders)
//            {
//                CollisionResult collision = checkTriangles(new SweptTriangle(ta, aObject.getVelocity()), new SweptTriangle(tb, new Vector3f()));
//            }
//        }

        return collisions;
    }

    public static List<CollisionResult> getCollision(PhysicsObject aObject, PhysicsObject bObject, double delta)
    {
        List<CollisionResult> collisions = new ArrayList<>();

        for (Triangle ta : aObject.getColliders())
        {
            for (Triangle tb : bObject.getColliders())
            {
                float epsilon = 0.0001F;
                CollisionResult collision = new CollisionResult(aObject, bObject);

                SweptTriangle sta = new SweptTriangle(ta, aObject.getVelocity());
                SweptTriangle stb = new SweptTriangle(tb, bObject.getVelocity());

                if (checkTriangles(sta, stb, collision, delta))
                {
                    collisions.add(collision);
                }
            }
        }

        return collisions;
    }

    public static boolean checkTriangles(SweptTriangle t0, SweptTriangle t1, CollisionResult collisionResult, double delta)
    {
        Vector3f relativeVelocity = Vector3f.sub(t0.velocity, t1.velocity, null);
        float epsilon = 0.0001F;
        float maxDistance = (float) (Math.max(Math.abs(Vector3f.dot(relativeVelocity, t0.getNormal())), Math.abs(Vector3f.dot(relativeVelocity, t1.getNormal()))) * delta + epsilon);

        // Stats
//    mNbPrimPrimTests++;
        Vector3f a0 = t0.getP1();
        Vector3f a1 = t0.getP2();
        Vector3f a2 = t0.getP3();
        Vector3f b0 = t1.getP1();
        Vector3f b1 = t1.getP2();
        Vector3f b2 = t1.getP3();

        // Compute plane of t0
        Vector3f n1 = new Vector3f(t0.getNormal());
        float d1 = Vector3f.dot(n1.negate(null), a0);

        // Calculate the signed distance from each vertex of t1 to the plane of t0.
        float da0 = Vector3f.dot(n1, b0) + d1;
        da0 = Math.abs(da0) < epsilon ? 0.0F : da0;
        float da1 = Vector3f.dot(n1, b1) + d1;
        da1 = Math.abs(da1) < epsilon ? 0.0F : da1;
        float da2 = Vector3f.dot(n1, b2) + d1;
        da2 = Math.abs(da2) < epsilon ? 0.0F : da2;

        float da0da1 = da0 * da1;
        float da0da2 = da0 * da2;

        if (da0da1 > maxDistance && da0da2 > maxDistance)
        {
            return false; // If the distance of every vertex has the same sign, they are all on the same side of the plane, and therefor cannot be colliding.
        }

        // Compute plane of t1
        Vector3f n2 = new Vector3f(t1.getNormal());
        float d2 = Vector3f.dot(n2.negate(null), b0);

        // Calculate the signed distance from each vertex of t0 to the plane of t1.
        float dv0 = Vector3f.dot(n2, a0) + d2;
        dv0 = Math.abs(dv0) < epsilon ? 0.0F : dv0;
        float dv1 = Vector3f.dot(n2, a1) + d2;
        dv1 = Math.abs(dv1) < epsilon ? 0.0F : dv1;
        float dv2 = Vector3f.dot(n2, a2) + d2;
        dv2 = Math.abs(dv2) < epsilon ? 0.0F : dv2;

        float db0db1 = dv0 * dv1;
        float db0db2 = dv0 * dv2;

        if (db0db1 > maxDistance && db0db2 > maxDistance)
        {
            return false; // No collision if all vertices are on the same side of the plane.
        }

        // The direction of the line of intersection is the cross product between the two normals of the triangles.
        Vector3f direction = Vector3f.cross(n1, n2, null);

        // index of the largest absolute component of direction. No clean looking way of doing this.
        int index = Math.abs(direction.x) > Math.abs(direction.y) ? (Math.abs(direction.x) > Math.abs(direction.z) ? 0 : 2) : (Math.abs(direction.y) > Math.abs(direction.z) ? 1 : 2);

        // project the vertices onto the line of intersection.
        float ap0 = MathUtils.getVectorElement(a0, index);
        float ap1 = MathUtils.getVectorElement(a1, index);
        float ap2 = MathUtils.getVectorElement(a2, index);
        float bp0 = MathUtils.getVectorElement(b0, index);
        float bp1 = MathUtils.getVectorElement(b1, index);
        float bp2 = MathUtils.getVectorElement(b2, index);

        // Compute intervals for triangle 1
        float[] intervals1 = new float[5];
        if (computeIntervals(ap0, ap1, ap2, dv0, dv1, dv2, intervals1))
        {
            return checkCoplanarTriangles(n1, t0, t1, collisionResult, delta);
        }
        // Compute intervals for triangle 2
        float[] intervals2 = new float[5];
        if (computeIntervals(bp0, bp1, bp2, da0, da1, da2, intervals2))
        {
            return checkCoplanarTriangles(n1, t0, t1, collisionResult, delta);
        }

        Vector3f intersection = new Vector3f();

        if (CollisionHelper.segmentPlaneIntersection(b0, b1, n1, a0, intersection))
            collisionResult.intersectionPoints.add(new Vector3f(intersection));
        if (CollisionHelper.segmentPlaneIntersection(b1, b2, n1, a0, intersection))
            collisionResult.intersectionPoints.add(new Vector3f(intersection));
        if (CollisionHelper.segmentPlaneIntersection(b2, b0, n1, a0, intersection))
            collisionResult.intersectionPoints.add(new Vector3f(intersection));
        if (CollisionHelper.segmentPlaneIntersection(a0, a1, n2, b0, intersection))
            collisionResult.intersectionPoints.add(new Vector3f(intersection));
        if (CollisionHelper.segmentPlaneIntersection(a1, a2, n2, b0, intersection))
            collisionResult.intersectionPoints.add(new Vector3f(intersection));
        if (CollisionHelper.segmentPlaneIntersection(a2, a0, n2, b0, intersection))
            collisionResult.intersectionPoints.add(new Vector3f(intersection));

        float xx = intervals1[3] * intervals1[4];
        float yy = intervals2[3] * intervals2[4];
        float xxyy = xx * yy;

        float[] isect1 = new float[2];
        float[] isect2 = new float[2];

        float tmp = intervals1[0] * xxyy;
        isect1[0] = tmp + intervals1[1] * intervals1[4] * yy;
        isect1[1] = tmp + intervals1[2] * intervals1[3] * yy;

        tmp = intervals2[0] * xxyy;
        isect2[0] = tmp + intervals2[1] * xx * intervals2[4];
        isect2[1] = tmp + intervals2[2] * xx * intervals2[3];

        return !(Math.max(isect1[0], isect1[1]) < Math.min(isect2[0], isect2[1])) && !(Math.max(isect2[0], isect2[1]) < Math.min(isect1[0], isect1[1]));
    }

    private static boolean computeIntervals(float proj0, float proj1, float proj2, float dist0, float dist1, float dist2, float[] intervals)
    {
        float d0d1 = dist0 * dist1;
        float d0d2 = dist0 * dist2;

        if (d0d1 > 0.0F)
        {
        /* here we know that d0d2<=0.0 */
        /* that is dist0, dist1 are on the same side, dist2 on the other or on the plane */
            intervals[0] = proj2;
            intervals[1] = (proj0 - proj2) * dist2;
            intervals[2] = (proj1 - proj2) * dist2;
            intervals[3] = dist2 - dist0;
            intervals[4] = dist2 - dist1;
        } else if (d0d2 > 0.0F)
        {
        /* here we know that d0d1<=0.0 */
            intervals[0] = proj1;
            intervals[1] = (proj0 - proj1) * dist1;
            intervals[2] = (proj2 - proj1) * dist1;
            intervals[3] = dist1 - dist0;
            intervals[4] = dist1 - dist2;
        } else if (dist1 * dist2 > 0.0F || dist0 != 0.0F)
        {
        /* here we know that d0d1<=0.0 or that dist0!=0.0 */
            intervals[0] = proj0;
            intervals[1] = (proj1 - proj0) * dist0;
            intervals[2] = (proj2 - proj0) * dist0;
            intervals[3] = dist0 - dist1;
            intervals[4] = dist0 - dist2;
        } else if (dist1 != 0.0F)
        {
            intervals[0] = proj1;
            intervals[1] = (proj0 - proj1) * dist1;
            intervals[2] = (proj2 - proj1) * dist1;
            intervals[3] = dist1 - dist0;
            intervals[4] = dist1 - dist2;
        } else if (dist2 != 0.0F)
        {
            intervals[0] = proj2;
            intervals[1] = (proj0 - proj2) * dist2;
            intervals[2] = (proj1 - proj2) * dist2;
            intervals[3] = dist2 - dist0;
            intervals[4] = dist2 - dist1;
        } else
        {
            // Triangles close enough to be considered in the same plane. (They are coplanar)
            return true;
        }

        return false;
    }

    private static boolean checkCoplanarTriangles(Vector3f normal, SweptTriangle t0, SweptTriangle t1, CollisionResult collisionResult, double delta)
    {
        int i0, i1;

        if (Math.abs(normal.getX()) > Math.abs(normal.getY()))
        {
            if (Math.abs(normal.getX()) > Math.abs(normal.getZ()))
            {
                i0 = 1;
                i1 = 2;
            } else
            {
                i0 = 0;
                i1 = 1;
            }
        } else
        {
            if (Math.abs(normal.getZ()) > Math.abs(normal.getY()))
            {
                i0 = 0;
                i1 = 1;
            } else
            {
                i0 = 0;
                i1 = 2;
            }
        }

        if (edgeIntersectsTriangle(i0, i1, t0.getP1(), t0.getP2(), t1, delta) || edgeIntersectsTriangle(i0, i1, t0.getP2(), t0.getP3(), t1, delta) || edgeIntersectsTriangle(i0, i1, t0.getP3(), t0.getP1(), t1, delta))
        {
            return true;
        }

        // If we get here, we can be sure that none of the edges overlap each other. We only need to check if one vertex is inside
        // each triangle, because it is impossible for the edges to not overlap and have some vertices inside and some outside.
        if (pointIntersectsTriangle(i0, i1, t0.getP1(), t1, delta) || pointIntersectsTriangle(i0, i1, t1.getP1(), t0, delta))
        {
            return true;
        }

        return false;
    }

    private static boolean edgeIntersectsTriangle(int i0, int i1, Vector3f a, Vector3f b, SweptTriangle t, double delta)
    {
        return edgeIntersectsEdge(i0, i1, a, b, t.getP1(), t.getP2(), delta) || edgeIntersectsEdge(i0, i1, a, b, t.getP2(), t.getP3(), delta) || edgeIntersectsEdge(i0, i1, a, b, t.getP3(), t.getP1(), delta);

    }

    private static boolean edgeIntersectsEdge(int i0, int i1, Vector3f a1, Vector3f b1, Vector3f a2, Vector3f b2, double delta)
    {
  /* test edge a2,b2 against a1,b1 */
        float ax = MathUtils.getVectorElement(b1, i0) - MathUtils.getVectorElement(a1, i0);
        float ay = MathUtils.getVectorElement(b1, i1) - MathUtils.getVectorElement(a1, i1);
        float bx = MathUtils.getVectorElement(a2, i0) - MathUtils.getVectorElement(b2, i0);
        float by = MathUtils.getVectorElement(a2, i1) - MathUtils.getVectorElement(b2, i1);
        float cx = MathUtils.getVectorElement(a1, i0) - MathUtils.getVectorElement(a2, i0);
        float cy = MathUtils.getVectorElement(a1, i1) - MathUtils.getVectorElement(a2, i1);
        float d = by * cx - bx * cy;
        float e = ax * cy - ay * cx;
        float f = ay * bx - ax * by;

        if ((f > 0.0F && d >= 0.0F && d <= f) || (f < 0.0F && d <= 0.0F && d >= f))
        {
            if (f > 0.0F)
            {
                if (e >= 0.0F && e <= f) return true;
            } else
            {
                if (e <= 0.0F && e >= f) return true;
            }
        }

        return false;
    }

    private static boolean pointIntersectsTriangle(int i0, int i1, Vector3f p, SweptTriangle t, double delta)
    {
        float a, b, c;

        a = MathUtils.getVectorElement(t.getP2(), i1) - MathUtils.getVectorElement(t.getP1(), i1);
        b = -(MathUtils.getVectorElement(t.getP2(), i0) - MathUtils.getVectorElement(t.getP1(), i0));
        c = -a * MathUtils.getVectorElement(t.getP1(), i0) - b * MathUtils.getVectorElement(t.getP1(), i1);
        float d0 = a * MathUtils.getVectorElement(p, i0) + b * MathUtils.getVectorElement(p, i1) + c;

        a = MathUtils.getVectorElement(t.getP3(), i1) - MathUtils.getVectorElement(t.getP2(), i1);
        b = -(MathUtils.getVectorElement(t.getP3(), i0) - MathUtils.getVectorElement(t.getP2(), i0));
        c = -a * MathUtils.getVectorElement(t.getP2(), i0) - b * MathUtils.getVectorElement(t.getP2(), i1);
        float d1 = a * MathUtils.getVectorElement(p, i0) + b * MathUtils.getVectorElement(p, i1) + c;

        a = MathUtils.getVectorElement(t.getP1(), i1) - MathUtils.getVectorElement(t.getP3(), i1);
        b = -(MathUtils.getVectorElement(t.getP1(), i0) - MathUtils.getVectorElement(t.getP3(), i0));
        c = -a * MathUtils.getVectorElement(t.getP3(), i0) - b * MathUtils.getVectorElement(t.getP3(), i1);
        float d2 = a * MathUtils.getVectorElement(p, i0) + b * MathUtils.getVectorElement(p, i1) + c;

        return d0 * d1 > 0.0 && d0 * d2 > 0.0;

    }

    private static final class SweptTriangle
    {
        public Triangle triangle;
        public Vector3f velocity;

        public SweptTriangle(Triangle triangle, Vector3f velocity)
        {
            this.triangle = triangle;
            this.velocity = velocity;
        }

        public Vector3f getVelocity()
        {
            return velocity;
        }

        public Vector3f getPosition()
        {
            return new Vector3f(triangle.getPosition());
        }

        public Vector3f getP1()
        {
            return Vector3f.add(new Vector3f(triangle.getP1()), this.getPosition(), null);
        }


        public Vector3f getP2()
        {
            return Vector3f.add(new Vector3f(triangle.getP2()), this.getPosition(), null);
        }


        public Vector3f getP3()
        {
            return Vector3f.add(new Vector3f(triangle.getP3()), this.getPosition(), null);
        }

        public Vector3f getNormal()
        {
            return new Vector3f(triangle.getNormal());
        }
    }
}
