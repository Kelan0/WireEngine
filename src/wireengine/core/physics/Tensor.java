package wireengine.core.physics;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public abstract class Tensor
{
    protected float mass;

    public Tensor(float mass)
    {
        this.mass = mass;
    }

    public abstract Matrix3f getTensor();

    public float getMass()
    {
        return mass;
    }

    public void setMass(float mass)
    {
        this.mass = mass;
    }

    public static final class Cuboid extends Tensor
    {
        protected Vector3f size;

        public Cuboid(float mass, Vector3f size)
        {
            super(mass);
            this.size = size;
        }

        @Override
        public Matrix3f getTensor()
        {
            Matrix3f tensor = new Matrix3f();
            float mScaler = (1.0F / 12.0F) * this.mass;

            tensor.m00 = mScaler * (size.y * size.y + size.z * size.z);
            tensor.m11 = mScaler * (size.x * size.x + size.z * size.z);
            tensor.m22 = mScaler * (size.x * size.x + size.y * size.y);

            return tensor;
        }

        public Vector3f getSize()
        {
            return size;
        }

        public void setSize(Vector3f size)
        {
            this.size = size;
        }
    }

    public static final class Ellipsoid extends Tensor
    {
        protected Vector3f radius;

        public Ellipsoid(float mass, Vector3f radius)
        {
            super(mass);
            this.radius = radius;
        }

        @Override
        public Matrix3f getTensor()
        {
            Matrix3f m = new Matrix3f();

            float mScaler = (1.0F / 5.0F) * this.mass;
            m.m00 = mScaler * (radius.y * radius.y + radius.z * radius.z);
            m.m11 = mScaler * (radius.x * radius.x + radius.z * radius.z);
            m.m22 = mScaler * (radius.x * radius.x + radius.y * radius.y);

            return m;
        }

        public Vector3f getRadius()
        {
            return radius;
        }

        public void setRadius(Vector3f radius)
        {
            this.radius = radius;
        }
    }

    public static final class Sphere extends Tensor
    {
        protected float radius;

        public Sphere(float mass, float radius)
        {
            super(mass);
            this.radius = radius;
        }

        @Override
        public Matrix3f getTensor()
        {
            Matrix3f m = new Matrix3f();

            float mScaler = (2.0F / 5.0F) * this.mass;
            m.m00 = m.m11 = m.m22 = mScaler * radius * radius;
            return m;
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
}
