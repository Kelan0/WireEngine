package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;

import java.util.Arrays;

/**
 * @author Kelan
 */
public class Simplex
{
    private Vector3f[] points = new Vector3f[0];

    public Simplex(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3)
    {
        this.set(p0, p1, p2, p3);
    }

    public Simplex(Vector3f p0, Vector3f p1, Vector3f p2)
    {
        this.set(p0, p1, p2);
    }

    public Simplex(Vector3f p0, Vector3f p1)
    {
        this.set(p0, p1);
    }

    public Simplex(Vector3f p0)
    {
        this.set(p0);
    }

    public Simplex set(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3)
    {
        this.points = new Vector3f[]{p0, p1, p2, p3};
        return this;
    }

    public Simplex set(Vector3f p0, Vector3f p1, Vector3f p2)
    {
        this.points = new Vector3f[]{p0, p1, p2};
        return this;
    }

    public Simplex set(Vector3f p0, Vector3f p1)
    {
        this.points = new Vector3f[]{p0, p1};
        return this;
    }

    public Simplex set(Vector3f p0)
    {
        this.points = new Vector3f[]{p0};
        return this;
    }

    public Simplex push(Vector3f point)
    {
        int size = Math.min(this.getSize() + 1, 4);
        Vector3f[] newPoints = new Vector3f[size];

        for (int i = size - 1; i > 0; i--)
        {
            newPoints[i] = this.points[i - 1];
        }

        newPoints[0] = point;

        this.points = newPoints;

        return this;
    }

    public int getSize()
    {
        return points.length;
    }

    public Vector3f[] getPoints()
    {
        return points;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Simplex simplex = (Simplex) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(points, simplex.points);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(points);
    }

    @Override
    public String toString()
    {
        return "Simplex{" + "points=" + Arrays.toString(points) + '}';
    }
}
