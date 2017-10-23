package wireengine.core.physics;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public interface PhysicsObject
{
    void tick(double delta);

    Vector3f getPosition();

    Vector3f getVelocity();

    Vector3f getAcceleration();

    float getMass();

    boolean isStatic();
}
