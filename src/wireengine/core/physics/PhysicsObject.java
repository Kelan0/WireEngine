package wireengine.core.physics;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.WireEngine;
import wireengine.core.level.Level;
import wireengine.core.physics.collision.Triangle;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.renderer.DebugRenderer;

import static org.lwjgl.opengl.GL11.GL_LINES;

/**
 * @author Kelan
 */
public class PhysicsObject implements IPhysicsObject
{
    protected Triangle[] colliders;
    protected Vector3f position = new Vector3f();
    protected Vector3f velocity = new Vector3f();
    protected Vector3f acceleration = new Vector3f();
    protected float mass = 1.0F;
    protected boolean onGround = false;
    protected final boolean isStatic;

    public PhysicsObject(Triangle[] colliders, float mass, boolean isStatic)
    {
        this.colliders = colliders;
        this.mass = mass;
        this.isStatic = isStatic;
    }

    public PhysicsObject(Triangle[] colliders, float mass)
    {
        this(colliders, mass, false);
    }

    @Override
    public synchronized void tick(double delta)
    {
        Level level = WireEngine.engine().getGame().getLevel();
        float friction = 5.6F;

        getVelocity().x += getAcceleration().x * delta;
        getVelocity().y += getAcceleration().y * delta;
        getVelocity().z += getAcceleration().z * delta;

//        level.collideWith(this, delta);

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

    public void renderDebug(ShaderProgram shaderProgram, Vector4f colour)
    {
        DebugRenderer.getInstance().begin(GL_LINES);
        if (this.colliders != null && this.colliders.length > 0)
        {
            for (Triangle triangle : this.colliders)
            {
                DebugRenderer.getInstance().addColour(colour);
                DebugRenderer.getInstance().addVertex(triangle.getP1());
                DebugRenderer.getInstance().addVertex(triangle.getP2());

                DebugRenderer.getInstance().addVertex(triangle.getP2());
                DebugRenderer.getInstance().addVertex(triangle.getP3());

                DebugRenderer.getInstance().addVertex(triangle.getP3());
                DebugRenderer.getInstance().addVertex(triangle.getP1());

                DebugRenderer.getInstance().addColour(new Vector4f(1.0F, 0.0F, 0.0F, 1.0F));
                DebugRenderer.getInstance().addVertex(triangle.getPosition());
                DebugRenderer.getInstance().addVertex(Vector3f.add(triangle.getPosition(), (Vector3f) new Vector3f(triangle.getNormal()).scale(0.25F), null));
            }
        }
        DebugRenderer.getInstance().end(shaderProgram);
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
        return this.position;
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

    public synchronized Triangle[] getColliders()
    {
        return colliders;
    }
}
