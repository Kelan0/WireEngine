package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.core.util.Constants;
import wireengine.core.util.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kelan
 */
public class Colliders
{
    /**
     * Creates a list of triangles in the form of a sphere, then scales it to an ellipsoid.
     * An icosphere is used for this, due to the fact that all of the triangles have the most
     * even distribution out of the other algorithms for creating a sphere.
     */
    public static Collider getEllipsoid(int numDivisions, Vector3f radius)
    {
        radius = MathUtils.abs(radius);
        List<PreTriangle> temp = new ArrayList<>();

        float t = (float) ((1.0F + Constants.SQRT_FIVE) * 0.5F);

        temp.add(new PreTriangle(new Vector3f(-1.0F, +t, 0.0F), new Vector3f(-t, 0.0F, +1.0F), new Vector3f(0.0F, +1.0F, +t)));
        temp.add(new PreTriangle(new Vector3f(-1.0F, +t, 0.0F), new Vector3f(0.0F, +1.0F, +t), new Vector3f(+1.0F, +t, 0.0F)));
        temp.add(new PreTriangle(new Vector3f(-1.0F, +t, 0.0F), new Vector3f(+1.0F, +t, 0.0F), new Vector3f(0.0F, +1.0F, -t)));
        temp.add(new PreTriangle(new Vector3f(-1.0F, +t, 0.0F), new Vector3f(0.0F, +1.0F, -t), new Vector3f(-t, 0.0F, -1.0F)));
        temp.add(new PreTriangle(new Vector3f(-1.0F, +t, 0.0F), new Vector3f(-t, 0.0F, -1.0F), new Vector3f(-t, 0.0F, +1.0F)));
        temp.add(new PreTriangle(new Vector3f(+1.0F, +t, 0.0F), new Vector3f(0.0F, +1.0F, +t), new Vector3f(+t, 0.0F, +1.0F)));
        temp.add(new PreTriangle(new Vector3f(0.0F, +1.0F, +t), new Vector3f(-t, 0.0F, +1.0F), new Vector3f(0.0F, -1.0F, +t)));
        temp.add(new PreTriangle(new Vector3f(-t, 0.0F, +1.0F), new Vector3f(-t, 0.0F, -1.0F), new Vector3f(-1.0F, -t, 0.0F)));
        temp.add(new PreTriangle(new Vector3f(-t, 0.0F, -1.0F), new Vector3f(0.0F, +1.0F, -t), new Vector3f(0.0F, -1.0F, -t)));
        temp.add(new PreTriangle(new Vector3f(0.0F, +1.0F, -t), new Vector3f(+1.0F, +t, 0.0F), new Vector3f(+t, 0.0F, -1.0F)));
        temp.add(new PreTriangle(new Vector3f(+1.0F, -t, 0.0F), new Vector3f(+t, 0.0F, +1.0F), new Vector3f(0.0F, -1.0F, +t)));
        temp.add(new PreTriangle(new Vector3f(+1.0F, -t, 0.0F), new Vector3f(0.0F, -1.0F, +t), new Vector3f(-1.0F, -t, 0.0F)));
        temp.add(new PreTriangle(new Vector3f(+1.0F, -t, 0.0F), new Vector3f(-1.0F, -t, 0.0F), new Vector3f(0.0F, -1.0F, -t)));
        temp.add(new PreTriangle(new Vector3f(+1.0F, -t, 0.0F), new Vector3f(0.0F, -1.0F, -t), new Vector3f(+t, 0.0F, -1.0F)));
        temp.add(new PreTriangle(new Vector3f(+1.0F, -t, 0.0F), new Vector3f(+t, 0.0F, -1.0F), new Vector3f(+t, 0.0F, +1.0F)));
        temp.add(new PreTriangle(new Vector3f(0.0F, -1.0F, +t), new Vector3f(+t, 0.0F, +1.0F), new Vector3f(0.0F, +1.0F, +t)));
        temp.add(new PreTriangle(new Vector3f(-1.0F, -t, 0.0F), new Vector3f(0.0F, -1.0F, +t), new Vector3f(-t, 0.0F, +1.0F)));
        temp.add(new PreTriangle(new Vector3f(0.0F, -1.0F, -t), new Vector3f(-1.0F, -t, 0.0F), new Vector3f(-t, 0.0F, -1.0F)));
        temp.add(new PreTriangle(new Vector3f(+t, 0.0F, -1.0F), new Vector3f(0.0F, -1.0F, -t), new Vector3f(0.0F, +1.0F, -t)));
        temp.add(new PreTriangle(new Vector3f(+t, 0.0F, +1.0F), new Vector3f(+t, 0.0F, -1.0F), new Vector3f(+1.0F, +t, 0.0F)));

        for (int i = 0; i < numDivisions; i++)
        {
            List<PreTriangle> newTriangles = new ArrayList<>();
            for (PreTriangle triangle : temp)
            {
                newTriangles.addAll(Arrays.asList(divideTriangle(triangle)));
            }
            temp.clear();
            temp.addAll(newTriangles);
        }

        List<Triangle> triangles = new ArrayList<>();

        for (PreTriangle triangle : temp)
        {
            triangle.v0 = MathUtils.mul(triangle.v0.normalise(null), radius);
            triangle.v1 = MathUtils.mul(triangle.v1.normalise(null), radius);
            triangle.v2 = MathUtils.mul(triangle.v2.normalise(null), radius);
            triangles.add(new Triangle(triangle.v0, triangle.v1, triangle.v2));
        }

        return new Collider(triangles, new Transformation());
    }

    public static PreTriangle[] divideTriangle(Triangle triangle)
    {
        return divideTriangle(new PreTriangle(triangle));
    }
    public static PreTriangle[] divideTriangle(PreTriangle triangle)
    {
        Vector3f a = triangle.v0;
        Vector3f b = triangle.v1;
        Vector3f c = triangle.v2;
        Vector3f ab = MathUtils.interpolate(a, b, 0.5F);
        Vector3f bc = MathUtils.interpolate(b, c, 0.5F);
        Vector3f ca = MathUtils.interpolate(c, a, 0.5F);

        return new PreTriangle[]{new PreTriangle(a, ab, ca), new PreTriangle(b, bc, ab), new PreTriangle(c, ca, bc), new PreTriangle(ab, bc, ca)};
    }
}