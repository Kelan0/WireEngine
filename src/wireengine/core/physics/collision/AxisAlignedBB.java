package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.rendering.renderer.ShaderProgram;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.util.MathUtils;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

/**
 * @author Kelan
 */
public class AxisAlignedBB
{
    private Transformation transformation = new Transformation();
    private Vector3f extents = new Vector3f();

    public AxisAlignedBB(Transformation transformation, Vector3f extents)
    {
        this.transformation = transformation;
        this.extents = extents;
    }

    public AxisAlignedBB(Vector3f extents)
    {
        this(new Transformation(), extents);
    }

    private AxisAlignedBB()
    {
    }

    public static AxisAlignedBB getMinimumEnclosingBB(List<? extends Vector3f> pointCloud)
    {
        Vector3f min = new Vector3f();
        Vector3f max = new Vector3f();
        Vector3f centre = new Vector3f();

        for (Vector3f point : pointCloud)
        {
            min.x = Math.min(min.x, point.x);
            min.y = Math.min(min.y, point.y);
            min.z = Math.min(min.z, point.z);

            max.x = Math.max(max.x, point.x);
            max.y = Math.max(max.y, point.y);
            max.z = Math.max(max.z, point.z);

            Vector3f.add(point, centre, centre);
        }

        centre.scale(1.0F / pointCloud.size());

        Vector3f.sub(min, centre, min);
        Vector3f.sub(max, centre, max);

        return new AxisAlignedBB((Vector3f) Vector3f.sub(max, min, null).scale(0.5F));
    }

    public boolean intersects(AxisAlignedBB aabb)
    {
        Vector3f extents0 = this.getExtents(true);
        Vector3f extents1 = aabb.getExtents(true);
        Vector3f min0 = Vector3f.sub(this.transformation.getTranslation(), extents0, null);
        Vector3f max0 = Vector3f.add(this.transformation.getTranslation(), extents0, null);
        Vector3f min1 = Vector3f.sub(aabb.transformation.getTranslation(), extents1, null);
        Vector3f max1 = Vector3f.add(aabb.transformation.getTranslation(), extents1, null);

        return max0.x > min1.x && min0.x < max1.x && max0.y > min1.y && min0.y < max1.y && max0.z > min1.z && min0.z < max1.z;
    }

    public void setTransform(Transformation transformation)
    {
        this.transformation = transformation;
    }

    public Vector3f getExtents(boolean rotated)
    {
        if (!rotated)
        {
            return extents;
        } else
        {
            Matrix3f rotation = MathUtils.matrix4fToMatrix3f(this.transformation.getMatrix(null), null);

            Vector3f extents = new Vector3f();
            extents.x = Math.abs(rotation.m00) * this.extents.x + Math.abs(rotation.m10) * this.extents.y + Math.abs(rotation.m20) * this.extents.z;
            extents.y = Math.abs(rotation.m01) * this.extents.x + Math.abs(rotation.m11) * this.extents.y + Math.abs(rotation.m21) * this.extents.z;
            extents.z = Math.abs(rotation.m02) * this.extents.x + Math.abs(rotation.m12) * this.extents.y + Math.abs(rotation.m22) * this.extents.z;
           return extents;
        }
    }

    public void renderDebug(ShaderProgram shaderProgram, Vector4f colour)
    {
        Vector3f extents = this.getExtents(true);
        Vector3f min = Vector3f.sub(this.transformation.getTranslation(), extents, null);
        Vector3f max = Vector3f.add(this.transformation.getTranslation(), extents, null);

        DebugRenderer.getInstance().begin(GL_LINES);
        DebugRenderer.getInstance().addColour(colour);

        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, min.y, min.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, min.y, min.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, min.y, min.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, max.y, min.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, max.y, min.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, max.y, min.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, max.y, min.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, min.y, min.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, min.y, min.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, min.y, max.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, max.y, min.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, max.y, max.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, max.y, min.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, max.y, max.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, min.y, min.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, min.y, max.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, min.y, max.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, min.y, max.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, min.y, max.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, max.y, max.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(max.x, max.y, max.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, max.y, max.z));

        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, max.y, max.z));
        DebugRenderer.getInstance().addVertex(new Vector3f(min.x, min.y, max.z));

        DebugRenderer.getInstance().end(shaderProgram);
    }
}
