package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.rendering.Axis;
import wireengine.core.util.MathUtils;

/**
 * @author Kelan
 */
public class Transformation
{
    private Quaternion rotation;
    private Vector3f translation;
    private Vector3f scale;

    public Transformation(Vector3f translation, Quaternion rotation, Vector3f scale)
    {
        this.set(translation, rotation, scale);
    }

    public Transformation(Vector3f translation, Vector3f scale)
    {
        this.set(translation, scale);
    }

    public Transformation(Vector3f translation, Quaternion rotation)
    {
        this.set(translation, rotation);
    }

    public Transformation(Vector3f translation)
    {
        this.set(translation);
    }

    public Transformation(Transformation transformation)
    {
        this.set(transformation);
    }

    public Transformation()
    {
        this.set(new Vector3f(), new Quaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
    }

    public Transformation set(Vector3f translation, Quaternion rotation, Vector3f scale)
    {
        this.rotation = rotation;
        this.translation = translation;
        this.scale = scale;
        return this;
    }

    public Transformation set(Vector3f translation, Vector3f scale)
    {
        return this.set(translation, new Quaternion(), scale);
    }

    public Transformation set(Vector3f translation, Quaternion rotation)
    {
        return this.set(translation, rotation, new Vector3f(1.0F, 1.0F, 1.0F));
    }

    public Transformation set(Vector3f translation)
    {
        return this.set(translation, new Quaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
    }

    public Transformation set(Transformation transformation)
    {
        return this.set(transformation.translation, transformation.rotation, transformation.scale);
    }

    public Vector3f getTranslation()
    {
        return translation;
    }

    public Quaternion getRotation()
    {
        return rotation;
    }

    public Vector3f getScale()
    {
        return scale;
    }

    public Transformation setTranslation(Vector3f translation, Axis axis)
    {
        this.translation.x = translation.x;
        this.translation.y = translation.y;
        this.translation.z = translation.z;
        return this;
    }

    public Transformation setTranslation(Vector3f translation)
    {
        return setTranslation(translation, Axis.getWorldAxis());
    }

    public Transformation setRotation(Quaternion rotation)
    {
        this.rotation.x = rotation.x;
        this.rotation.y = rotation.y;
        this.rotation.z = rotation.z;
        return this;
    }

    public Transformation setScale(Vector3f scale)
    {
        this.scale.x = scale.x;
        this.scale.y = scale.y;
        this.scale.z = scale.z;

        return this;
    }

    public Transformation setScale(float scale)
    {
        return setScale(new Vector3f(scale, scale, scale));
    }

    public Transformation translate(Vector3f translation, Axis axis)
    {
        this.translation.x += (translation.x * axis.getX().x) + (translation.y * axis.getY().x) + (translation.z * axis.getZ().x);
        this.translation.y += (translation.x * axis.getX().y) + (translation.y * axis.getY().y) + (translation.z * axis.getZ().y);
        this.translation.z += (translation.x * axis.getX().z) + (translation.y * axis.getY().z) + (translation.z * axis.getZ().z);

        return this;
    }

    public Transformation translate(Vector3f translation)
    {
        return translate(translation, Axis.getWorldAxis());
    }

    public Transformation rotate(Vector3f rotation, Axis axis)
    {
        this.rotate(axis.getX(), rotation.x);
        this.rotate(axis.getY(), rotation.y);
        this.rotate(axis.getZ(), rotation.z);

        return this;
    }

    public Transformation rotate(Quaternion rotation)
    {
        Quaternion.mul(rotation, this.rotation, this.rotation).normalise();

        return this;
    }

    public Transformation rotate(Vector3f rotation)
    {
        return rotate(rotation, Axis.getWorldAxis());
    }

    public Transformation rotate(Vector3f axis, float angle)
    {
        return rotate(MathUtils.axisAngleToQuaternion(axis, angle, null));
    }

    public Transformation scale(Vector3f scale)
    {
        this.scale.x *= scale.x;
        this.scale.y *= scale.y;
        this.scale.z *= scale.z;

        return this;
    }

    public Transformation scale(float scale)
    {
        return scale(new Vector3f(scale, scale, scale));
    }

    public Matrix4f getMatrix(Matrix4f dest)
    {
        if (dest == null)
        {
            dest = new Matrix4f();
        }

        Matrix4f.translate(this.translation, dest, dest);
        MathUtils.quaternionToMatrix4f(this.rotation, dest);
        Matrix4f.scale(this.scale, dest, dest);

        return dest;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transformation that = (Transformation) o;

        if (translation != null ? !translation.equals(that.translation) : that.translation != null) return false;
        if (rotation != null ? !rotation.equals(that.rotation) : that.rotation != null) return false;
        return scale != null ? scale.equals(that.scale) : that.scale == null;
    }

    @Override
    public int hashCode()
    {
        int result = translation != null ? translation.hashCode() : 0;
        result = 31 * result + (rotation != null ? rotation.hashCode() : 0);
        result = 31 * result + (scale != null ? scale.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Transformation{" + "translation=" + translation + ", rotation=" + rotation + ", scale=" + scale + '}';
    }
}
