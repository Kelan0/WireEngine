package wireengine.core.physics.collision.algorithm;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.physics.collision.Collider;

/**
 * @author Kelan
 */
public class GJK
{
    public float epsilon = 0.0001F;
    public Collider collider1;
    public Collider collider2;
    public Vector3f direction;
    public Simplex simplex;
    public boolean didCollide;
    public int iterations;

    private GJK(Collider collider1, Collider collider2)
    {
        this.collider1 = collider1;
        this.collider2 = collider2;
        this.direction = new Vector3f(1.0F, 0.0F, 0.0F);
        this.simplex = new Simplex();
        this.didCollide = false;
        this.iterations = 0;
    }

    protected SupportPoint getSupport(Vector3f direction)
    {
        return new SupportPoint(this.collider1.getFurthestVertex(direction), this.collider2.getFurthestVertex(direction.negate(null)));
    }

    private boolean addSupport(Vector3f direction)
    {
        SupportPoint support = getSupport(direction);

        this.simplex.push(support);

        return Vector3f.dot(direction, support.get()) >= 0.0F;
    }

    private boolean update()
    {
        iterations++;
        if (simplex.getSize() == 0) // Simplex is empty.
        {
            this.direction = Vector3f.sub(collider2.getTransformation().getTranslation(), collider1.getTransformation().getTranslation(), null);
        } else if (simplex.getSize() == 1) // Simplex is 0-dimensional. It consists of one point.
        {
            this.direction = simplex.getPoints()[0].get().negate(null);
        } else if (simplex.getSize() == 2) // Simplex is 1-dimensional. It consists of a line.
        {
            Vector3f ab = Vector3f.sub(simplex.getPoints()[1].get(), simplex.getPoints()[0].get(), null);
            Vector3f originDir = simplex.getPoints()[0].get().negate(null);

            this.direction = Vector3f.cross(Vector3f.cross(ab, originDir, null), ab, null);
        } else if (simplex.getSize() == 3) // Simplex is 2-dimensional. It consists of a triangle.
        {
            Vector3f ac = Vector3f.sub(simplex.getPoints()[2].get(), simplex.getPoints()[0].get(), null);
            Vector3f ab = Vector3f.sub(simplex.getPoints()[1].get(), simplex.getPoints()[0].get(), null);
            Vector3f originDir = simplex.getPoints()[0].get().negate(null);

            if (Vector3f.dot(direction, originDir) < 0.0F)
            {
                direction = Vector3f.cross(ab, ac, null);
            } else
            {
                direction = Vector3f.cross(ac, ab, null);
            }
        } else if (simplex.getSize() == 4) // Simplex is 3-dimensional. It consists of a tetrahedron.
        {
            Vector3f da = Vector3f.sub(simplex.getPoints()[0].get(), simplex.getPoints()[1].get(), null);
            Vector3f db = Vector3f.sub(simplex.getPoints()[0].get(), simplex.getPoints()[2].get(), null);
            Vector3f dc = Vector3f.sub(simplex.getPoints()[0].get(), simplex.getPoints()[3].get(), null);
            Vector3f originDir = simplex.getPoints()[0].get().negate(null);

            Vector3f normal;

            if (Vector3f.dot(normal = Vector3f.cross(da, db, null), originDir) > 0.0F) // triangle ABD
            {
                simplex.set(simplex.getPoints()[0], simplex.getPoints()[1], simplex.getPoints()[3]);
                direction = normal;
            } else if (Vector3f.dot(normal = Vector3f.cross(db, dc, null), originDir) > 0.0F) // PolyTriangle BCD
            {
                simplex.set(simplex.getPoints()[1], simplex.getPoints()[2], simplex.getPoints()[3]);
                direction = normal;
            } else if (Vector3f.dot(normal = Vector3f.cross(dc, da, null), originDir) > 0.0F) // PolyTriangle CAD
            {
                simplex.set(simplex.getPoints()[0], simplex.getPoints()[2], simplex.getPoints()[3]);
                direction = normal;
            } else
            {
                this.didCollide = true; // The origin is completely enclosed within this simplex.
                return false; // Do not continue updating
            }
        } else
        {
            throw new IllegalStateException("Simplex has more than 4 vertices. Either it is not a simplex, or it exists in a higher dimension. Either way, collision with this is not supported.");
        }

        return this.addSupport(direction); // Continue updating if the support point passed the origin.
    }

    public static GJK checkCollision(Collider collider1, Collider collider2)
    {
        GJK gjk = new GJK(collider1, collider2);

        while (gjk.iterations < 100)
        {
            if (!gjk.update())
            {
                break;
            }
        }

        return gjk;
    }
}
