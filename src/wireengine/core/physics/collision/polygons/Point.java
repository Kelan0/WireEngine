package wireengine.core.physics.collision.polygons;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Polygon3D;

/**
 * @author Kelan
 */
public class Point extends Polygon3D
{
    public Point(Vector3f position, Vector3f normal)
    {
        super(position, normal);
    }

    public Point(Vector3f position)
    {
        this(position, new Vector3f());
    }
}
