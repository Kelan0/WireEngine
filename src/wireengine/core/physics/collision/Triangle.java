package wireengine.core.physics.collision;

import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.util.MathUtils;

/**
 * @author Kelan
 */
public class Triangle
{
    private Vector3f p1;
    private Vector3f p2;
    private Vector3f p3;

    private Vector3f normal;

    public Triangle(Vector3f p1, Vector3f p2, Vector3f p3)
    {
        this.set(p1, p2, p3);
    }

    public void set(Vector3f p1, Vector3f p2, Vector3f p3)
    {
        if (p1 == null || p2 == null || p3 == null)
        {
            throw new IllegalArgumentException("Cannot construct triangle from null vertices.");
        }

        float temp = p1.lengthSquared() + p2.lengthSquared() + p3.lengthSquared();
        if (Float.isNaN(temp) || Float.isInfinite(temp))
        {
            throw new IllegalArgumentException("Cannot construct triangle from infinite vertex positions.");
        }

        Vector3f e1 = Vector3f.sub(p1, p2, null);
        Vector3f e2 = Vector3f.sub(p2, p3, null);
        Vector3f e3 = Vector3f.sub(p3, p1, null);

        if (e1.lengthSquared() == 0.0F || e2.lengthSquared() == 0.0F || e3.lengthSquared() == 0.0F)
        {
            throw new IllegalArgumentException("Cannot construct triangle from zero-length sides.");
        }

        this.normal = Vector3f.cross(e1, e2, null).normalise(null);
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public ReadableVector3f getP1()
    {
        return p1;
    }

    public ReadableVector3f getP2()
    {
        return p2;
    }

    public ReadableVector3f getP3()
    {
        return p3;
    }

    public ReadableVector3f getNormal()
    {
        return new Vector3f(normal);
    }

    public Vector3f getPosition()
    {
        return MathUtils.averageVector3f(p1, p2, p3);
    }

    public void setP1(Vector3f p1)
    {
        if (p1 != null)
        {
            this.set(p1, p2, p3);
        }
    }

    public void setP2(Vector3f p2)
    {
        if (p2 != null)
        {
            this.set(p1, p2, p3);
        }
    }

    public void setP3(Vector3f p3)
    {
        if (p3 != null)
        {
            this.set(p1, p2, p3);
        }
    }

    /**
     * Get the closest point on the surface of this triangle to {@code point}
     */
    public Vector3f getClosestPoint(Vector3f p)
    {
        // Implementation adapted from https://www.gamedev.net/forums/topic/552906-closest-point-on-triangle/

        Vector3f aEdge = Vector3f.sub(this.p2, this.p1, null);
        Vector3f bEdge = Vector3f.sub(this.p3, this.p1, null);
        Vector3f relPoint = Vector3f.sub(this.p1, p, null);

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

        return Vector3f.add(this.p1, Vector3f.add((Vector3f) aEdge.scale(u), (Vector3f) bEdge.scale(v), null), null);
    }

    /**
     * Check if the point {@code p} lies inside this triangle.
     */
    public boolean pointIntersects(Vector3f p)
    {
        //Implementation adapted from https://www.braynzarsoft.net/viewtutorial/q16390-31-sliding-camera-collision-detection
        Vector3f cp1, cp2;

        cp1 = Vector3f.cross(Vector3f.sub(p3, p2, null), Vector3f.sub(p, p2, null), null);
        cp2 = Vector3f.cross(Vector3f.sub(p3, p2, null), Vector3f.sub(p1, p2, null), null);
        if (Vector3f.dot(cp1, cp2) >= 0.0F)
        {
            cp1 = Vector3f.cross(Vector3f.sub(p3, p1, null), Vector3f.sub(p, p1, null), null);
            cp2 = Vector3f.cross(Vector3f.sub(p3, p1, null), Vector3f.sub(p2, p1, null), null);
            if (Vector3f.dot(cp1, cp2) >= 0.0F)
            {
                cp1 = Vector3f.cross(Vector3f.sub(p2, p1, null), Vector3f.sub(p, p1, null), null);
                cp2 = Vector3f.cross(Vector3f.sub(p2, p1, null), Vector3f.sub(p3, p1, null), null);
                if (Vector3f.dot(cp1, cp2) >= 0.0F)
                {
                    return true;
                }
            }
        }
        return false;
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
        return normal != null ? normal.equals(triangle.normal) : triangle.normal == null;
    }

    @Override
    public int hashCode()
    {
        int result = p1 != null ? p1.hashCode() : 0;
        result = 31 * result + (p2 != null ? p2.hashCode() : 0);
        result = 31 * result + (p3 != null ? p3.hashCode() : 0);
        result = 31 * result + (normal != null ? normal.hashCode() : 0);
        return result;
    }
}
