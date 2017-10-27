package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.colliders.Plane;

/**
 * @author Kelan
 */
public abstract class Polygon3D
{
    public Vector3f position;
    public Plane plane;

    public Polygon3D(Vector3f position, Vector3f normal)
    {
        this.position = position;
        this.plane = new Plane(position, normal.lengthSquared() == 0.0F ? new Vector3f(0.0F, 1.0F, 0.0F) : normal);
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public Plane getPlane()
    {
        return plane;
    }
}
