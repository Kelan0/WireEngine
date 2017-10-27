package wireengine.core.physics.collision.polygons;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Polygon3D;

import java.util.List;

/**
 * @author Kelan
 */
public class Points extends Polygon3D
{
    public List<Vector2f> points; //Polygon only allowed to be flat in the plane of its normal.

    public Points(Vector3f position, Vector3f normal, List<Vector2f> points)
    {
        super(position, normal);
        this.points = points;
    }

    public List<Vector2f> getPoints()
    {
        return points;
    }

    public void setPoints(List<Vector2f> points)
    {
        this.points = points;
    }
}
