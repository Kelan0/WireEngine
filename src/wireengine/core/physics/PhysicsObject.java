package wireengine.core.physics;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.WireEngine;
import wireengine.core.level.Level;

/**
 * @author Kelan
 */
public class PhysicsObject<T extends Collider<T>> implements IPhysicsObject
{
    protected T collider;
    protected Vector3f velocity = new Vector3f();
    protected Vector3f acceleration = new Vector3f();
    protected float mass = 1.0F;
    protected boolean onGround = false;
    protected final boolean isStatic;

    public PhysicsObject(T collider, float mass, boolean isStatic)
    {
        this.collider = collider;
        this.mass = mass;
        this.isStatic = isStatic;
    }

    public PhysicsObject(T collider, float mass)
    {
        this(collider, mass, false);
    }

    @Override
    public synchronized void tick(double delta)
    {
        Level level = WireEngine.engine().getGame().getLevel();
        float friction = 5.6F;

        getVelocity().x += getAcceleration().x * delta;
        getVelocity().y += getAcceleration().y * delta;
        getVelocity().z += getAcceleration().z * delta;

        level.collideWith(this, delta);

        if (this.acceleration.lengthSquared() <= 0.0F)
        {
            this.velocity.x /= (1.0F + friction * delta);
            this.velocity.y /= (1.0F + friction * delta);
            this.velocity.z /= (1.0F + friction * delta);
        }

        getPosition().x += getVelocity().x * delta;// + 0.5F * object.getAcceleration().x * delta * delta;
        getPosition().y += getVelocity().y * delta;// + 0.5F * object.getAcceleration().y * delta * delta;
        getPosition().z += getVelocity().z * delta;// + 0.5F * object.getAcceleration().z * delta * delta;
        this.acceleration = new Vector3f();
    }

    @Override
    public synchronized void applyForce(Vector3f force)
    {
        applyAcceleration((Vector3f) new Vector3f(force).scale(1.0F / mass)); //F = M * A, A = F / M
    }

    @Override
    public synchronized void applyAcceleration(Vector3f acceleration)
    {
        Vector3f.add(acceleration, this.acceleration, this.acceleration);
    }

    @Override
    public synchronized Vector3f getPosition()
    {
        return collider.getPosition();
    }

    @Override
    public synchronized Vector3f getVelocity()
    {
        return velocity;
    }

    @Override
    public synchronized Vector3f getAcceleration()
    {
        return acceleration;
    }

    public synchronized Vector3f getVelocity(double delta)
    {
        return (Vector3f) new Vector3f(getVelocity()).scale((float) delta);
    }

    public synchronized Vector3f getAcceleration(double delta)
    {
        return (Vector3f) new Vector3f(getAcceleration()).scale((float) delta);
    }

    @Override
    public synchronized float getMass()
    {
        return mass;
    }

    @Override
    public synchronized boolean isStatic()
    {
        return isStatic;
    }

    public synchronized boolean isOnGround()
    {
        return onGround;
    }

    public synchronized T getCollider()
    {
        return collider;
    }
}
