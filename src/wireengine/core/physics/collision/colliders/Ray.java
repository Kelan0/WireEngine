package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class Ray
{
    public Vector3f origin;
    public Vector3f direction;
    public float maxLength;

    public Ray(Vector3f origin, Vector3f direction, float maxLength)
    {
        this.origin = origin;
        this.direction = direction;
        this.maxLength = maxLength;
    }

    public Ray(Vector3f origin, Vector3f direction)
    {
        this(origin, direction, Float.MAX_VALUE);
    }

    public Vector3f getOrigin()
    {
        return origin;
    }

    public void setOrigin(Vector3f origin)
    {
        this.origin = origin;
    }

    public Vector3f getDirection()
    {
        return direction;
    }

    public void setDirection(Vector3f direction)
    {
        this.direction = direction;
    }

    public float getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(float maxLength)
    {
        this.maxLength = maxLength;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ray ray = (Ray) o;

        if (Float.compare(ray.maxLength, maxLength) != 0) return false;
        if (origin != null ? !origin.equals(ray.origin) : ray.origin != null) return false;
        return direction != null ? direction.equals(ray.direction) : ray.direction == null;
    }

    @Override
    public int hashCode()
    {
        int result = origin != null ? origin.hashCode() : 0;
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (maxLength != +0.0f ? Float.floatToIntBits(maxLength) : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Ray{" + "origin=" + origin + ", direction=" + direction + ", maxLength=" + maxLength +'}';
    }
}
