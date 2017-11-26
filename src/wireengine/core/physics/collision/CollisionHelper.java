package wireengine.core.physics.collision;

import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class CollisionHelper
{
    public static boolean segmentPlaneIntersection(ReadableVector3f lineStart, ReadableVector3f lineEnd, ReadableVector3f planeNormal, ReadableVector3f planePoint, Vector3f destCollisionPoint)
    {
        Vector3f direction = Vector3f.sub(new Vector3f(lineEnd), new Vector3f(lineStart), null);
        float length = direction.length();
        direction.scale(1.0F / length);

        return rayPlaneIntersection(lineStart, direction, length, planeNormal, planePoint, destCollisionPoint);
    }

    public static boolean rayPlaneIntersection(ReadableVector3f rayOrigin, ReadableVector3f rayDirection, ReadableVector3f planeNormal, ReadableVector3f planePoint, Vector3f destCollisionPoint)
    {
        return rayPlaneIntersection(rayOrigin, rayDirection, Float.POSITIVE_INFINITY, planeNormal, planePoint, destCollisionPoint);
    }

    public static boolean rayPlaneIntersection(ReadableVector3f rayOrigin, ReadableVector3f rayDirection, float rayLength, ReadableVector3f planeNormal, ReadableVector3f planePoint, Vector3f destCollisionPoint)
    {
        if (destCollisionPoint == null)
        {
            destCollisionPoint = new Vector3f();
        }

        float d = Vector3f.dot(new Vector3f(planeNormal), new Vector3f(rayDirection));

        if (Math.abs(d) > 0.0001F)
        {
            float t = Vector3f.dot(Vector3f.sub(new Vector3f(planePoint), new Vector3f(rayOrigin), null), new Vector3f(planeNormal)) / d;

            if (t >= 0.0F)
            {
                if (Float.isInfinite(rayLength) || t < rayLength)
                {
                    destCollisionPoint.x = rayOrigin.getX() + rayDirection.getX() * t;
                    destCollisionPoint.y = rayOrigin.getY() + rayDirection.getY() * t;
                    destCollisionPoint.z = rayOrigin.getZ() + rayDirection.getZ() * t;
                    return true;
                }
            }
        }

        return false;
    }
}
