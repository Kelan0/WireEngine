package wireengine.core.physics.collision.algorithm;

import org.lwjgl.util.vector.Vector3f;
import org.lwjglx.Sys;
import wireengine.core.physics.collision.Collider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class EPA
{
    public GJK gjk;
    public Polytope polytope;
    public int iterations;

    public Vector3f collisionPoint;
    public Vector3f collisionNormal;
    public float collisionDepth;

    private EPA(GJK gjk)
    {
        this.gjk = gjk;
        this.polytope = new Polytope(gjk.simplex);
        this.iterations = 0;
    }

    private Vector3f barycentric(Vector3f p, Vector3f a, Vector3f b, Vector3f c)
    {
        // code from Crister Erickson's Real-Time Collision Detection
        Vector3f v0 = Vector3f.sub(b, a, null);
        Vector3f v1 = Vector3f.sub(c, a, null);
        Vector3f v2 = Vector3f.sub(p, a, null);
        float d00 = Vector3f.dot(v0, v0);
        float d01 = Vector3f.dot(v0, v1);
        float d11 = Vector3f.dot(v1, v1);
        float d20 = Vector3f.dot(v2, v0);
        float d21 = Vector3f.dot(v2, v1);
        float denom = d00 * d11 - d01 * d01;

        Vector3f ret = new Vector3f();
        ret.y = (d11 * d20 - d01 * d21) / denom;
        ret.z = (d00 * d21 - d01 * d20) / denom;
        ret.x = 1.0F - ret.y - ret.z;
        return ret;
    }

    private boolean update()
    {
        iterations++;

        Polytope.PolyTriangle currentTriangle = null;
        float currentDistance = Float.MAX_VALUE;

        for (Polytope.PolyTriangle triangle : polytope.triangles)
        {
            if (triangle != null)
            {
                if (currentTriangle == null)
                {
                    currentTriangle = triangle;
                } else
                {
                    float distance = triangle.distanceFromOrigin();

                    if (distance < currentDistance)
                    {
                        currentDistance = distance;
                        currentTriangle = triangle;
                    }
                }
            }
        }

        if (currentTriangle == null)
        {
            throw new IllegalStateException("No triangle exists. " + this.polytope);
        }

        SupportPoint supportPoint = this.gjk.getSupport(currentTriangle.normal);

        if (Vector3f.dot(currentTriangle.normal, supportPoint.get()) - currentDistance < gjk.epsilon)
        {
            Vector3f barycentric = this.barycentric((Vector3f) currentTriangle.normal.scale(currentDistance), currentTriangle.vertices[0].get(), currentTriangle.vertices[1].get(), currentTriangle.vertices[2].get());

            this.collisionDepth = currentDistance;
            this.collisionNormal = currentTriangle.normal.negate(null);
            this.collisionPoint = new Vector3f();
            Vector3f.add((Vector3f) new Vector3f(currentTriangle.vertices[0].getSupport1()).scale(barycentric.x), this.collisionPoint, this.collisionPoint);
            Vector3f.add((Vector3f) new Vector3f(currentTriangle.vertices[1].getSupport1()).scale(barycentric.y), this.collisionPoint, this.collisionPoint);
            Vector3f.add((Vector3f) new Vector3f(currentTriangle.vertices[2].getSupport1()).scale(barycentric.z), this.collisionPoint, this.collisionPoint);

            return false; // Do not continue iterating
        } else
        {
            this.polytope.addPoint(supportPoint);
            return true; // We are not done, continue iterating.
        }
    }

    public static EPA getContact(GJK gjk)
    {
        if (gjk == null)
        {
            return null;
        } else
        {
            EPA epa = new EPA(gjk);

            while (epa.iterations < 60)
            {
                if (!epa.update())
                {
                    break;
                }
            }

            return epa;
        }
    }

    public static EPA getContact(Collider collider1, Collider collider2)
    {
        GJK gjk = GJK.checkCollision(collider1, collider2);

        if (gjk != null && gjk.didCollide)
        {
            return getContact(gjk);
        }

        return null;
    }
}
