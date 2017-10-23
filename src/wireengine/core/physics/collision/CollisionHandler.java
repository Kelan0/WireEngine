package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.rendering.geometry.Mesh;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class CollisionHandler
{
    protected boolean didHit;
    protected Vector3f hitPosition;
    protected Vector3f hitNormal;
    protected Object hitData;
    public String string = "";

    CollisionHandler()
    {
        this.didHit = false;
        this.hitData = null;
        this.hitPosition = new Vector3f();
        this.hitNormal = new Vector3f();
    }

    public boolean didHit()
    {
        return didHit;
    }

    public Object getHitData()
    {
        return hitData;
    }

    public Vector3f getHitPosition()
    {
        return hitPosition;
    }

    public Vector3f getHitNormal()
    {
        return hitNormal;
    }

    public static CompositeCollider calculateCollider(Mesh mesh)
    {
        List<Collider> triangles = new ArrayList<>();

        for (Mesh.Face3 face : mesh.getFaceList())
        {
            Vector3f p1 = face.getV1().getPosition();
            Vector3f p2 = face.getV2().getPosition();
            Vector3f p3 = face.getV3().getPosition();

            Triangle triangle = new Triangle(p1, p2, p3);
            triangles.add(triangle);
        }

        return new CompositeCollider(mesh.getCentre(), triangles);
    }
}
