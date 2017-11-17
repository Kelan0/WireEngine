package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class Sphere
{
    private Vector3f position;
    private float radius;

    public Sphere(Vector3f position, float radius)
    {
        this.position = position;
        this.radius = radius;
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public float getRadius()
    {
        return radius;
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sphere sphere = (Sphere) o;

        if (Float.compare(sphere.radius, radius) != 0) return false;
        return position != null ? position.equals(sphere.position) : sphere.position == null;
    }

    @Override
    public int hashCode()
    {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + (radius != +0.0f ? Float.floatToIntBits(radius) : 0);
        return result;
    }
}
