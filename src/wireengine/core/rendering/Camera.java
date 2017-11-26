package wireengine.core.rendering;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.rendering.renderer.ShaderProgram;
import wireengine.core.util.MathUtils;
import wireengine.testgame.TestGame;

/**
 * @author Kelan
 */
public class Camera
{
    private Quaternion orientation;
    private Vector3f position;
    private Axis axisLock;

    public Camera(Vector3f position, Axis axisLock)
    {
        this.orientation = new Quaternion();
        this.position = position;
        this.axisLock = axisLock;
    }

    public Camera(Vector3f position)
    {
        this(position, new Axis(new Vector3f(), new Vector3f(), new Vector3f()));
    }

    public Camera(Axis lockAxis)
    {
        this(new Vector3f(), lockAxis);
    }

    public Camera()
    {
        this(new Vector3f());
    }

    public void render(ShaderProgram shader)
    {
        shader.setUniformMatrix4f("viewMatrix", this.getViewMatrix());
    }

    public Camera setPosition(Vector3f position)
    {
        if (position != null)
        {
            this.position = position;
        } else
        {
            System.out.println("a");
        }
        return this;
    }

    public Camera translate(Vector3f translation)
    {
        this.position.x += translation.x;
        this.position.y += translation.y;
        this.position.z += translation.z;

        return this;
    }

    public Camera translateAxis(Vector3f translation, Axis axis)
    {
        axis.transform(translation, this.position);
        return this;
    }

    public Camera setOrientation(Quaternion orientation)
    {
        if (orientation != null)
        {
            this.orientation = orientation;
        }
        return this;
    }

    public Camera rotateAxis(Vector3f axis, float radians)
    {
        Quaternion rotation = MathUtils.axisAngleToQuaternion(axis, radians, null);

        Quaternion.mul(rotation, orientation, orientation);
        orientation.normalise();

        return this;
    }

    public Camera rotatePitch(float radians)
    {
        return rotateAxis(this.getAxis().getX(), radians);
    }

    public Camera rotateYaw(float radians)
    {
        return rotateAxis(this.getAxis().getY(), radians);
    }

    public Camera rotateRoll(float radians)
    {
        return rotateAxis(this.getAxis().getZ(), radians);
    }

    public Quaternion getOrientation()
    {
        return orientation;
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public void setOrthoDirection(Vector3f orthoDirection)
    {
        if (orthoDirection.lengthSquared() > 0.0F)
        {
            TestGame.getInstance().getWorldRenderer().setOrthographicProjection(true);

            Vector3f forward = this.getAxis().getForward();

            this.rotateAxis(Vector3f.cross(orthoDirection, forward, null), MathUtils.getAngleVector3f(orthoDirection, forward));
        } else
        {
            TestGame.getInstance().getWorldRenderer().setOrthographicProjection(false);
        }
    }

    public Axis getAxis()
    {
        Axis axis = Axis.getWorldAxis().rotate(orientation.negate(null), null);

        if (axisLock.getX().lengthSquared() > 0.0F)
        {
            axis.setX(axisLock.getX());
        }
        if (axisLock.getY().lengthSquared() > 0.0F)
        {
            axis.setY(axisLock.getY());
        }
        if (axisLock.getZ().lengthSquared() > 0.0F)
        {
            axis.setZ(axisLock.getZ());
        }

        return axis;
    }

    public Axis getAxisLock()
    {
        return axisLock;
    }

    public Matrix4f getViewMatrix()
    {
        return MathUtils.quaternionToMatrix4f(this.orientation, null).translate(this.position.negate(null));
    }

    @Override
    public String toString()
    {
        return "Camera{" + "orientation=" + orientation + ", position=" + position + ", axisLock=" + axisLock + '}';
    }
}