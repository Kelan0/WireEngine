package wireengine.core.physics;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.rendering.geometry.Transformation;

/**
 * @author Kelan
 */
public interface IPhysicsObject
{
    void tick(double delta);

    void applyLinearImpulse(Vector3f impulse);

    void applyLinearForce(Vector3f force);

    void applyLinearAcceleration(Vector3f acceleration);

    void applyAngularImpulse(Vector3f impulse);

    void applyAngularForce(Vector3f force);

    void applyAngularAcceleration(Vector3f torque);

    void applyForce(Vector3f force, Vector3f position);

    void applyImpulse(Vector3f force, Vector3f position);

    Transformation getTransformation();

    Vector3f getLinearVelocity();

    Vector3f getLinearAcceleration();

    Vector3f getAngularVelocity();

    Vector3f getAngularAcceleration();

    float getMass();

    boolean isStatic();
}
