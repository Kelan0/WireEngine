package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.*;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionState;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.util.Constants;
import wireengine.core.util.MathUtils;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public class Ellipsoid extends Collider<Ellipsoid>
{
    private Vector3f position;
    private Vector3f radius;

    public Ellipsoid(Vector3f position, Vector3f radius)
    {
        super(ColliderType.ELIPSOID);
        this.position = position;
        this.radius = radius;
    }

    public void renderDebug(ShaderProgram shaderProgram, Vector4f colour)
    {
        int polyMode = glGetInteger(GL_POLYGON_MODE);
//        glPolygonMode(GL_FRONT_AND_BACK, type);
        DebugRenderer.getInstance().begin(GL_LINES);
        DebugRenderer.getInstance().addColour(colour);
        int radialDivisions = 30;

        double angleStep = ((Constants.PI * 2.0) / radialDivisions);

        for (int i = 0, j = radialDivisions - 1; i < radialDivisions; j = i++)
        {
            Vector2f a = new Vector2f((float) Math.cos(angleStep * i), (float) Math.sin(angleStep * i));
            Vector2f b = new Vector2f((float) Math.cos(angleStep * j), (float) Math.sin(angleStep * j));

            Vector3f xz1 = new Vector3f(a.x * radius.x, 0.0F, a.y * radius.z);
            Vector3f xz2 = new Vector3f(b.x * radius.x, 0.0F, b.y * radius.z);

            Vector3f xy1 = new Vector3f(a.x * radius.x, a.y * radius.y, 0.0F);
            Vector3f xy2 = new Vector3f(b.x * radius.x, b.y * radius.y, 0.0F);

            Vector3f yz1 = new Vector3f(0.0F, a.x * radius.y, a.y * radius.z);
            Vector3f yz2 = new Vector3f(0.0F, b.x * radius.y, b.y * radius.z);

            DebugRenderer.getInstance().addVertex(Vector3f.add(xz1, this.getCentre(), null));
            DebugRenderer.getInstance().addVertex(Vector3f.add(xz2, this.getCentre(), null));

            DebugRenderer.getInstance().addVertex(Vector3f.add(xy1, this.getCentre(), null));
            DebugRenderer.getInstance().addVertex(Vector3f.add(xy2, this.getCentre(), null));

            DebugRenderer.getInstance().addVertex(Vector3f.add(yz1, this.getCentre(), null));
            DebugRenderer.getInstance().addVertex(Vector3f.add(yz2, this.getCentre(), null));
        }

        DebugRenderer.getInstance().end(shaderProgram);
        glPolygonMode(GL_FRONT_AND_BACK, polyMode);
    }

    public Matrix3f getElipsoidSpace()
    {
        Matrix3f mat = new Matrix3f();
        mat.m00 = 1.0F / this.radius.x;
        mat.m11 = 1.0F / this.radius.y;
        mat.m22 = 1.0F / this.radius.z;
        return mat;
    }

    public Vector3f getRelativePoint(Vector3f point)
    {
        return Matrix3f.transform(this.getElipsoidSpace(), point, null);
    }

    @Override
    public CollisionState.CollisionComponent<Ellipsoid> getCollision(AxisAlignedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ellipsoid> getCollision(Ellipsoid collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ellipsoid> getCollision(Frustum collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ellipsoid> getCollision(OrientedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ellipsoid> getCollision(Plane collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ellipsoid> getCollision(Ray collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ellipsoid> getCollision(Sphere collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Ellipsoid> getCollision(Triangle collider)
    {

        return null;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        point =  getRelativePoint(point);
        Vector3f centre = getRelativePoint(this.getCentre());

        if (MathUtils.distanceSquared(point, centre) <= 1.0F)
        {
            return true;
        }

        return false;
    }

    @Override
    public Vector3f getClosestPoint(Vector3f point)
    {
        Vector3f eCentre = getRelativePoint(this.getCentre());
        Vector3f ePoint = getRelativePoint(point);

        Vector3f eUnitSphere = Vector3f.sub(ePoint, eCentre, null).normalise(null);
        Matrix3f eSpaceInverse = Matrix3f.invert(getElipsoidSpace(), null);

        return Matrix3f.transform(eSpaceInverse, eUnitSphere, null);
    }

    @Override
    public Vector3f getNormalAt(Vector3f point)
    {
        return getClosestPoint(point).normalise(null);
    }

    @Override
    public Vector3f getPosition()
    {
        return position;
    }

    public Vector3f getRadius()
    {
        return radius;
    }

    @Override
    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public void setRadius(Vector3f radius)
    {
        this.radius = radius;
    }
}
