package wireengine.core.util;

import org.lwjgl.util.vector.*;
import wireengine.core.rendering.Axis;

import java.nio.FloatBuffer;

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

    public static float getAngleVector3f(Vector3f a, Vector3f b)
    {
        return (float) Math.acos(Vector3f.dot(a, b) / a.length() * b.length());
    }

    public static float interpolate(float v1, float v2, double d)
    {
        return interpolate(new Vector2f(v1, 0.0F), new Vector2f(v2, 0.0F), d).x;
    }

    public static Vector2f interpolate(Vector2f v1, Vector2f v2, double d)
    {
        return new Vector2f(interpolate(new Vector3f(v1.x, v1.y, 0.0F), new Vector3f(v2.x, v2.y, 0.0F), d));
    }

    public static Vector3f interpolate(Vector3f v1, Vector3f v2, double d)
    {
        return new Vector3f(interpolate(new Vector4f(v1.x, v1.y, v1.z, 0.0F), new Vector4f(v2.x, v2.y, v2.z, 0.0F), d));
    }

    public static Vector3f direction(Vector3f a, Vector3f b)
    {
        return Vector3f.sub(b, a, null);
    }

    public static Vector3f mul(Vector3f a, Vector3f b)
    {
        return new Vector3f(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    public static Vector3f div(Vector3f a, Vector3f b)
    {
        return new Vector3f(a.x / b.x, a.y / b.y, a.z / b.z);
    }

    public static Vector3f reflect(Vector3f vector, Vector3f normal, Vector3f dest)
    {
        if (dest == null)
        {
            dest = new Vector3f();
        }

        float vDotN = Vector3f.dot(vector, normal);
        dest.x = -2.0F * vDotN * normal.x + vector.x;
        dest.y = -2.0F * vDotN * normal.y + vector.y;
        dest.z = -2.0F * vDotN * normal.z + vector.z;

        return dest;
    }

    public static void setVectorElement(WritableVector3f vector, float value, int i)
    {
        if (i == 0) vector.setX(value);
        if (i == 1) vector.setY(value);
        if (i == 2) vector.setZ(value);
    }

    public static float getVectorElement(ReadableVector3f vector, int i)
    {
        return getVectorArray(vector)[i];
    }

    public static float[] getVectorArray(ReadableVector4f vector)
    {
        return new float[]{vector.getX(), vector.getY(), vector.getZ(), vector.getW()};
    }

    public static float[] getVectorArray(ReadableVector3f vector)
    {
        return new float[]{vector.getX(), vector.getY(), vector.getZ()};
    }

    public static float[] getVectorArray(ReadableVector2f vector)
    {
        return new float[]{vector.getX(), vector.getY()};
    }

    public static float distanceSquared(Vector3f a, Vector3f b)
    {
        return direction(a, b).lengthSquared();
    }

    public static float distance(Vector3f a, Vector3f b)
    {
        return direction(a, b).length();
    }

    public static Vector4f interpolate(Vector4f v1, Vector4f v2, double d)
    {
        float x = (float) (v1.x + d * (v2.x - v1.x));
        float y = (float) (v1.y + d * (v2.y - v1.y));
        float z = (float) (v1.z + d * (v2.z - v1.z));
        float w = (float) (v1.w + d * (v2.w - v1.w));
        return new Vector4f(x, y, z, w);
    }

    public static float dotPerp(Vector2f v1, Vector2f v2)
    {
        return v1.x * v2.y - v1.y * v2.x;
    }

    public static void generateComplementBasis(Vector3f u, Vector3f v, Vector3f w)
    {
        float invLength;

        if (Math.abs(w.x) >= Math.abs(w.y))
        {
            invLength = (float) (1.0F / Math.sqrt(w.x * w.x + w.z * w.z));
            u.x = -w.z * invLength;
            u.y = 0.0F;
            u.z = +w.x * invLength;
            v.x = w.y * u.z;
            v.y = w.z * u.x - w.x * u.z;
            v.z = -w.y * u.x;
        } else
        {
            invLength = (float) (1.0F / Math.sqrt(w.y * w.y + w.z * w.z));
            u.x = 0.0F;
            u.y = +w.z * invLength;
            u.z = -w.y * invLength;
            v.x = w.y * u.z - w.z * u.y;
            v.y = -w.x * u.z;
            v.z = w.x * u.y;
        }
    }

    public static Quaternion quaternionDifference(Quaternion a, Quaternion b)
    {
        return Quaternion.mul(Quaternion.negate(a, null), b, null);
    }

    public static Quaternion axisAngleToQuaternion(Vector3f axis, float radians, Quaternion dest)
    {
        if (dest == null)
        {
            dest = new Quaternion();
        }

        if (radians != 0.0F && axis.lengthSquared() > 0.0F)
        {
            dest.setFromAxisAngle(new Vector4f(axis.x, axis.y, axis.z, radians));
        }

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
        if (vectors != null && vectors.length > 0)
        {
            float x = 0.0F;
            float y = 0.0F;
            float z = 0.0F;

            for (Vector3f v : vectors)
            {
                x += v.x;
                y += v.y;
                z += v.z;
            }
            return new Vector3f(x / vectors.length, y / vectors.length, z / vectors.length);
        }

        return null;
    }

    public static Vector3f quaternionToEuler(Quaternion orientation, Vector3f dest)
    {
        if (dest == null)
        {
            dest = new Vector3f();
        }

        dest.x = (float) Math.atan2(2.0F * orientation.x * orientation.w - 2.0F * orientation.y * orientation.z, 1.0F - 2.0F * orientation.x * orientation.x - 2.0F * orientation.z * orientation.z);
        dest.y = (float) Math.atan2(2.0F * orientation.y * orientation.w - 2.0F * orientation.x * orientation.z, 1.0F - 2.0F * orientation.y * orientation.y - 2.0F * orientation.z * orientation.z);
        dest.z = (float) Math.asin(2.0F * orientation.x * orientation.y + 2.0F * orientation.z * orientation.w);

        return dest;
    }

    public static Vector3f getClosest(Vector3f vector, Vector3f[] vectors)
    {
        Vector3f closest = null;
        float distance = Float.MAX_VALUE;

        for (Vector3f v : vectors)
        {
            float a = Vector3f.sub(v, vector, null).lengthSquared();

            if (a < distance)
            {
                closest = v;
                distance = a;
            }
        }

        return closest;
    }

    public static float clamp(float f, float min, float max)
    {
        min = Math.min(min, max);
        max = Math.max(min, max);
        return f < min ? min : f > max ? max : f;
    }

    public static float sign(float f)
    {
        if (f > 0.0F)
        {
            return 1.0F;
        } else if (f < 0.0F)
        {
            return -1.0F;
        } else
        {
            return 0.0F;
        }
    }

    public static Vector3f setLength(float length, Vector3f vector, Vector3f dest)
    {
        if (dest == null)
        {
            dest = new Vector3f();
        }

        vector.normalise(dest).scale(length);
        return dest;
    }

    public static Vector3f abs(Vector3f vector)
    {
        return new Vector3f(Math.abs(vector.x), Math.abs(vector.y), Math.abs(vector.z));
    }


    public static boolean getRoots(float a, float b, float c, FloatBuffer buf)
    {
        float determinant = b * b - 4.0F * a * c;
        if (determinant >= 0.0F)
        {
            float sqrtD = (float) Math.sqrt(determinant);
            float temp1 = (-b - sqrtD) / (2.0F * a);
            float temp2 = (-b + sqrtD) / (2.0F * a);
            float x1 = Math.min(temp1, temp2);
            float x2 = Math.max(temp1, temp2);

            buf.put(x1).put(x2).flip();
            return true;
        } else
        {
            buf.put(Float.NaN).put(Float.NaN).flip();
            return false;
        }
    }
}
