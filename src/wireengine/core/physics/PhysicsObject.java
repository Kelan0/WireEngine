package wireengine.core.physics;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.physics.collision.AxisAlignedBB;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.rendering.renderer.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public class PhysicsObject implements IPhysicsObject
{
    protected AxisAlignedBB aabb;
    protected Collider collider;

    protected Transformation transformation;
    protected Vector3f linearVelocity = new Vector3f();
    protected Vector3f linearAcceleration = new Vector3f();
    protected Vector3f angularVelocity = new Vector3f();
    protected Vector3f angularAcceleration = new Vector3f();

    protected float mass = 1.0F;
    protected boolean onGround = false;
    protected boolean dampVelocity = false;

    protected final boolean isStatic;

    public PhysicsObject(Collider collider, float mass, boolean isStatic)
    {
        this.collider = collider;
        this.mass = mass;
        this.isStatic = isStatic;

        if (collider != null)
        {
            this.transformation = collider.getTransformation();
            this.aabb = AxisAlignedBB.getMinimumEnclosingBB(collider.getVertices());
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

        Vector3f position = this.getTransformation().getTranslation();
        Vector3f linearVelocity = this.getLinearVelocity();
        Vector3f linearAcceleration = this.getLinearAcceleration();

        linearVelocity.x += linearAcceleration.x * delta;
        linearVelocity.y += linearAcceleration.y * delta;
        linearVelocity.z += linearAcceleration.z * delta;

        float dot = Vector3f.dot(linearVelocity, linearAcceleration);
        if (dot <= 0.0F && dampVelocity) //TODo: scale the linearVelocity damper value based on how much the linearVelocity and the linearAcceleration face in the same direction.
        {
            linearVelocity.x /= 1.0F + velocityDamper * delta;
            linearVelocity.y /= 1.0F + velocityDamper * delta;
            linearVelocity.z /= 1.0F + velocityDamper * delta;
        }

        position.x += linearVelocity.x * delta + 0.5F * linearAcceleration.x * delta * delta;
        position.y += linearVelocity.y * delta + 0.5F * linearAcceleration.y * delta * delta;
        position.z += linearVelocity.z * delta + 0.5F * linearAcceleration.z * delta * delta;
        updateColliders();

        this.linearAcceleration = new Vector3f();
    }

    private void updateColliders()
    {
        if (this.collider != null)
        {
            this.aabb.setTransform(this.collider.getTransformation());
        }
    }

    public synchronized void renderDebug(ShaderProgram shaderProgram, Vector4f colour, int renderMode)
    {
        DebugRenderer.getInstance().begin(renderMode);

        if (this.collider != null && this.collider.getNumTriangles() > 0)
        {
            for (Triangle triangle : this.collider.getTriangles())
            {
                triangle = triangle.getTransformed(this.getTransformation());
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

        this.aabb.renderDebug(shaderProgram, colour);
    }

    @Override
    public synchronized void applyForce(Vector3f force)
    {
        applyAcceleration((Vector3f) new Vector3f(force).scale(1.0F / mass)); //F = M * A, A = F / M
    }

    @Override
    public synchronized void applyAcceleration(Vector3f acceleration)
    {
        Vector3f.add(acceleration, this.linearAcceleration, this.linearAcceleration);
    }

    @Override
    public synchronized void applyTorque(Vector3f torque)
    {

    }

    @Override
    public synchronized Transformation getTransformation()
    {
        return transformation;
    }

    @Override
    public synchronized Vector3f getLinearVelocity()
    {
        return this.linearVelocity;
    }

    @Override
    public synchronized Vector3f getLinearAcceleration()
    {
        return linearAcceleration;
    }

    @Override
    public synchronized Vector3f getAngularVelocity()
    {
        return this.angularVelocity;
    }

    @Override
    public synchronized Vector3f getAngularAcceleration()
    {
        return this.angularAcceleration;
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

    public boolean doDampVelocity()
    {
        return dampVelocity;
    }

    public void setDampVelocity(boolean dampVelocity)
    {
        this.dampVelocity = dampVelocity;
    }

    public synchronized List<Triangle> getTriangles()
    {
        if (this.collider != null)
        {
            return this.collider.getTriangles();
        }

        return new ArrayList<>();
    }

    public Collider getCollider()
    {
        return this.collider;
    }

    public AxisAlignedBB getAxisAlignedBB()
    {
        return aabb;
    }
}
