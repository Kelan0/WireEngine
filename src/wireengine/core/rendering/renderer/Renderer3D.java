package wireengine.core.rendering.renderer;

import org.lwjgl.util.vector.Matrix4f;

import static org.lwjgl.opengl.GL11.glFrustum;

/**
 * @author Kelan
 */
public abstract class Renderer3D extends Renderer
{
    protected float fov;
    protected float near;
    protected float far;
    protected Matrix4f projectionMatrix;

    public Renderer3D(int priority, int width, int height, float fov, float near, float far)
    {
        super(priority, width, height);
        this.fov = fov;
        this.near = near;
        this.far = far;
    }

    public float getFov()
    {
        return fov;
    }

    public void setFov(float fov)
    {
        this.fov = fov;
    }

    public float getNear()
    {
        return near;
    }

    public void setNear(float near)
    {
        this.near = near;
    }

    public float getFar()
    {
        return far;
    }

    public void setFar(float far)
    {
        this.far = far;
    }

    public void createProjection(boolean immediate)
    {
        float aspect = (float) width / (float) height;
        float tangent = (float) (Math.tan(Math.toRadians(fov * 0.5)));

        float right = near * tangent * aspect;
        float left = -near * tangent * aspect;
        float top = near * tangent;
        float bottom = -near * tangent;

        if (immediate)
        {
            glFrustum(left, right, bottom, top, near, far);
        } else
        {
            if (projectionMatrix == null)
            {
                projectionMatrix = new Matrix4f();
            }

            projectionMatrix.setZero();
            projectionMatrix.m00 = (2.0F * near) / (right - left);
            projectionMatrix.m11 = (2.0F * near) / (top - bottom);
            projectionMatrix.m20 = (right + left) / (right - left);
            projectionMatrix.m21 = (top + bottom) / (top - bottom);
            projectionMatrix.m22 = -(far + near) / (far - near);
            projectionMatrix.m23 = -1.0F;
            projectionMatrix.m32 = -(2.0F * far * near) / (far - near);
        }
    }
}
