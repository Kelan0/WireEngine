package wireengine.core.physics.collision.polygons;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Polygon3D;

/**
 * @author Kelan
 */
public class Circle extends Polygon3D
{
    public float radius;

    public Circle(Vector3f position, Vector3f normal, float radius)
    {
        super(position, normal);
        this.radius = radius;
    }

    public float getRadius()
    {
        return radius;
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
    }
}
