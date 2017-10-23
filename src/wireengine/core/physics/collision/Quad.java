package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.util.MathUtils;

/**
 * @author Kelan
 */
public class Quad extends Collider
{
    protected Vector3f p1;
    protected Vector3f p2;
    protected Vector3f p3;
    protected Vector3f p4;

    protected Plane plane; //TODO: calculate this plane... and handle the case where this quad does not lie on a singe plane... somehow...

    public Quad(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4)
    {
        super(ColliderType.QUAD);
        this.set(p1, p2, p3, p4);
    }

    public Quad(Vector3f position, Vector3f u, Vector3f v)
    {
        super(ColliderType.QUAD);

        Vector3f p1 = Vector3f.add(position, (Vector3f) u.scale(+0.5F), null);
        Vector3f p2 = Vector3f.add(position, (Vector3f) u.scale(-0.5F), null);
        Vector3f p3 = Vector3f.add(position, (Vector3f) v.scale(+0.5F), null);
        Vector3f p4 = Vector3f.add(position, (Vector3f) v.scale(-0.5F), null);
        this.set(p1, p2, p3, p4);
    }

    public void set(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    @Override
    public CollisionHandler getCollision(Collider collider, float epsilon)
    {
        return null;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        return false;
    }

    @Override
    public Vector3f getClosestPoint(Vector3f point)
    {
        return null;
    }

    @Override
    public Vector3f getNormalAt(Vector3f point)
    {
        return this.plane.getNormalAt(point);
    }

    @Override
    public Vector3f getPosition()
    {
        return MathUtils.averageVector3f(p1, p2, p3, p4);
    }

    @Override
    public void setPosition(Vector3f position)
    {
        Vector3f change = Vector3f.sub(position, getPosition(), null);

        Vector3f.add(change, p1, p1);
        Vector3f.add(change, p2, p2);
        Vector3f.add(change, p3, p3);
        Vector3f.add(change, p4, p4);
    }
}
