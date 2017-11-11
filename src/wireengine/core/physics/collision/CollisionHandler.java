package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.PhysicsObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class CollisionHandler
{
    public static List<CollisionResult> getCollision(PhysicsObject aObject, PhysicsObject bObject, double delta)
    {
        List<CollisionResult> collisions = new ArrayList<>();

        for (Triangle ta : aObject.getColliders())
        {
            for (Triangle tb : bObject.getColliders())
            {
                CollisionResult collision = checkTriangles(new SweptTriangle(ta, aObject.getVelocity()), new SweptTriangle(tb, bObject.getVelocity()));
            }
        }

        return collisions;
    }

    private static CollisionResult checkTriangles(SweptTriangle a, SweptTriangle b)
    {
        return null;
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
    }
}
