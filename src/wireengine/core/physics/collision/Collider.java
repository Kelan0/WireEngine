package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.util.Constants;

import static org.lwjgl.opengl.GL11.GL_LINES;

/**
 * @author Kelan
 */
public abstract class Collider
{
    public final ColliderType colliderType;

    Collider(ColliderType colliderType)
    {
        this.colliderType = colliderType;
    }

    public CollisionHandler getCollision(Collider collider, float epsilon)
    {
        CollisionHandler collision = new CollisionHandler();

        Vector3f closestOnThis = this.getClosestPoint(collider.getCentre());
        Vector3f closestOnThat = collider.getClosestPoint(this.getCentre());

        //Collision is true if the closest point on either collider is inside the other collider.
        if (this.pointIntersects(closestOnThat) || collider.pointIntersects(closestOnThis))
        {
            collision.didHit = true;
            collision.hitPosition = closestOnThis;
            collision.hitNormal = this.getNormalAt(closestOnThis);
        }

        return collision;
    }

    public Vector3f getCentre()
    {
        return getPosition();
    }

    public abstract boolean pointIntersects(Vector3f point);

    public abstract Vector3f getClosestPoint(Vector3f point);

    public abstract Vector3f getNormalAt(Vector3f point);

    public abstract Vector3f getPosition();

    public abstract void setPosition(Vector3f position);

    public ColliderType getColliderType()
    {
        return colliderType;
    }

    public enum ColliderType
    {
        PLANE, SPHERE, CYLINDER, CONE, QUAD, TRIANGLE, AABB, OBB, FRUSTUM, RAY, COMPOSITE
    }
}
