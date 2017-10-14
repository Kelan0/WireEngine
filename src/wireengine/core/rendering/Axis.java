package wireengine.core.rendering;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.util.MathUtils;

/**
 * @author Kelan
 */
public class Axis
{
    private static final Axis WORLD_AXIS = new Axis(new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0f));
    private static final Axis NO_AXIS = new Axis(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F));
    private static final Axis X_AXIS_ONLY = new Axis(new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F));
    private static final Axis Y_AXIS_ONLY = new Axis(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F));
    private static final Axis Z_AXIS_ONLY = new Axis(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F));

    private Vector3f x;
    private Vector3f y;
    private Vector3f z;

    public Axis(Vector3f x, Vector3f y, Vector3f z)
    {
        if (x == null || y == null || z == null)
        {
            throw new IllegalArgumentException("Cannot create axis with null vectors");
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Axis(Axis axis)
    {
        this(new Vector3f(axis.x), new Vector3f(axis.y), new Vector3f(axis.z));
    }

    public Axis(Matrix3f matrix)
    {
        this(MathUtils.matrix3fToAxis(matrix, null));
    }

    public Axis()
    {
        this(WORLD_AXIS);
    }

    public static Axis getWorldAxis()
    {
        return new Axis(WORLD_AXIS);
    }

    public static Axis getNoAxis()
    {
        return new Axis(NO_AXIS);
    }

    public static Axis getxAxisOnly()
    {
        return new Axis(X_AXIS_ONLY);
    }

    public static Axis getyAxisOnly()
    {
        return new Axis(Y_AXIS_ONLY);
    }

    public static Axis getzAxisOnly()
    {
        return new Axis(Z_AXIS_ONLY);
    }

    public Axis normalize(Axis dest)
    {
        if (dest == null)
        {
            dest = new Axis();
        }


        dest.setX(this.getX().lengthSquared() > 0.0F ? this.getX().normalise(null) : new Vector3f());
        dest.setY(this.getY().lengthSquared() > 0.0F ? this.getY().normalise(null) : new Vector3f());
        dest.setZ(this.getZ().lengthSquared() > 0.0F ? this.getZ().normalise(null) : new Vector3f());

        return dest;
    }

    public Axis negate(Axis dest)
    {
        if (dest == null)
        {
            dest = new Axis();
        }

        dest.setX(this.getX().negate(null));
        dest.setY(this.getY().negate(null));
        dest.setZ(this.getZ().negate(null));

        return dest;
    }

    public Axis rotate(Quaternion quat, Axis dest)
    {
        if (dest == null)
        {
            dest = new Axis();
        }

        dest.setX(MathUtils.rotateVector3f(quat, this.getX(), null));
        dest.setY(MathUtils.rotateVector3f(quat, this.getY(), null));
        dest.setZ(MathUtils.rotateVector3f(quat, this.getZ(), null));

        return dest;
    }

    public Axis scale(Vector3f scale, Axis dest)
    {
        if (dest == null)
        {
            dest = new Axis();
        }

        dest.setX(new Vector3f(this.getX().x * scale.x, this.getX().y * scale.x, this.getX().z * scale.x));
        dest.setY(new Vector3f(this.getY().x * scale.y, this.getY().y * scale.y, this.getY().z * scale.y));
        dest.setZ(new Vector3f(this.getZ().x * scale.z, this.getZ().y * scale.z, this.getZ().z * scale.z));

        return dest;
    }

    public Vector3f getX()
    {
        return new Vector3f(x);
    }

    public Vector3f getY()
    {
        return new Vector3f(y);
    }

    public Vector3f getZ()
    {
        return new Vector3f(z);
    }

    public void setX(Vector3f x)
    {
        this.x = x;
    }

    public void setY(Vector3f y)
    {
        this.y = y;
    }

    public void setZ(Vector3f z)
    {
        this.z = z;
    }

    public Vector3f getLeft()
    {
        return getX();
    }

    public Vector3f getRight()
    {
        return getLeft().negate(null);
    }

    public Vector3f getUp()
    {
        return getY();
    }

    public Vector3f getDown()
    {
        return getUp().negate(null);
    }

    public Vector3f getForward()
    {
        return getZ();
    }

    public Vector3f getBackward()
    {
        return getForward().negate(null);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Axis axis = (Axis) o;

        if (x != null ? !x.equals(axis.x) : axis.x != null) return false;
        if (y != null ? !y.equals(axis.y) : axis.y != null) return false;
        return z != null ? z.equals(axis.z) : axis.z == null;
    }

    @Override
    public int hashCode()
    {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        result = 31 * result + (z != null ? z.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Axis{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
