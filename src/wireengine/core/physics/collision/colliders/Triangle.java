package wireengine.core.physics.collision.colliders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.core.util.MathUtils;

/**
 * @author Kelan
 */
public class Triangle
{
    private Vector3f[] vertices = new Vector3f[]{null, null, null};

    private Vector3f position;
    private Vector3f normal;

    public Triangle(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f position)
    {
        this.set(p1, p2, p3);
        this.position = position;
    }

    public Triangle(Vector3f p1, Vector3f p2, Vector3f p3)
    {
        this(p1, p2, p3, new Vector3f());
    }

    public Triangle(Triangle triangle)
    {
        this.vertices[0] = new Vector3f(triangle.vertices[0]);
        this.vertices[1] = new Vector3f(triangle.vertices[1]);
        this.vertices[2] = new Vector3f(triangle.vertices[2]);

        this.position = new Vector3f(triangle.position);
        this.normal = new Vector3f(triangle.normal);
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
            throw new IllegalArgumentException("Cannot construct triangle from zero-length sides. {" + p1 + ", " + p2 + ", " + p3 + "}");
        }

        this.normal = Vector3f.cross(e1, e2, null).normalise(null);
        this.vertices[0] = p1;
        this.vertices[1] = p2;
        this.vertices[2] = p3;
    }

    public ReadableVector3f getP1()
    {
        return this.vertices[0];
    }

    public ReadableVector3f getP2()
    {
        return this.vertices[1];
    }

    public ReadableVector3f getP3()
    {
        return this.vertices[2];
    }

    public ReadableVector3f getNormal()
    {
        return new Vector3f(normal);
    }

    public Vector3f getCentre()
    {
        return MathUtils.averageVector3f(this.vertices);
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public Triangle getTransformed(Transformation transformation)
    {
        Triangle triangle = new Triangle(this);

        Matrix4f matrix = transformation.getMatrix(null);

        triangle.vertices[0] = new Vector3f(Matrix4f.transform(matrix, new Vector4f(triangle.vertices[0].x, triangle.vertices[0].y, triangle.vertices[0].z, 1.0F), null));
        triangle.vertices[1] = new Vector3f(Matrix4f.transform(matrix, new Vector4f(triangle.vertices[1].x, triangle.vertices[1].y, triangle.vertices[1].z, 1.0F), null));
        triangle.vertices[2] = new Vector3f(Matrix4f.transform(matrix, new Vector4f(triangle.vertices[2].x, triangle.vertices[2].y, triangle.vertices[2].z, 1.0F), null));
        triangle.position = new Vector3f(Matrix4f.transform(matrix, new Vector4f(triangle.position.x, triangle.position.y, triangle.position.z, 1.0F), null));
        triangle.normal = new Vector3f(Matrix4f.transform(matrix, new Vector4f(triangle.normal.x, triangle.normal.y, triangle.normal.z, 0.0F), null));

        return triangle;
    }

    public void setP1(Vector3f p1)
    {
        if (p1 != null)
        {
            this.set(p1, this.vertices[1], this.vertices[2]);
        }
    }

    public void setP2(Vector3f p2)
    {
        if (p2 != null)
        {
            this.set(this.vertices[0], p2, this.vertices[2]);
        }
    }

    public void setP3(Vector3f p3)
    {
        if (p3 != null)
        {
            this.set(this.vertices[0], this.vertices[1], p3);
        }
    }

    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    /**
     * Get the closest point on the surface of this triangle to {@code point}
     */
    public Vector3f getClosestPoint(Vector3f p)
    {
        p = Vector3f.sub(p, position, null);
        // Implementation adapted from https://www.gamedev.net/forums/topic/552906-closest-point-on-triangle/

        Vector3f aEdge = Vector3f.sub(this.vertices[1], this.vertices[0], null);
        Vector3f bEdge = Vector3f.sub(this.vertices[2], this.vertices[0], null);
        Vector3f relPoint = Vector3f.sub(this.vertices[0], p, null);

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

        return Vector3f.add(this.vertices[0], Vector3f.add((Vector3f) aEdge.scale(u), (Vector3f) bEdge.scale(v), null), null);
    }

    /**
     * Check if the point {@code p} lies inside this triangle.
     */
    public boolean pointIntersects(Vector3f p)
    {
        p = Vector3f.sub(p, position, null);
        //Implementation adapted from https://www.braynzarsoft.net/viewtutorial/q16390-31-sliding-camera-collision-detection
        Vector3f cp1, cp2;

        cp1 = Vector3f.cross(Vector3f.sub(this.vertices[2], this.vertices[1], null), Vector3f.sub(p, this.vertices[1], null), null);
        cp2 = Vector3f.cross(Vector3f.sub(this.vertices[2], this.vertices[1], null), Vector3f.sub(vertices[0], this.vertices[1], null), null);
        if (Vector3f.dot(cp1, cp2) >= 0.0F)
        {
            cp1 = Vector3f.cross(Vector3f.sub(this.vertices[2], vertices[0], null), Vector3f.sub(p, vertices[0], null), null);
            cp2 = Vector3f.cross(Vector3f.sub(this.vertices[2], vertices[0], null), Vector3f.sub(this.vertices[1], vertices[0], null), null);
            if (Vector3f.dot(cp1, cp2) >= 0.0F)
            {
                cp1 = Vector3f.cross(Vector3f.sub(this.vertices[1], vertices[0], null), Vector3f.sub(p, vertices[0], null), null);
                cp2 = Vector3f.cross(Vector3f.sub(this.vertices[1], vertices[0], null), Vector3f.sub(this.vertices[2], vertices[0], null), null);
                if (Vector3f.dot(cp1, cp2) >= 0.0F)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean rayIntersects(Ray ray, Vector3f destCollisionPoint)
    {
        if (destCollisionPoint == null)
        {
            destCollisionPoint = new Vector3f();
        }

        float epsilon = 0.0001F;
        Vector3f edge1, edge2, h, s, q;
        float a, f, u, v;

        edge1 = Vector3f.sub(this.vertices[1], vertices[0], null);
        edge2 = Vector3f.sub(this.vertices[2], vertices[0], null);
        h = Vector3f.cross(ray.direction, edge2, null);
        a = Vector3f.dot(edge1, h);

        if (a > -epsilon && a < epsilon)
        {
            return false;
        }

        f = 1.0F / a;
        s = Vector3f.sub(ray.origin, vertices[0], null);
        u = f * Vector3f.dot(s, h);
        if (u < 0.0 || u > 1.0)
        {
            return false;
        }
        q = Vector3f.cross(s, edge1, null);
        v = f * Vector3f.dot(ray.direction, q);
        if (v < 0.0 || u + v > 1.0)
        {
            return false;
        }

        // At this stage we can compute t to find out where the intersection point is on the line.
        float t = f * Vector3f.dot(edge2, q);
        if (t > epsilon) // ray intersection
        {
            destCollisionPoint.x = ray.origin.x + ray.direction.x * t;
            destCollisionPoint.y = ray.origin.y + ray.direction.y * t;
            destCollisionPoint.z = ray.origin.z + ray.direction.z * t;
            return true;
        } else // This means that there is a line intersection but not a ray intersection.
        {
            return false;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triangle triangle = (Triangle) o;

        if (vertices[0] != null ? !vertices[0].equals(triangle.vertices[0]) : triangle.vertices[0] != null)
            return false;
        if (this.vertices[1] != null ? !this.vertices[1].equals(triangle.vertices[1]) : triangle.vertices[1] != null)
            return false;
        if (this.vertices[2] != null ? !this.vertices[2].equals(triangle.vertices[2]) : triangle.vertices[2] != null)
            return false;
        return normal != null ? normal.equals(triangle.normal) : triangle.normal == null;
    }

    @Override
    public int hashCode()
    {
        int result = vertices[0] != null ? vertices[0].hashCode() : 0;
        result = 31 * result + (this.vertices[1] != null ? this.vertices[1].hashCode() : 0);
        result = 31 * result + (this.vertices[2] != null ? this.vertices[2].hashCode() : 0);
        result = 31 * result + (normal != null ? normal.hashCode() : 0);
        return result;
    }
}
