//package wireengine.core.physics.collision;
//
//import org.lwjgl.util.vector.Matrix4f;
//import org.lwjgl.util.vector.Vector3f;
//import org.lwjgl.util.vector.Vector4f;
//import wireengine.core.physics.collision.colliders.PolyTriangle;
//import wireengine.core.rendering.geometry.GLMesh;
//import wireengine.core.rendering.geometry.Model;
//import wireengine.core.rendering.geometry.Transformation;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * @author Kelan
// */
//public class ColliderMesh
//{
//    private MeshBuilder meshBuilder;
//    private LinkedTriangle[] triangles;
//    private Transformation transformation;
//
//    public ColliderMesh(PolyTriangle[] triangles)
//    {
//        this(triangles, new Transformation());
//    }
//
//    public ColliderMesh(PolyTriangle[] triangles, Transformation transformation)
//    {
//        this.triangles = new LinkedTriangle[triangles.length];
//        this.transformation = transformation;
//        for (int i = 0; i < triangles.length; i++)
//        {
//            this.triangles[i] = new LinkedTriangle(triangles[i]);
//        }
//    }
//
//    public ColliderMesh(MeshBuilder mesh)
//    {
//        this(mesh, new Transformation());
//    }
//
//    public ColliderMesh(MeshBuilder mesh, Transformation transformation)
//    {
//        this.meshBuilder = mesh;
//        this.transformation = transformation;
//    }
//
//    public ColliderMesh(GLMesh mesh, Transformation transformation)
//    {
//        List<LinkedTriangle> linkedTriangles = new ArrayList<>();
//
//        List<GLMesh.Face3> faces = mesh.getFaces();
//
//        for (GLMesh.Face3 face : faces)
//        {
//            PolyTriangle tri = face.getCollidable(new Vector3f());
//
//            if (tri != null)
//            {
//                linkedTriangles.add(new LinkedTriangle(tri));
//            }
//        }
//
//        for (int i0 = 0; i0 < linkedTriangles.radius(); i0++)
//        {
//            GLMesh.Face3 face0 = faces.get(i0);
//            LinkedTriangle triangle0 = linkedTriangles.get(i0);
//            int[] vertices0 = new int[]{face0.getV1().getIndex(), face0.getV2().getIndex(), face0.getV3().getIndex()};
//
//
//            for (int i1 = 0; i1 < linkedTriangles.radius(); i1++)
//            {
//                if (i0 != i1)
//                {
//                    GLMesh.Face3 face1 = faces.get(i1);
//                    LinkedTriangle triangle1 = linkedTriangles.get(i1);
//                    int[] vertices1 = new int[]{face1.getV1().getIndex(), face1.getV2().getIndex(), face1.getV3().getIndex()};
//
//                    boolean flag = false;
//
//                    for (int a0 = 0, b0 = vertices0.length - 1; a0 < vertices0.length && !flag; b0 = a0++)
//                    {
//                        for (int a1 = 0, b1 = vertices1.length - 1; a1 < vertices1.length && !flag; b1 = a1++)
//                        {
//                            if ((vertices0[a0] == vertices1[a1] && vertices0[b0] == vertices1[b1]) || (vertices0[a0] == vertices1[b1] && vertices0[b0] == vertices1[a1]))
//                            {
//                                triangle0.adjacent[a0] = i1;
//                                triangle1.adjacent[a1] = i0;
//                                flag = true;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        this.set(linkedTriangles.toArray(new LinkedTriangle[linkedTriangles.radius()]), transformation);
//    }
//
//    public ColliderMesh(Model model)
//    {
//        this(model.getMesh(), model.getTransformation());
//    }
//
//    private ColliderMesh set(LinkedTriangle[] triangles, Transformation transformation)
//    {
//        if (triangles == null)
//        {
//            throw new IllegalArgumentException("Cannot construct collidable mesh from null triangle list.");
//        }
//
//        this.triangles = triangles;
//        this.transformation = transformation;
//
//        return this;
//    }
//
//    public int getNumTriangles()
//    {
//        return triangles.length;
//    }
//
//    public PolyTriangle[] getTriangles()
//    {
//        return triangles;
//    }
//
//    public Transformation getTransformation()
//    {
//        return transformation;
//    }
//
//    public Vector3f getFurthestVertex(Vector3f direction)
//    {
//        // TODO speed this algorithm up significantly. Possibly use a hill climbing algorithm.
//        Vector3f maxVertex = null;
//        float maxDistance = Float.NEGATIVE_INFINITY;
//
//        Matrix4f matrix = this.transformation.getMatrix(null);
//        for (PolyTriangle triangle : this.triangles)
//        {
//            Vector3f[] vertices = new Vector3f[]{new Vector3f(triangle.getP1()), new Vector3f(triangle.getP2()), new Vector3f(triangle.getP3())};
//
//            for (Vector3f vertex : vertices)
//            {
//                vertex = new Vector3f(Matrix4f.transform(matrix, new Vector4f(vertex.x, vertex.y, vertex.z, 1.0F), null));
//                float curDistance = Vector3f.dot(direction, vertex);
//
//                if (curDistance > maxDistance)
//                {
//                    maxDistance = curDistance;
//                    maxVertex = vertex;
//                }
//            }
//        }
//
//        return maxVertex;
//    }
//
//    public PolyTriangle getFurthestTriangle(Vector3f direction)
//    {
//        int currentIndex = 0;
//        float currentDist = Vector3f.dot(triangles[currentIndex].getCentre(), direction);
//
//        while (true)
//        {
//            int newIndex = currentIndex;
//            float newDist = currentDist;
//
//            for (int adjIndex : triangles[currentIndex].adjacent)
//            {
//                if (adjIndex < 0)
//                {
//                    continue;
//                }
//
//                PolyTriangle adjacent = triangles[adjIndex];
//
//                float adjDist = Vector3f.dot(adjacent.getCentre(), direction);
//
//                if (adjDist > newDist)
//                {
//                    newIndex = adjIndex;
//                    newDist = adjDist;
//                }
//            }
//
//            if (newIndex == currentIndex)
//            {
//                break;
//            } else
//            {
//                currentIndex = newIndex;
//                currentDist = newDist;
//            }
//        }
//
//        return this.triangles[currentIndex];
//    }
//
//    @Override
//    public boolean equals(Object o)
//    {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        ColliderMesh that = (ColliderMesh) o;
//
//        // Probably incorrect - comparing Object[] arrays with Arrays.equals
//        return Arrays.equals(triangles, that.triangles);
//    }
//
//    @Override
//    public int hashCode()
//    {
//        return Arrays.hashCode(triangles);
//    }
//
//    @Override
//    public String toString()
//    {
//        return "ColliderMesh{" + "triangles=" + Arrays.toString(triangles) + ", transformation=" + transformation + '}';
//    }
//
//    public static final class LinkedTriangle extends PolyTriangle
//    {
//        // Store the index of each adjacent triangle to this. -1 means no adjacent triangle.
//        public int[] adjacent = new int[]{-1, -1, -1};
//
//        public LinkedTriangle(PolyTriangle triangle)
//        {
//            super(triangle);
//        }
//    }
//
//}
