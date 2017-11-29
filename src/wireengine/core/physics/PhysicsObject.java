package wireengine.core.physics;

import org.lwjgl.util.vector.*;
import wireengine.core.physics.collision.AxisAlignedBB;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.rendering.renderer.ShaderProgram;
import wireengine.core.util.MathUtils;

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
    protected Vector3f angularAcceleration = new Vector3f(); // torque

    protected Tensor tensor;
    protected Matrix3f worldTensor = new Matrix3f();
    protected Matrix3f worldTensorInv = new Matrix3f();
    protected Vector3f centreOfMass;

    protected float mass = 1.0F;
    protected boolean onGround = false;
    protected boolean dampVelocity = false;

    protected final boolean isStatic;

    public PhysicsObject(Collider collider, float mass, Tensor tensor, boolean isStatic)
    {
        this.collider = collider;
        this.mass = mass;
        this.tensor = tensor;
        this.isStatic = isStatic;

        if (collider != null)
        {
            this.transformation = collider.getTransformation();
            this.aabb = AxisAlignedBB.getMinimumEnclosingBB(collider.getVertices());
        }
    }

    public PhysicsObject(Collider collider, float mass, Tensor tensor)
    {
        this(collider, mass, tensor, false);
    }

    @Override
    public synchronized void tick(double delta)
    {
        Vector3f rCentre = this.collider.getCentre();
        Vector4f wCentre = new Vector4f(rCentre.x, rCentre.y, rCentre.z, 1.0F);
        Matrix4f.transform(this.transformation.getMatrix(null), wCentre, wCentre);
        this.centreOfMass = new Vector3f(wCentre);

        if (this.tensor != null)
        {
            Matrix3f rotation = MathUtils.quaternionToMatrix3f(this.getTransformation().getRotation(), null);
            Matrix3f localTensor = this.tensor.getTensor();
            Matrix3f localTensorInv = Matrix3f.invert(localTensor, null);

            this.worldTensor = new Matrix3f();
            this.worldTensorInv = new Matrix3f();
            Matrix3f.mul(Matrix3f.mul(rotation, localTensor, null), rotation.transpose(null), this.worldTensor);
            Matrix3f.mul(Matrix3f.mul(rotation, localTensorInv, null), rotation.transpose(null), this.worldTensorInv);
        }

        integrateVelocities(delta);
        applyDamping(delta);
        integrateTransforms(delta);
        updateColliders();

        this.linearAcceleration = new Vector3f();
        this.angularAcceleration = new Vector3f();
    }

    private void updateColliders()
    {
        if (this.collider != null)
        {
            this.aabb.setTransform(this.collider.getTransformation());
        }
    }

    private void applyDamping(double delta)
    {
        float linearDamper = 5.6F; //TODO
        float dot = Vector3f.dot(linearVelocity, linearAcceleration);
        if (dot <= 0.0F && dampVelocity) //TODo: scale the linearVelocity damper value based on how much the linearVelocity and the linearAcceleration face in the same direction.
        {
            linearVelocity.x /= 1.0F + linearDamper * delta;
            linearVelocity.y /= 1.0F + linearDamper * delta;
            linearVelocity.z /= 1.0F + linearDamper * delta;
        }

        float angularDamper = 0.4F;

        angularVelocity.x /= 1.0F + angularDamper * delta;
        angularVelocity.y /= 1.0F + angularDamper * delta;
        angularVelocity.z /= 1.0F + angularDamper * delta;
    }

    private void integrateVelocities(double delta)
    {
        Vector3f linearAcceleration = (Vector3f) new Vector3f(this.linearAcceleration).scale((float) delta);
        Vector3f.add(linearAcceleration, this.linearVelocity, this.linearVelocity);

        Vector3f angularAcceleration = (Vector3f) new Vector3f(this.angularAcceleration).scale((float) delta);
        Matrix3f.transform(this.worldTensorInv, angularAcceleration, angularAcceleration);
        Vector3f.add(angularAcceleration, this.angularVelocity, this.angularVelocity);
    }

    private void integrateTransforms(double delta)
    {
        Vector3f position = this.getTransformation().getTranslation();
        Quaternion rotation = this.getTransformation().getRotation();

        position.x += linearVelocity.x * delta + 0.5F * linearAcceleration.x * delta * delta;
        position.y += linearVelocity.y * delta + 0.5F * linearAcceleration.y * delta * delta;
        position.z += linearVelocity.z * delta + 0.5F * linearAcceleration.z * delta * delta;

        if (rotation.lengthSquared() <= 0.0F)
        {
            rotation = new Quaternion();
        }

        rotation.normalise();
        rotation.x += (+angularVelocity.x * rotation.w + angularVelocity.y * rotation.z - angularVelocity.z * rotation.y) * delta * 0.5F;
        rotation.y += (+angularVelocity.y * rotation.w + angularVelocity.z * rotation.x - angularVelocity.x * rotation.z) * delta * 0.5F;
        rotation.z += (+angularVelocity.z * rotation.w + angularVelocity.x * rotation.y - angularVelocity.y * rotation.x) * delta * 0.5F;
        rotation.w += (-angularVelocity.x * rotation.x - angularVelocity.y * rotation.y - angularVelocity.z * rotation.z) * delta * 0.5F;
        rotation.normalise();
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
    public synchronized void applyLinearImpulse(Vector3f impulse)
    {
        Vector3f.add(impulse, this.linearVelocity, this.linearVelocity);
    }

    @Override
    public synchronized void applyLinearForce(Vector3f force)
    {
        applyLinearAcceleration((Vector3f) new Vector3f(force).scale(1.0F / mass)); //F = M * A, A = F / M
    }

    @Override
    public synchronized void applyLinearAcceleration(Vector3f acceleration)
    {
        Vector3f.add(acceleration, this.linearAcceleration, this.linearAcceleration);
    }

    @Override
    public synchronized void applyAngularImpulse(Vector3f impulse)
    {
        Vector3f.add(Matrix3f.transform(this.worldTensorInv, impulse, null), this.angularVelocity, this.angularVelocity);
    }

    @Override
    public void applyAngularForce(Vector3f force)
    {
        applyAngularAcceleration(force);
    }

    @Override
    public synchronized void applyAngularAcceleration(Vector3f acceleration) // torque
    {
        Vector3f.add(acceleration, this.angularAcceleration, this.angularAcceleration);
    }

    @Override
    public void applyForce(Vector3f force, Vector3f position)
    {
        if (force != null)
        {
            applyLinearForce(force);

            if (position != null)
            {
                applyAngularForce(Vector3f.cross(position, force, null));
            }
        }
    }

    @Override
    public synchronized void applyImpulse(Vector3f impulse, Vector3f position)
    {
        if (impulse != null)
        {
            applyLinearImpulse(impulse);

            if (position != null)
            {
                applyAngularImpulse(Vector3f.cross(position, impulse, null));
            }
        }
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

    public Vector3f getCentreOfMass()
    {
        return this.centreOfMass;
    }

    public Matrix3f getWorldTensorInv()
    {
        return this.worldTensorInv;
    }

    public Matrix3f getWorldTensor()
    {
        return this.worldTensor;
    }
    public AxisAlignedBB getAxisAlignedBB()
    {
        return aabb;
    }
}
