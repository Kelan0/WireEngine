package wireengine.core.physics.collision;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.rendering.Axis;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.util.Constants;
import wireengine.core.util.MathUtils;

import static org.lwjgl.opengl.GL11.GL_LINES;

/**
 * @author Kelan
 */
public class Cylinder extends Collider
{
    protected Plane plane; //A plane intersecting this cylinder orthogonal to the up-axis.
    protected Vector3f position;
    protected float radius;
    protected float height;

    public Cylinder(Vector3f position, Vector3f up, float radius, float height)
    {
        super(ColliderType.CYLINDER);
        this.set(position, up, radius, height);
    }

    public void set(Vector3f position, Vector3f up, float radius, float height)
    {
        this.plane = new Plane(position, up);
        this.position = position;
        this.radius = radius;
        this.height = height;
    }

    public void renderDebug(ShaderProgram shaderProgram, Vector4f colour)
    {
        int divisions = 12;

        DebugRenderer.getInstance().begin(GL_LINES);
        DebugRenderer.getInstance().addColour(colour);

        double angleStep = ((Constants.PI * 2.0) / divisions);
        Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
        Vector3f norm = this.plane.getNormalAt(null);
        Quaternion rotation = MathUtils.axisAngleToQuaternion(Vector3f.cross(up, norm, null), MathUtils.getAngleVector3f(up, norm), null);

        for (int i = 0; i < divisions; i++)
        {
            Vector3f aBot = new Vector3f((float) Math.cos(angleStep * i) * radius, -this.height * 0.5F, (float) Math.sin(angleStep * i) * radius);
            Vector3f aTop = new Vector3f((float) Math.cos(angleStep * i) * radius, +this.height * 0.5F, (float) Math.sin(angleStep * i) * radius);
            Vector3f bBot = new Vector3f((float) Math.cos(angleStep * (i + 1)) * radius, -this.height * 0.5F, (float) Math.sin(angleStep * (i + 1)) * radius);
            Vector3f bTop = new Vector3f((float) Math.cos(angleStep * (i + 1)) * radius, +this.height * 0.5F, (float) Math.sin(angleStep * (i + 1)) * radius);

            Vector3f.add(MathUtils.rotateVector3f(rotation, aBot, aBot), this.position, aBot);
            Vector3f.add(MathUtils.rotateVector3f(rotation, aTop, aTop), this.position, aTop);
            Vector3f.add(MathUtils.rotateVector3f(rotation, bBot, bBot), this.position, bBot);
            Vector3f.add(MathUtils.rotateVector3f(rotation, bTop, bTop), this.position, bTop);

            DebugRenderer.getInstance().addVertex(aBot);
            DebugRenderer.getInstance().addVertex(bBot);
            DebugRenderer.getInstance().addVertex(aTop);
            DebugRenderer.getInstance().addVertex(bTop);
            DebugRenderer.getInstance().addVertex(aBot);
            DebugRenderer.getInstance().addVertex(aTop);

            aBot = this.getRelativeCoordinate(aBot);
            aTop = this.getRelativeCoordinate(aTop);
            bBot = this.getRelativeCoordinate(bBot);
            bTop = this.getRelativeCoordinate(bTop);

            DebugRenderer.getInstance().addColour(new Vector4f(0.0F, 0.0F, 1.0F, 1.0F));
            DebugRenderer.getInstance().addVertex(aBot);
            DebugRenderer.getInstance().addVertex(bBot);
            DebugRenderer.getInstance().addVertex(aTop);
            DebugRenderer.getInstance().addVertex(bTop);
            DebugRenderer.getInstance().addVertex(aBot);
            DebugRenderer.getInstance().addVertex(aTop);
        }

        DebugRenderer.getInstance().end(shaderProgram);
    }

    @Override
    public CollisionHandler getCollision(Collider collider, float epsilon)
    {
        CollisionHandler collision = new CollisionHandler();

        if (ColliderType.TRIANGLE.equals(collider.colliderType))
        {
            collision.hitData = new Vector3f[0];
            Triangle triangle = (Triangle) collider;

            Vector3f a1 = this.getRelativeCoordinate(triangle.getP1());
            Vector3f a2 = this.getRelativeCoordinate(triangle.getP2());
            Vector3f a3 = this.getRelativeCoordinate(triangle.getP3());

            Vector3f[] temp = new Vector3f[]{a1, a2, a3};

            int[] order = getOrderedVertices(a1, a2, a3); //Order the vertices by y-value. Required for clipping.
            Vector3f[] verts = new Vector3f[]{temp[order[0]], temp[order[1]], temp[order[2]]};

            if (verts[2].y < -this.height * 0.5F || verts[0].y > this.height * 0.5F)
            {
                return collision;
            }

            Vector3f[] poly = new Vector3f[5];
            int polysize = getClippedPolygon(verts, poly);

            for (int i = 0; i < polysize; i++)
            {
                poly[i].y = 0.0F;
            }

            if (discOverlapsPoly(poly, polysize, collision))
            {
                collision.didHit = true;
                return collision;
            }
        }

        return collision;
    }

    private int[] getOrderedVertices(Vector3f v1, Vector3f v2, Vector3f v3)
    {
        if (v1.y < v2.y)
        {
            if (v3.y < v1.y)
            {
                return new int[]{2, 0, 1};
            } else if (v3.y < v2.y)
            {
                return new int[]{0, 2, 1};
            } else
            {
                return new int[]{0, 1, 2};
            }
        } else
        {
            if (v3.y < v2.y)
            {
                return new int[]{2, 1, 0};
            } else if (v3.y < v1.y)
            {
                return new int[]{1, 2, 0};
            } else
            {
                return new int[]{1, 0, 2};
            }
        }
    }

    private boolean discOverlapsPoly(Vector3f[] polygon, int polysize, CollisionHandler collision)
    {
        if (polysize > 0)
        {
            for (int i = 0; i < polysize; i++)
            {
                if (polygon[i].lengthSquared() == Float.NaN || polygon[i].lengthSquared() == Float.NEGATIVE_INFINITY || polygon[i].lengthSquared() == Float.POSITIVE_INFINITY)
                {
                    return false;
                }
            }

            if (pointInsidePolygon(new Vector3f(), polygon, polysize))
            {
                collision.hitData = new Vector3f[polysize];
                for (int i = 0; i < polysize; i++)
                {
                    ((Vector3f[]) collision.hitData)[i] = polygon[i];
                }
                collision.string = "Disc intersects poly";
                return true;
            }
            for (int i = 0, j = polysize - 1; i < polysize; j = i++)
            {
                if (discOverlapsPoint(polygon[i]))
                {
                    collision.hitData = new Vector3f[]{polygon[i]};
                    collision.string = "Point intersects disc";
                    return true;
                }

                if (discOverlapsEdge(polygon[i], polygon[j]))
                {
                    collision.hitData = new Vector3f[]{polygon[i], polygon[j]};
                    collision.string = "Edge intersects disc";
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pointInsidePolygon(Vector3f point, Vector3f[] polygon, int polysize)
    {
        boolean result = false;
        for (int i = 0, j = polysize - 1; i < polysize; j = i++)
        {
            if ((polygon[i].z > point.z) != (polygon[j].z > point.z) && (point.x < (polygon[j].x - polygon[i].x) * (point.z - polygon[i].z) / (polygon[j].z - polygon[i].z) + polygon[i].x))
            {
                result = !result;
            }
        }

        return result;
    }

    private boolean discOverlapsPoint(Vector3f p)
    {
        Vector3f distance = p;//Vector3f.sub(p, new Vector3f(this.getCentre().x, 0.0F, this.getCentre().z), null);
        if (distance.lengthSquared() < this.radius * this.radius)
        {
            return true;
        }

        return false;
    }

    private boolean discOverlapsEdge(Vector3f p1, Vector3f p2)
    {
        Vector3f centre = new Vector3f();//new Vector3f(this.getCentre().x, 0.0F, this.getCentre().z);
        float c1x = centre.x - p1.x;
        float c1y = centre.z - p1.z;
        float e1x = p2.x - p1.x;
        float e1y = p2.z - p1.z;

        float k = c1x * e1x + c1y * e1y;

        if (k > 0.0F)
        {
            float len = (float) Math.sqrt(e1x * e1x + e1y * e1y);
            k = k / len;

            if (k < len && c1x * c1x + c1y * c1y - k * k <= this.radius * this.radius)
            {
                return true;
            }
        }

        return false;
    }

    private int getClippedPolygon(Vector3f[] triangle, Vector3f[] clipped)
    {
        float hhalf = this.height * 0.5F;

        if (triangle[0].y < -hhalf)
        {
            if (triangle[2].y > hhalf)
            {
                if (triangle[1].y >= hhalf)
                {
                    clipped[0] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[2], triangle[0], null).scale((-hhalf - triangle[0].y) / (triangle[2].y - triangle[0].y)), null);
                    clipped[1] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[1], triangle[0], null).scale((-hhalf - triangle[0].y) / (triangle[1].y - triangle[0].y)), null);
                    clipped[2] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[1], triangle[0], null).scale((+hhalf - triangle[0].y) / (triangle[1].y - triangle[0].y)), null);
                    clipped[3] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[2], triangle[0], null).scale((+hhalf - triangle[0].y) / (triangle[2].y - triangle[0].y)), null);
                    return 4;
                } else if (triangle[1].y <= -hhalf)
                {
                    clipped[0] = Vector3f.add(triangle[2], (Vector3f) Vector3f.sub(triangle[0], triangle[2], null).scale((-hhalf - triangle[2].y) / (triangle[0].y - triangle[2].y)), null);
                    clipped[1] = Vector3f.add(triangle[2], (Vector3f) Vector3f.sub(triangle[1], triangle[2], null).scale((-hhalf - triangle[2].y) / (triangle[1].y - triangle[2].y)), null);
                    clipped[2] = Vector3f.add(triangle[2], (Vector3f) Vector3f.sub(triangle[1], triangle[2], null).scale((+hhalf - triangle[2].y) / (triangle[1].y - triangle[2].y)), null);
                    clipped[3] = Vector3f.add(triangle[2], (Vector3f) Vector3f.sub(triangle[0], triangle[2], null).scale((+hhalf - triangle[2].y) / (triangle[0].y - triangle[2].y)), null);
                    return 4;
                } else
                {
                    clipped[0] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[2], triangle[0], null).scale((-hhalf - triangle[0].y) / (triangle[2].y - triangle[0].y)), null);
                    clipped[1] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[1], triangle[0], null).scale((-hhalf - triangle[0].y) / (triangle[1].y - triangle[0].y)), null);
                    clipped[3] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[1], triangle[0], null).scale((+hhalf - triangle[0].y) / (triangle[1].y - triangle[0].y)), null);
                    clipped[4] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[2], triangle[0], null).scale((+hhalf - triangle[0].y) / (triangle[2].y - triangle[0].y)), null);
                    clipped[2] = triangle[1];
                    return 5;
                }
            } else if (triangle[2].y > -hhalf)
            {
                if (triangle[1].y <= -hhalf)
                {
                    clipped[0] = Vector3f.add(triangle[2], (Vector3f) Vector3f.sub(triangle[1], triangle[2], null).scale((-hhalf - triangle[2].y) / (triangle[1].y - triangle[2].y)), null);
                    clipped[1] = Vector3f.add(triangle[2], (Vector3f) Vector3f.sub(triangle[0], triangle[2], null).scale((-hhalf - triangle[2].y) / (triangle[0].y - triangle[2].y)), null);
                    clipped[2] = triangle[2];
                    return 3;
                } else
                {
                    clipped[0] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[2], triangle[0], null).scale((-hhalf - triangle[0].y) / (triangle[2].y - triangle[0].y)), null);
                    clipped[1] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[1], triangle[0], null).scale((-hhalf - triangle[0].y) / (triangle[1].y - triangle[0].y)), null);
                    clipped[2] = triangle[1];
                    clipped[3] = triangle[2];
                    return 4;
                }
            } else
            {
                if (triangle[1].y < -hhalf)
                {
                    clipped[0] = triangle[2];
                    return 1;
                } else
                {
                    clipped[0] = triangle[2];
                    clipped[0] = triangle[1];
                    return 2;
                }
            }
        } else if (triangle[0].y < hhalf)
        {
            if (triangle[1].y >= hhalf)
            {
                clipped[0] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[2], triangle[0], null).scale((-hhalf - triangle[0].y) / (triangle[2].y - triangle[0].y)), null);
                clipped[1] = Vector3f.add(triangle[0], (Vector3f) Vector3f.sub(triangle[1], triangle[0], null).scale((-hhalf - triangle[0].y) / (triangle[1].y - triangle[0].y)), null);
                clipped[2] = triangle[0];
                return 3;
            } else
            {
                clipped[0] = Vector3f.add(triangle[2], (Vector3f) Vector3f.sub(triangle[1], triangle[2], null).scale((-hhalf - triangle[2].y) / (triangle[1].y - triangle[2].y)), null);
                clipped[1] = Vector3f.add(triangle[2], (Vector3f) Vector3f.sub(triangle[0], triangle[2], null).scale((-hhalf - triangle[2].y) / (triangle[0].y - triangle[2].y)), null);
                clipped[2] = triangle[0];
                clipped[3] = triangle[1];
                return 4;
            }
        } else
        {
            if (triangle[1].y > hhalf)
            {
                clipped[0] = triangle[0];
                return 1;
            } else
            {
                clipped[0] = triangle[0];
                clipped[1] = triangle[1];
                return 2;
            }
        }
    }

    public Vector3f getRelativeCoordinate(Vector3f point)
    {
        point = Vector3f.sub(point, this.getCentre(), null);
        Vector3f u = new Vector3f(0.0F, 1.0F, 0.0F);
        Vector3f n = this.plane.getNormalAt(null);

        Quaternion rotation = MathUtils.axisAngleToQuaternion(Vector3f.cross(n, u, null), MathUtils.getAngleVector3f(u, n), null);
        return Vector3f.add(new Vector3f(), MathUtils.rotateVector3f(rotation, point, null), null);
    }

    public Axis getAxis()
    {
        Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
        Vector3f norm = this.plane.getNormalAt(null);
        Quaternion rotation = MathUtils.axisAngleToQuaternion(Vector3f.cross(up, norm, null), MathUtils.getAngleVector3f(up, norm), null);

        return Axis.getWorldAxis().rotate(rotation, null);
    }

    @Override
    public boolean pointIntersects(Vector3f point)
    {
        Vector3f pointOnPlane = this.plane.getClosestPoint(point);
        Vector3f centreOnPlane = this.plane.getClosestPoint(this.getCentre());
        Vector3f centreToPoint = Vector3f.sub(pointOnPlane, centreOnPlane, null);

        float radialDistanceSquared = centreToPoint.lengthSquared(); //distance between the point, projected onto the plane, and the centre of the cylinder projected onto the plane.
        float signedAxialDistance = this.plane.getSignedDistance(point); //Height above/below the plane. Negative if below, positive otherwise.

        return radialDistanceSquared < this.radius * this.radius && signedAxialDistance > -this.height * 0.5F && signedAxialDistance < this.height * 0.5F;
    }

    @Override
    public Vector3f getClosestPoint(Vector3f point)
    {
        Vector3f pointOnPlane = this.plane.getClosestPoint(point);
        Vector3f centreOnPlane = this.plane.getClosestPoint(this.getCentre());
        Vector3f centreToPoint = Vector3f.sub(pointOnPlane, centreOnPlane, null);
        Vector3f closestPoint = new Vector3f(centreToPoint);

        float radialDistanceSquared = centreToPoint.lengthSquared(); //distance between the point, projected onto the plane, and the centre of the cylinder projected onto the plane.
        float signedAxialDistance = this.plane.getSignedDistance(point); //Height above/below the plane. Negative if below, positive otherwise.

        if (radialDistanceSquared > this.radius * this.radius)
        {
            closestPoint.normalise().scale((float) Math.min(Math.sqrt(radialDistanceSquared), this.radius));
        }

        Vector3f.add((Vector3f) this.plane.getNormalAt(null).scale(signedAxialDistance - this.plane.getDistance()), closestPoint, closestPoint); // Translate the point back off the plane.
        Vector3f.add(Vector3f.add(centreOnPlane, (Vector3f) this.plane.getNormalAt(null).scale(this.plane.getDistance()), null), closestPoint, closestPoint);

        if (signedAxialDistance > this.height * 0.5F)
        {
            Vector3f.add((Vector3f) this.plane.getNormalAt(null).scale(this.height * 0.5F - signedAxialDistance), closestPoint, closestPoint);
        }

        if (signedAxialDistance < -this.height * 0.5F)
        {
            Vector3f.add((Vector3f) this.plane.getNormalAt(null).scale(-this.height * 0.5F - signedAxialDistance), closestPoint, closestPoint);
        }

        return closestPoint;
    }

    @Override
    public Vector3f getNormalAt(Vector3f point)
    {
        return null;
    }

    @Override
    public Vector3f getPosition()
    {
        return position;
    }

    @Override
    public void setPosition(Vector3f position)
    {
        this.set(position, getUp(), radius, height);
    }

    public Vector3f getUp()
    {
        return new Vector3f(this.plane.normal);
    }

    public void setUp(Vector3f up)
    {
        this.set(position, up, radius, height);
    }

    public float getRadius()
    {
        return radius;
    }

    public void setRadius(float radius)
    {
        this.set(position, getUp(), radius, height);
    }

    public float getHeight()
    {
        return height;
    }

    public void setHeight(float height)
    {
        this.set(position, getUp(), radius, height);
    }
}
