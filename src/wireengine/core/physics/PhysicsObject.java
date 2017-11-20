package wireengine.core.physics;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.renderer.DebugRenderer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public class PhysicsObject implements IPhysicsObject
{
    protected Collider collider;
    protected Vector3f position = new Vector3f();
    protected Vector3f velocity = new Vector3f();
    protected Vector3f acceleration = new Vector3f();
    protected float mass = 1.0F;
    protected boolean onGround = false;
    protected final boolean isStatic;

    public PhysicsObject(Collider collider, float mass, boolean isStatic)
    {
        this.collider = collider;
        this.mass = mass;
        this.isStatic = isStatic;

        if (collider != null)
        {
            this.position = collider.getTransformation().getTranslation();
        }
    }

    public PhysicsObject(Collider collider, float mass)
    {
        this(collider, mass, false);
    }

    @Override
    public synchronized void tick(double delta)
    {
        float velocityDamper = 5.6F; //kind of like friction I guess.

        getVelocity().x += getAcceleration().x * delta;
        getVelocity().y += getAcceleration().y * delta;
        getVelocity().z += getAcceleration().z * delta;

        float dot = Vector3f.dot(this.velocity, this.acceleration);
        if (dot <= 0.0F) //TODo: scale the velocity damper value based on how much the velocity and the acceleration face in the same direction.
        {
            this.velocity.x /= 1.0F + velocityDamper * delta;
            this.velocity.y /= 1.0F + velocityDamper * delta;
            this.velocity.z /= 1.0F + velocityDamper * delta;
        }

        getPosition().x += getVelocity().x * delta;// + 0.5F * object.getAcceleration().x * delta * delta;
        getPosition().y += getVelocity().y * delta;// + 0.5F * object.getAcceleration().y * delta * delta;
        getPosition().z += getVelocity().z * delta;// + 0.5F * object.getAcceleration().z * delta * delta;
        updateColliders();
        this.acceleration = new Vector3f();
    }

    private void updateColliders()
    {
        if (this.collider != null)
        {
            this.collider.getTransformation().setTranslation(this.position);
        }
    }

    public void renderDebug(ShaderProgram shaderProgram, Vector4f colour, int renderMode)
    {
        DebugRenderer.getInstance().begin(renderMode);
        DebugRenderer.getInstance().translate(this.position);

        if (this.collider != null && this.collider.getNumTriangles() > 0)
        {
            for (Triangle triangle : this.collider.getTriangles())
            {
                DebugRenderer.getInstance().addColour(colour);

                if (renderMode == GL_LINES)
                {
                    DebugRenderer.getInstance().addVertex(triangle.getP1());
                    DebugRenderer.getInstance().addVertex(triangle.getP2());

                    DebugRenderer.getInstance().addVertex(triangle.getP2());
                    DebugRenderer.getInstance().addVertex(triangle.getP3());

                    DebugRenderer.getInstance().addVertex(triangle.getP3());
                    DebugRenderer.getInstance().addVertex(triangle.getP1());

                    DebugRenderer.getInstance().addColour(new Vector4f(1.0F, 0.0F, 0.0F, 1.0F));
                    DebugRenderer.getInstance().addVertex(triangle.getCentre());
                    DebugRenderer.getInstance().addVertex(Vector3f.add(triangle.getCentre(), (Vector3f) new Vector3f(triangle.getNormal()).scale(0.25F), null));
                } else if (renderMode == GL_TRIANGLES || renderMode == GL_POINTS || renderMode == GL_LINE_LOOP)
                {
                    DebugRenderer.getInstance().addVertex(triangle.getP1());
                    DebugRenderer.getInstance().addVertex(triangle.getP2());
                    DebugRenderer.getInstance().addVertex(triangle.getP3());
                }
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

    public synchronized List<Triangle> getTriangles()
    {
        if (this.collider != null)
        {
            return this.collider.getTriangles();
        }

        return new ArrayList<>();
    }
}
