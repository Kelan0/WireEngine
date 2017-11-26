package wireengine.core.physics;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.rendering.geometry.Transformation;

/**
 * @author Kelan
 */
public interface IPhysicsObject
{
    void tick(double delta);

    void applyForce(Vector3f force);

    void applyAcceleration(Vector3f acceleration);

    void applyTorque(Vector3f torque);

    Transformation getTransformation();

    Vector3f getLinearVelocity();

    Vector3f getLinearAcceleration();

    Vector3f getAngularVelocity();

    Vector3f getAngularAcceleration();

    float getMass();

    boolean isStatic();
}
