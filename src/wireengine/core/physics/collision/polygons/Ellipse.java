package wireengine.core.physics.collision.polygons;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Polygon3D;

/**
 * @author Kelan
 */
public class Ellipse extends Polygon3D
{
    public Vector2f radius;

    public Ellipse(Vector3f position, Vector3f normal, Vector2f radius)
    {
        super(position, normal);
        this.radius = radius;
    }

    public Vector2f getRadius()
    {
        return radius;
    }

    public void setRadius(Vector2f radius)
    {
        this.radius = radius;
    }
}
