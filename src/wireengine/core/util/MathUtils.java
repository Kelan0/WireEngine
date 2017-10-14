package wireengine.core.util;

import org.lwjgl.util.vector.*;
import wireengine.core.rendering.Axis;
import wireengine.core.rendering.geometry.Mesh;

/**
 * @author Kelan
 */
public class MathUtils
{
    public static Vector4f rotateVector4f(Quaternion quat, Vector4f vec, Vector4f dest)
    {
        if (dest == null)
        {
            dest = new Vector4f();
        }

        return Matrix4f.transform(quaternionToMatrix4f(quat, null), vec, dest);
    }

    public static Vector3f rotateVector3f(Quaternion quat, Vector3f vec, Vector3f dest)
    {
        if (dest == null)
        {
            dest = new Vector3f();
        }

        return Matrix3f.transform(quaternionToMatrix3f(quat, null), vec, dest);
    }

    public static Vector3f interpolate(Vector3f v1, Vector3f v2, double d)
    {
        float x = (float) (v1.x + d * (v2.x - v1.x));
        float y = (float) (v1.y + d * (v2.y - v1.y));
        float z = (float) (v1.z + d * (v2.z - v1.z));
        return new Vector3f(x, y, z);
    }

    public static Quaternion axisAngleToQuaternion(Vector3f axis, float radians, Quaternion dest)
    {
        if (dest == null)
        {
            dest = new Quaternion();
        }

        dest.setFromAxisAngle(new Vector4f(axis.x, axis.y, axis.z, radians));

        return dest;
    }

    public static Quaternion matrix4fToQuaternion(Matrix4f mat, Quaternion dest)
    {
        if (dest == null)
        {
            dest = new Quaternion();
        }

        dest.setFromMatrix(mat);

        return dest;
    }


    public static Quaternion matrix3fToQuaternion(Matrix3f mat, Quaternion dest)
    {
        if (dest == null)
        {
            dest = new Quaternion();
        }

        dest.setFromMatrix(mat);

        return dest;
    }

    public static Matrix4f quaternionToMatrix4f(Quaternion quat, Matrix4f dest)
    {
        if (dest == null)
        {
            dest = new Matrix4f();
        }

        quat.normalise();

        float s = 2.0F / quat.length();

        dest.m00 = 1.0F - s * (quat.y * quat.y + quat.z * quat.z);
        dest.m10 = s * (quat.x * quat.y + quat.w * quat.z);
        dest.m20 = s * (quat.x * quat.z - quat.w * quat.y);
        dest.m01 = s * (quat.x * quat.y - quat.w * quat.z);
        dest.m11 = 1.0F - s * (quat.x * quat.x + quat.z * quat.z);
        dest.m21 = s * (quat.y * quat.z + quat.w * quat.x);
        dest.m02 = s * (quat.x * quat.z + quat.w * quat.y);
        dest.m12 = s * (quat.y * quat.z - quat.w * quat.x);
        dest.m22 = 1.0F - s * (quat.x * quat.x + quat.y * quat.y);

        return dest;
    }

    public static Matrix3f quaternionToMatrix3f(Quaternion quat, Matrix3f dest)
    {
        return matrix4fToMatrix3f(quaternionToMatrix4f(quat, null), dest);
    }

    public static Axis matrix4fToAxis(Matrix4f matrix, Axis dest)
    {
        if (dest == null)
        {
            dest = new Axis();
        }

        dest.setX(new Vector3f(matrix.m00, matrix.m01, matrix.m02));
        dest.setY(new Vector3f(matrix.m10, matrix.m11, matrix.m12));
        dest.setZ(new Vector3f(matrix.m20, matrix.m21, matrix.m22));
        return dest;
    }

    public static Axis matrix3fToAxis(Matrix3f matrix, Axis dest)
    {
        return matrix4fToAxis(matrix3fToMatrix4f(matrix, null), dest);
    }

    public static Matrix4f axisToMatrix4f(Axis axis, Matrix4f dest)
    {
        if (dest == null)
        {
            dest = new Matrix4f();
        }

        dest.m00 = axis.getX().x;
        dest.m01 = axis.getX().y;
        dest.m02 = axis.getX().z;
        dest.m10 = axis.getY().x;
        dest.m11 = axis.getY().y;
        dest.m12 = axis.getY().z;
        dest.m20 = axis.getZ().x;
        dest.m21 = axis.getZ().y;
        dest.m22 = axis.getZ().z;

        return dest;
    }

    public static Matrix3f axisToMatrix3f(Axis axis, Matrix3f dest)
    {
        return matrix4fToMatrix3f(axisToMatrix4f(axis, null), dest);
    }

    public static Matrix3f matrix4fToMatrix3f(Matrix4f matrix, Matrix3f dest)
    {
        if (dest == null)
        {
            dest = new Matrix3f();
        }

        dest.m00 = matrix.m00;
        dest.m01 = matrix.m01;
        dest.m02 = matrix.m02;
        dest.m10 = matrix.m10;
        dest.m11 = matrix.m11;
        dest.m12 = matrix.m12;
        dest.m20 = matrix.m20;
        dest.m21 = matrix.m21;
        dest.m22 = matrix.m22;

        return dest;
    }

    public static Matrix4f matrix3fToMatrix4f(Matrix3f matrix, Matrix4f dest)
    {
        if (dest == null)
        {
            dest = new Matrix4f();
        }

        dest.m00 = matrix.m00;
        dest.m01 = matrix.m01;
        dest.m02 = matrix.m02;
        dest.m10 = matrix.m10;
        dest.m11 = matrix.m11;
        dest.m12 = matrix.m12;
        dest.m20 = matrix.m20;
        dest.m21 = matrix.m21;
        dest.m22 = matrix.m22;

        return dest;
    }

    public static Vector3f averageVector3f(Vector3f... vectors)
    {
        float x = 0.0F;
        float y = 0.0F;
        float z = 0.0F;

        if (vectors == null || vectors.length <= 0)
        {
            vectors = new Vector3f[1];
        } else
        {
            for (Vector3f v : vectors)
            {
                x += v.x;
                y += v.y;
                z += v.z;
            }
        }
        return new Vector3f(x / vectors.length, y / vectors.length, z / vectors.length);
    }
}
