package wireengine.core.physics.collision.algorithm;

import java.util.Arrays;

/**
 * @author Kelan
 */
public class Simplex
{
    private SupportPoint[] points = new SupportPoint[0];

    public Simplex(SupportPoint p0, SupportPoint p1, SupportPoint p2, SupportPoint p3)
    {
        this.set(p0, p1, p2, p3);
    }

    public Simplex(SupportPoint p0, SupportPoint p1, SupportPoint p2)
    {
        this.set(p0, p1, p2);
    }

    public Simplex(SupportPoint p0, SupportPoint p1)
    {
        this.set(p0, p1);
    }

    public Simplex(SupportPoint p0)
    {
        this.set(p0);
    }

    public Simplex()
    {
        this.set();
    }

    public Simplex set(SupportPoint p0, SupportPoint p1, SupportPoint p2, SupportPoint p3)
    {
        this.points = new SupportPoint[]{p0, p1, p2, p3};
        return this;
    }

    public Simplex set(SupportPoint p0, SupportPoint p1, SupportPoint p2)
    {
        this.points = new SupportPoint[]{p0, p1, p2};
        return this;
    }

    public Simplex set(SupportPoint p0, SupportPoint p1)
    {
        this.points = new SupportPoint[]{p0, p1};
        return this;
    }

    public Simplex set(SupportPoint p0)
    {
        this.points = new SupportPoint[]{p0};
        return this;
    }

    public Simplex set()
    {
        this.points = new SupportPoint[] {};
        return this;
    }

    public Simplex push(SupportPoint point)
    {
        int size = Math.min(this.getSize() + 1, 4);
        SupportPoint[] newPoints = new SupportPoint[size];

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

    public SupportPoint[] getPoints()
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
