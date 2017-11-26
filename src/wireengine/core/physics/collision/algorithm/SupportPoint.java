package wireengine.core.physics.collision.algorithm;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class SupportPoint
{
    private Vector3f support1;
    private Vector3f support2;
    private Vector3f difference;

    public SupportPoint(Vector3f support1, Vector3f support2)
    {
        this.support1 = support1;
        this.support2 = support2;
        this.difference = Vector3f.sub(support1, support2, null);
    }

    public SupportPoint(Vector3f difference)
    {
        this.difference = difference;
    }

    public Vector3f getSupport1()
    {
        return support1;
    }

    public Vector3f getSupport2()
    {
        return support2;
    }

    public Vector3f get()
    {
        return difference;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupportPoint that = (SupportPoint) o;

        return difference != null ? difference.equals(that.difference) : that.difference == null;
    }

    @Override
    public int hashCode()
    {
        return difference != null ? difference.hashCode() : 0;
    }
}
