package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.colliders.Triangle;

/**
 * A small wrapper class used to deal with a triplet of vertices.
 * This is used instead of a regular triangle, because the constructor
 * of the regular PolyTriangle class creates the normal, which involves an
 * expensive square root. These vertices can also be accessed directly
 * and are writable, where in the regular triangle the vertices are read-only.
 *
 * @author Kelan
 */
public final class PreTriangle
{
    public Vector3f v0;
    public Vector3f v1;
    public Vector3f v2;

    public PreTriangle(Vector3f v0, Vector3f v1, Vector3f v2)
    {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }

    public PreTriangle(Triangle triangle)
    {
        this.v0 = new Vector3f(triangle.getP1());
        this.v1 = new Vector3f(triangle.getP2());
        this.v2 = new Vector3f(triangle.getP3());
    }
}
