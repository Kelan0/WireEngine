package wireengine.core.physics;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public interface IPhysicsObject
{
    void tick(double delta);

    void applyForce(Vector3f force);

    void applyAcceleration(Vector3f acceleration);

    Vector3f getPosition();

    Vector3f getVelocity();

    Vector3f getAcceleration();

    float getMass();

    boolean isStatic();
}
