package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionState;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.util.MathUtils;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public class Triangle extends Collider<Triangle>
{
    protected Vector3f p1;
    protected Vector3f p2;
    protected Vector3f p3;
    protected Plane plane;

    public Triangle(Vector3f p1, Vector3f p2, Vector3f p3)
    {
        super(ColliderType.TRIANGLE);
        this.set(p1, p2, p3);
    }

    public Triangle(Triangle triangle)
    {
        super(ColliderType.TRIANGLE);
        this.p1 = new Vector3f(triangle.p1);
        this.p2 = new Vector3f(triangle.p2);
        this.p3 = new Vector3f(triangle.p3);
        this.plane = new Plane(triangle.plane.getPosition(), triangle.plane.getNormalAt(null));
    }

    public void set(Vector3f p1, Vector3f p2, Vector3f p3)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        Vector3f edge1 = Vector3f.sub(p2, p1, null);
        Vector3f edge2 = Vector3f.sub(p3, p1, null);

        Vector3f normal = Vector3f.cross(edge1, edge2, null);

        this.plane = new Plane(this.getPosition(), normal);
    }

    public void renderDebug(ShaderProgram shaderProgram, Vector4f colour, int type)
    {
        DebugRenderer.getInstance().begin(GL_TRIANGLES);

        int polyMode = glGetInteger(GL_POLYGON_MODE);


        DebugRenderer.getInstance().addColour(colour);
        DebugRenderer.getInstance().addVertex(new Vector3f(this.getP1()));
        DebugRenderer.getInstance().addVertex(new Vector3f(this.getP2()));
        DebugRenderer.getInstance().addVertex(new Vector3f(this.getP3()));

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glPolygonMode(GL_FRONT_AND_BACK, type);
        DebugRenderer.getInstance().end(shaderProgram);
        glPolygonMode(GL_FRONT_AND_BACK, polyMode);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }

    @Override
    public CollisionState.CollisionComponent<Triangle> getCollision(AxisAlignedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Triangle> getCollision(Ellipsoid collider)
    {
        CollisionState.CollisionComponent<Triangle> collision = new CollisionState.CollisionComponent<>(this);

        Vector3f closestOnCollider = collider.getClosestPoint(this.getClosestPoint(collider.getCentre()));

        if (this.pointIntersects(closestOnCollider))
        {
            collision.collisionPoint = this.getClosestPoint(closestOnCollider);
            collision.collisionNormal = collider.getNormalAt(null);
            return collision;
        }

        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Triangle> getCollision(Frustum collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Triangle> getCollision(OrientedBB collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Triangle> getCollision(Plane collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Triangle> getCollision(Ray collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Triangle> getCollision(Sphere collider)
    {
        return null;
    }

    @Override
    public CollisionState.CollisionComponent<Triangle> getCollision(Triangle collider)
    {
        return null;
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        Vector3f aEdge = Vector3f.sub(this.getP3(), this.getP1(), null);
        Vector3f bEdge = Vector3f.sub(this.getP2(), this.getP1(), null);
        Vector3f relPoint = Vector3f.sub(point, this.getP1(), null);

        float aEdgeLengthSquared = Vector3f.dot(aEdge, aEdge);
        float bEdgeLengthSquared = Vector3f.dot(bEdge, bEdge);
        float aDotb = Vector3f.dot(aEdge, bEdge);
        float aDotRel = Vector3f.dot(aEdge, relPoint);
        float bDotRel = Vector3f.dot(bEdge, relPoint);

        float invBarycentricScaler = 1.0F / (aEdgeLengthSquared * bEdgeLengthSquared - aDotb * aDotb);
        float u = (bEdgeLengthSquared * aDotRel - aDotb * bDotRel) * invBarycentricScaler;
        float v = (aEdgeLengthSquared * bDotRel - aDotb * aDotRel) * invBarycentricScaler;

        return (u >= 0.0F) && (v >= 0.0F) && (u + v < 1.0F);
    }

    public Vector3f getClosestPoint(Vector3f point)
    {
        // Implementation adapted from https://www.gamedev.net/forums/topic/552906-closest-point-on-triangle/

        Vector3f aEdge = Vector3f.sub(this.getP2(), this.getP1(), null);
        Vector3f bEdge = Vector3f.sub(this.getP3(), this.getP1(), null);
        Vector3f relPoint = Vector3f.sub(this.getP1(), point, null);

        float aEdgeLengthSquared = Vector3f.dot(aEdge, aEdge);
        float bEdgeLengthSquared = Vector3f.dot(bEdge, bEdge);
        float aDotb = Vector3f.dot(aEdge, bEdge);
        float aDotRel = Vector3f.dot(aEdge, relPoint);
        float bDotRel = Vector3f.dot(bEdge, relPoint);

        float barycentricScaler = aEdgeLengthSquared * bEdgeLengthSquared - aDotb * aDotb;
        float u = aDotb * bDotRel - bEdgeLengthSquared * aDotRel;
        float v = aDotb * aDotRel - aEdgeLengthSquared * bDotRel;

        if (u + v < barycentricScaler)
        {
            if (u < 0.0F)
            {
                if (v < 0.0F)
                {
                    if (aDotRel < 0.0F)
                    {
                        u = MathUtils.clamp(-aDotRel / aEdgeLengthSquared, 0.0F, 1.0F);
                        v = 0.0F;
                    } else
                    {
                        u = 0.0F;
                        v = MathUtils.clamp(-bDotRel / bEdgeLengthSquared, 0.0F, 1.0F);
                    }
                } else
                {
                    u = 0.0F;
                    v = MathUtils.clamp(-bDotRel / bEdgeLengthSquared, 0.0F, 1.0F);
                }
            } else if (v < 0.0F)
            {
                u = MathUtils.clamp(-aDotRel / aEdgeLengthSquared, 0.0F, 1.0F);
                v = 0.0F;
            } else
            {
                float invDet = 1.0F / barycentricScaler;
                u *= invDet;
                v *= invDet;
            }
        } else
        {
            if (u < 0.0F)
            {
                float tmp0 = aDotb + aDotRel;
                float tmp1 = bEdgeLengthSquared + bDotRel;
                if (tmp1 > tmp0)
                {
                    float numer = tmp1 - tmp0;
                    float denom = aEdgeLengthSquared - 2 * aDotb + bEdgeLengthSquared;
                    u = MathUtils.clamp(numer / denom, 0.0F, 1.0F);
                    v = 1 - u;
                } else
                {
                    v = MathUtils.clamp(-bDotRel / bEdgeLengthSquared, 0.0F, 1.0F);
                    u = 0.0F;
                }
            } else if (v < 0.0F)
            {
                if (aEdgeLengthSquared + aDotRel > aDotb + bDotRel)
                {
                    float numer = bEdgeLengthSquared + bDotRel - aDotb - aDotRel;
                    float denom = aEdgeLengthSquared - 2 * aDotb + bEdgeLengthSquared;
                    u = MathUtils.clamp(numer / denom, 0.0F, 1.0F);
                    v = 1 - u;
                } else
                {
                    u = MathUtils.clamp(-bDotRel / bEdgeLengthSquared, 0.0F, 1.0F);
                    v = 0.0F;
                }
            } else
            {
                float numer = bEdgeLengthSquared + bDotRel - aDotb - aDotRel;
                float denom = aEdgeLengthSquared - 2 * aDotb + bEdgeLengthSquared;
                u = MathUtils.clamp(numer / denom, 0.0F, 1.0F);
                v = 1.0F - u;
            }
        }

        return Vector3f.add(this.getP1(), Vector3f.add((Vector3f) aEdge.scale(u), (Vector3f) bEdge.scale(v), null), null);
    }

    public Vector3f pointOnPlane(Vector3f point)
    {
        return this.plane.getClosestPoint(point);
    }

    @Override
    public Vector3f getNormalAt(Vector3f point)
    {
        return this.plane.getNormalAt(point);
    }

    public Plane getPlane()
    {
        return new Plane(this.getPosition(), this.plane.getNormalAt(null));
    }

    public float getSignedDistance(Vector3f point)
    {
        return this.plane.getSignedDistance(point);
    }

    @Override
    public Vector3f getPosition()
    {
        return MathUtils.averageVector3f(p1, p2, p3);
    }

    @Override
    public void setPosition(Vector3f position)
    {
        Vector3f difference = Vector3f.sub(position, getPosition(), null);

        Vector3f p1 = Vector3f.add(difference, this.p1, null);
        Vector3f p2 = Vector3f.add(difference, this.p2, null);
        Vector3f p3 = Vector3f.add(difference, this.p3, null);

        this.set(p1, p2, p3);
    }

    public Vector3f getP1()
    {
        return p1;
    }

    public Vector3f getP2()
    {
        return p2;
    }

    public Vector3f getP3()
    {
        return p3;
    }

    public float getArea()
    {
        float d1 = (p2.y - p1.y) * (p3.z - p1.z) - (p2.z - p1.z) * (p3.y - p1.y);
        float d2 = (p2.z - p1.z) * (p3.x - p1.x) - (p2.x - p1.x) * (p3.z - p1.z);
        float d3 = (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);

        return (float) Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3) * 0.5F;
    }

    public void setP1(Vector3f p1)
    {
        this.set(p1, p2, p3);
    }

    public void setP2(Vector3f p2)
    {
        this.set(p1, p2, p3);
    }

    public void setP3(Vector3f p3)
    {
        this.set(p1, p2, p3);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triangle triangle = (Triangle) o;

        if (p1 != null ? !p1.equals(triangle.p1) : triangle.p1 != null) return false;
        if (p2 != null ? !p2.equals(triangle.p2) : triangle.p2 != null) return false;
        if (p3 != null ? !p3.equals(triangle.p3) : triangle.p3 != null) return false;
        return plane != null ? plane.equals(triangle.plane) : triangle.plane == null;
    }

    @Override
    public int hashCode()
    {
        int result = p1 != null ? p1.hashCode() : 0;
        result = 31 * result + (p2 != null ? p2.hashCode() : 0);
        result = 31 * result + (p3 != null ? p3.hashCode() : 0);
        result = 31 * result + (plane != null ? plane.hashCode() : 0);
        return result;
    }
}
