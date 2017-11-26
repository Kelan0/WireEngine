package wireengine.core.physics.collision.algorithm;

import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kelan
 */
public class Polytope
{
    public List<PolyTriangle> triangles = new ArrayList<>();
    public List<PolyEdge> edges = new ArrayList<>();

    public Polytope()
    {

    }

    public Polytope(Simplex simplex)
    {
        this.triangles.add(new PolyTriangle(simplex.getPoints()[0], simplex.getPoints()[1], simplex.getPoints()[2]));
        this.triangles.add(new PolyTriangle(simplex.getPoints()[0], simplex.getPoints()[2], simplex.getPoints()[3]));
        this.triangles.add(new PolyTriangle(simplex.getPoints()[0], simplex.getPoints()[3], simplex.getPoints()[1]));
        this.triangles.add(new PolyTriangle(simplex.getPoints()[1], simplex.getPoints()[3], simplex.getPoints()[2]));

    }

    public boolean addPoint(SupportPoint v0)
    {
        boolean didAdd = false;

        Iterator<PolyTriangle> iterator = this.triangles.iterator();

        while (iterator.hasNext())
        {
            PolyTriangle triangle = iterator.next();
            if (Vector3f.dot(triangle.normal, Vector3f.sub(v0.get(), triangle.vertices[0].get(), null)) > 0.0F)
            {
                this.updateEdges(triangle.vertices[0], triangle.vertices[1]);
                this.updateEdges(triangle.vertices[1], triangle.vertices[2]);
                this.updateEdges(triangle.vertices[2], triangle.vertices[0]);
                iterator.remove();
            }
        }

        for (PolyEdge edge : this.edges)
        {
            this.triangles.add(new PolyTriangle(v0, edge.vertices[0], edge.vertices[1]));
            didAdd = true;
        }

        this.edges.clear();

        return didAdd;
    }

    private void updateEdges(SupportPoint v0, SupportPoint v1)
    {
        Iterator<PolyEdge> iterator = this.edges.iterator();
        while (iterator.hasNext())
        {
            PolyEdge polyEdge = iterator.next();
            if (polyEdge.vertices[0] == v1 && polyEdge.vertices[1] == v0)
            {
                iterator.remove();
                return;
            }
        }

        this.edges.add(new PolyEdge(v0, v1));
    }

    @Override
    public String toString()
    {
        return "Polytope{" + "triangles=" + triangles + '}';
    }

    public static final class PolyTriangle
    {
        public SupportPoint[] vertices = new SupportPoint[3];
        public Vector3f normal;

        public PolyTriangle(SupportPoint v0, SupportPoint v1, SupportPoint v2)
        {
            this.vertices[0] = v0;
            this.vertices[1] = v1;
            this.vertices[2] = v2;
            Vector3f edge0 = Vector3f.sub(v1.get(), v0.get(), null);
            Vector3f edge1 = Vector3f.sub(v2.get(), v0.get(), null);
            this.normal = new Vector3f(Vector3f.cross(edge0, edge1, null).normalise(null));
        }

        public float distanceFromOrigin()
        {
            return Vector3f.dot(this.normal, this.vertices[0].get());
        }


        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PolyTriangle that = (PolyTriangle) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(vertices, that.vertices);
        }

        @Override
        public int hashCode()
        {
            int result = Arrays.hashCode(vertices);
            result = 31 * result + (normal != null ? normal.hashCode() : 0);
            return result;
        }
    }

    public static final class PolyEdge
    {
        public SupportPoint[] vertices = new SupportPoint[2];

        public PolyEdge(SupportPoint v0, SupportPoint v1)
        {
            this.vertices[0] = v0;
            this.vertices[1] = v1;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PolyEdge polyEdge = (PolyEdge) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(vertices, polyEdge.vertices);
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(vertices);
        }
    }
}
