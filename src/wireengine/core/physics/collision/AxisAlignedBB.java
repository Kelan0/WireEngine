package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.rendering.Axis;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.util.MathUtils;

import static org.lwjgl.opengl.GL11.GL_LINES;

/**
 * @author Kelan
 */
public class AxisAlignedBB extends Collider
{
    protected Vector3f position;
    protected Vector3f min;
    protected Vector3f max;

    public AxisAlignedBB(Vector3f position, Vector3f min, Vector3f max)
    {
        super(ColliderType.AABB);
        this.set(position, min, max);
    }

    public AxisAlignedBB(Vector3f min, Vector3f max)
    {
        super(ColliderType.AABB);
        this.set(MathUtils.averageVector3f(min, max), min, max);
    }

    private void set(Vector3f position, Vector3f min, Vector3f max)
    {
        this.position = position;
        this.min = min;
        this.max = max;
    }

    public void renderDebug(ShaderProgram shaderProgram, Vector4f colour)
    {
        DebugRenderer.getInstance().begin(GL_LINES);

        DebugRenderer.getInstance().addColour(colour);
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMin().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMin().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMin().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMax().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMax().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMax().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMax().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMin().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMin().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMin().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMin().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMax().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMax().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMax().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMax().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMin().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMin().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMin().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMin().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMin().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMax().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMax().x, getMax().y, getMax().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMax().y, getMin().z));
        DebugRenderer.getInstance().addVertex(new Vector3f(getMin().x, getMax().y, getMax().z));

        DebugRenderer.getInstance().end(shaderProgram);
    }

    @Override
    public CollisionHandler getCollision(Collider collider, float epsilon)
    {
        return null;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        return point.x > min.x && point.x < max.x && point.y > min.y && point.y < max.y && point.z > min.z && point.z < max.z;
    }

    @Override
    public Vector3f getClosestPoint(Vector3f point)
    {
        Vector3f closest = new Vector3f();

        closest.x = MathUtils.clamp(point.x, min.x, max.x);
        closest.y = MathUtils.clamp(point.y, min.y, max.y);
        closest.z = MathUtils.clamp(point.z, min.z, max.z);

        return closest;
    }

    @Override
    public Vector3f getNormalAt(Vector3f point)
    {
        Vector3f normal = new Vector3f();
        Axis axis = Axis.getWorldAxis();
        Vector3f dimensions = Vector3f.sub(max, min, null);
        float min = Float.MAX_VALUE;

        point = Vector3f.sub(point, this.position, null);

        float xDistance = Math.abs(dimensions.x - Math.abs(point.x));
        if (xDistance < min)
        {
            min = xDistance;
            normal = (Vector3f) axis.getX().scale(MathUtils.sign(point.x));
        }

        float yDistance = Math.abs(dimensions.y - Math.abs(point.y));
        if (yDistance < min)
        {
            min = yDistance;
            normal = (Vector3f) axis.getY().scale(MathUtils.sign(point.y));
        }

        float zDistance = Math.abs(dimensions.z - Math.abs(point.z));
        if (zDistance < min)
        {
            min = zDistance;
            normal = (Vector3f) axis.getZ().scale(MathUtils.sign(point.z));
        }

        return normal;
    }

    @Override
    public Vector3f getPosition()
    {
        return position;
    }

    @Override
    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public Vector3f getMin()
    {
        return min;
    }

    public void setMin(Vector3f min)
    {
        this.min = min;
    }

    public Vector3f getMax()
    {
        return max;
    }

    public void setMax(Vector3f max)
    {
        this.max = max;
    }
}
