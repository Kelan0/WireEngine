package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class MeshHelper
{
    public static Mesh createCube(float xSize, float ySize, float zSize)
    {
        xSize *= 0.5;
        ySize *= 0.5;
        zSize *= 0.5;

        Mesh.Vertex v0 = new Mesh.Vertex(new Vector3f(-xSize, -ySize, -zSize), new Vector3f(), new Vector2f());
        Mesh.Vertex v1 = new Mesh.Vertex(new Vector3f(-xSize, -ySize, +zSize), new Vector3f(), new Vector2f());
        Mesh.Vertex v2 = new Mesh.Vertex(new Vector3f(-xSize, +ySize, +zSize), new Vector3f(), new Vector2f());
        Mesh.Vertex v3 = new Mesh.Vertex(new Vector3f(-xSize, +ySize, -zSize), new Vector3f(), new Vector2f());
        Mesh.Vertex v4 = new Mesh.Vertex(new Vector3f(+xSize, -ySize, -zSize), new Vector3f(), new Vector2f());
        Mesh.Vertex v5 = new Mesh.Vertex(new Vector3f(+xSize, -ySize, +zSize), new Vector3f(), new Vector2f());
        Mesh.Vertex v6 = new Mesh.Vertex(new Vector3f(+xSize, +ySize, +zSize), new Vector3f(), new Vector2f());
        Mesh.Vertex v7 = new Mesh.Vertex(new Vector3f(+xSize, +ySize, -zSize), new Vector3f(), new Vector2f());

        Mesh.Face4 fNegX = new Mesh.Face4(v1, v2, v3, v0);
        Mesh.Face4 fPosX = new Mesh.Face4(v5, v4, v7, v6);
        Mesh.Face4 fNegY = new Mesh.Face4(v1, v5, v4, v0);
        Mesh.Face4 fPosY = new Mesh.Face4(v2, v6, v7, v3);
        Mesh.Face4 fNegZ = new Mesh.Face4(v7, v4, v0, v3);
        Mesh.Face4 fPosZ = new Mesh.Face4(v1, v5, v6, v2);

        return Mesh.create().addFace(fNegX).addFace(fNegY).addFace(fNegZ).addFace(fPosX).addFace(fPosY).addFace(fPosZ).compile();
    }

    public static Mesh createUVSphere(float radius, int xDivisions, int yDivisions)
    {
        final float S = 1.0F / (xDivisions - 1.0F);
        final float R = 1.0F / (yDivisions - 1.0F);

        Mesh.Vertex[] vertices = new Mesh.Vertex[yDivisions * xDivisions * 3];

        int pointer = 0;
        for (int i = 0; i < yDivisions; i++)
        {
            for (int j = 0; j < xDivisions; j++)
            {
                final float x = (float) (Math.cos(2.0F * Math.PI * j * S) * Math.sin(Math.PI * i * R));
                final float y = (float) Math.sin(-Math.PI / 2.0F + Math.PI * i * R);
                final float z = (float) (Math.sin(2.0F * Math.PI * j * S) * Math.sin(Math.PI * i * R));

                Vector3f p = new Vector3f(x * radius, y * radius, z * radius);
                Vector3f n = new Vector3f(x, y, z);
                Vector2f t = new Vector2f(j * S, i * R);

                vertices[pointer++] = new Mesh.Vertex(p, n, t);
            }
        }

        int[] indices = new int[yDivisions * xDivisions * 4];

        pointer = 0;
        for (int i = 0; i < yDivisions; i++)
        {
            for (int j = 0; j < xDivisions; j++)
            {
                indices[pointer++] = i * xDivisions + j;
                indices[pointer++] = i * xDivisions + (j + 1);
                indices[pointer++] = (i + 1) * xDivisions + (j + 1);
                indices[pointer++] = (i + 1) * xDivisions + j;
            }
        }

        Mesh mesh = Mesh.create();

        for (pointer = 0; pointer < indices.length;)
        {
            Mesh.Vertex v1 = vertices[indices[pointer++]];
            Mesh.Vertex v2 = vertices[indices[pointer++]];
            Mesh.Vertex v3 = vertices[indices[pointer++]];
            Mesh.Vertex v4 = vertices[indices[pointer++]];

            mesh.addFace(new Mesh.Face4(v1, v2, v3, v4));
        }

        return mesh.compile();
    }

    public static Mesh createCubeSphere(float radius, int divisions)
    {
        Mesh mesh = createCube(1.0F, 1.0F, 1.0F);

        for (int i = 0; i < divisions; i++)
        {
            mesh.subdivideFaces(0);
        }

        for (Mesh.Vertex vertex : mesh.vertexList)
        {
            vertex.position.normalise(vertex.position).scale(radius);
        }

        return mesh.compile();
    }
    public static Mesh createPlane(float width, float height, Vector3f normal)
    {
        Mesh mesh = Mesh.create();

        return mesh;
    }

    public static Mesh createCapsule(float radius, float distance, float xDivisions, float yDivisions)
    {
        Mesh mesh = Mesh.create();

        return mesh;
    }
}
