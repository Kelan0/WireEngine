package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.WireEngine;
import wireengine.core.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class MeshHelper
{
    public static final String OBJ_VERTEX_GEOMETRIC = "v ";
    public static final String OBJ_VERTEX_NORMAL = "vn ";
    public static final String OBJ_VERTEX_TEXTURE = "vt ";
    public static final String OBJ_FACE_INDEX = "f ";
    public static final String OBJ_MATERIAL_LIB = "mtllib ";
    public static final String OBJ_MATERIAL_USE = "usemtl ";
    public static final String MTL_DEFINITION = "newmtl ";
    public static final String MTL_AMBIENT_COLOUR = "Ka ";
    public static final String MTL_DEFUSE_OLOUR = "Kd ";
    public static final String MTL_SPECULAR_COLOUR = "Ks ";
    public static final String MTL_SPECULAR_COEFFICIENT = "Ns ";
    public static final String MTL_AMBIENT_TEXTURE = "map_Ka ";
    public static final String MTL_DEFUSE_TEXTURE = "map_Kd ";
    public static final String MTL_SPECULAR_TEXTURE = "map_Ks ";

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

        for (pointer = 0; pointer < indices.length; )
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

    public static Mesh parseObj(String file) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        if (FileUtils.readFile(file, sb) && sb.length() > 0)
        {
            String[] fileSource = sb.toString().split("\n");

            List<Vector3f> geometrics = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();
            List<Vector2f> textures = new ArrayList<>();

            for (String line : fileSource)
            {
                if (line == null)
                {
                    continue;
                }

                if (line.startsWith(OBJ_VERTEX_GEOMETRIC))
                {
                    line = line.substring(OBJ_VERTEX_GEOMETRIC.length());
                    geometrics.add(readVector3f(line));
                    continue;
                }

                if (line.startsWith(OBJ_VERTEX_NORMAL))
                {
                    line = line.substring(OBJ_VERTEX_NORMAL.length());
                    normals.add(readVector3f(line));
                    continue;
                }

                if (line.startsWith(OBJ_VERTEX_TEXTURE))
                {
                    line = line.substring(OBJ_VERTEX_TEXTURE.length());
                    textures.add(readVector2f(line));
                    continue;
                }

                if (line.startsWith(OBJ_FACE_INDEX))
                {
                    line = line.substring(OBJ_FACE_INDEX.length());

                    String[] data = line.split(" ");

                    if (data.length == 3) //Only triangulated faces allowed
                    {
                        int[] indices = new int[9];
                        int i = readIndices(line, indices);

                        if (i == 0)
                        {
                            WireEngine.getLogger().warning("Failed to load face indices from OBJ. This may cause errors.");
                            continue;
                        }

                        if (i >= 1)
                        {
                            Mesh.Vertex v1 = new Mesh.Vertex(geometrics.get(indices[0]), new Vector3f(), new Vector2f());
                            Mesh.Vertex v2 = new Mesh.Vertex(geometrics.get(indices[1]), new Vector3f(), new Vector2f());
                            Mesh.Vertex v3 = new Mesh.Vertex(geometrics.get(indices[2]), new Vector3f(), new Vector2f());

                            if (i >= 2)
                            {
                                if (i >= 3)
                                {
                                    v1.texture = textures.get(indices[3]);
                                    v2.texture = textures.get(indices[4]);
                                    v3.texture = textures.get(indices[5]);
                                    v1.normal = normals.get(indices[6]);
                                    v2.normal = normals.get(indices[7]);
                                    v3.normal = normals.get(indices[8]);
                                } else
                                {
                                    v1.normal = normals.get(indices[3]);
                                    v2.normal = normals.get(indices[4]);
                                    v3.normal = normals.get(indices[5]);
                                }
                            }
                        }
                    } else
                    {
                        throw new IllegalStateException("OBJ file " + file + " is not triangulated. Onnly triangulated OBJs are supported");
                    }

                    continue;
                }
            }

            Mesh mesh = Mesh.create();


            return mesh.compile();
        }

//        throw new UnexpectedException("An unexpected error occurred while loading the OBJ file " + file);

        return null;
    }

    public static List<Material> parseMtl(String file) throws IOException
    {
        List<Material> materials = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        if (FileUtils.readFile(file, sb) && sb.length() > 0)
        {
            File f = new File(file);
            String[] fileSource = sb.toString().split("\n");
            Material currentMaterial = null;

            for (String line : fileSource)
            {
                if (line.startsWith(MTL_DEFINITION))
                {
                    line = line.substring(MTL_DEFINITION.length());
                    if (currentMaterial != null)
                    {
                        materials.add(currentMaterial);
                    }

                    currentMaterial = new Material(line.split(" ")[0]);
                    continue;
                }

                if (currentMaterial != null)
                {
                    if (line.startsWith(MTL_AMBIENT_COLOUR))
                    {
                        line = line.substring(MTL_AMBIENT_COLOUR.length());
                        currentMaterial.ambient = readVector3f(line);
                        continue;
                    }

                    if (line.startsWith(MTL_DEFUSE_OLOUR))
                    {
                        line = line.substring(MTL_DEFUSE_OLOUR.length());
                        currentMaterial.diffuse = readVector3f(line);
                        continue;
                    }
                    if (line.startsWith(MTL_SPECULAR_COLOUR))
                    {
                        line = line.substring(MTL_SPECULAR_COLOUR.length());
                        currentMaterial.specular = readVector3f(line);
                        continue;
                    }

                    if (line.startsWith(MTL_DEFUSE_TEXTURE))
                    {
                        line = line.substring(MTL_DEFUSE_TEXTURE.length());

                        String temp = line;

                        int pointer = 0;

                        /*
                        https://github.com/syoyo/tinyobjloader/blob/master/tiny_obj_loader.h
                        http://paulbourke.net/dataformats/mtl/
                        https://github.com/jaredloomis/YFNH-LWJGL/blob/master/3DYFNH/src/net/future/model/OBJLoader.java
                         */
                        if (temp.startsWith("-blendu "))
                        {
                            temp = temp.substring("-blendu ".length());
                            boolean blendu = readBoolean(temp, true);
                        } else if (temp.startsWith("-blendv "))
                        {
                            temp = temp.substring("-blendv ".length());
                            boolean blendv = readBoolean(temp, true);
                        } else if (temp.startsWith("-blendu "))
                        {
                            temp = temp.substring("-blendu ".length());
                            boolean blendu = readBoolean(temp, true);
                        }

                        String filename = line.split(" ")[0];
                        float wrapx = 0.0F;
                        float wrapy = 0.0F;

                        currentMaterial.texture = Texture.loadTexture(f.getParentFile() + File.separator + filename).setTextureWrap(wrapx, wrapy);
                        continue;
                    }

                    //TODO add support for specular coefficiants
                    if (line.startsWith(MTL_SPECULAR_COEFFICIENT))
                    {

                        continue;
                    }

                    //TODO add support for ambient texture map
                    if (line.startsWith(MTL_AMBIENT_TEXTURE))
                    {

                        continue;
                    }

                    //TODO add support for specular texture map
                    if (line.startsWith(MTL_SPECULAR_TEXTURE))
                    {

                        continue;
                    }
                }
            }
        }

        return materials;
    }

    private static boolean readBoolean(String str, boolean defaultVal)
    {
        if (str != null && str.length() > 0)
        {
            str = str.toLowerCase();
            if ("true".startsWith(str) || "on".startsWith(str) || "yes".startsWith(str))
            {
                return true;
            }

            if ("false".startsWith(str) || "off".startsWith(str) || "no".startsWith(str))
            {
                return false;
            }
        }

        return defaultVal;
    }

    private static Vector2f readVector2f(String line)
    {
        String[] data = line.split(" ");
        float x = Float.parseFloat(data[0]);
        float y = Float.parseFloat(data[1]);

        return new Vector2f(x, y);
    }

    private static Vector3f readVector3f(String line)
    {
        String[] data = line.split(" ");
        float x = Float.parseFloat(data[0]);
        float y = Float.parseFloat(data[1]);
        float z = Float.parseFloat(data[2]);

        return new Vector3f(x, y, z);
    }

    public static int readIndices(String line, int[] indices)
    {
        if (line == null || line.isEmpty() || indices == null || indices.length < 3)
        {
            return 0;
        }

        int maxIndex; //1 guarantees a vertex position. 2 guarantees a position and a normal. 3 guarantees a position, texture and normal
        if (line.contains("/")) //This vertex has normals and/or textures
        {
            if (line.contains("//")) //This vertex does not have textures
            {
                maxIndex = 2;
                line = line.replace("//", "/");
            } else
            {
                maxIndex = 3;
            }
        } else
        {
            maxIndex = 1;
        }

        if (indices.length < maxIndex * 3)
        {
            return 0;
        }

        String[] data = line.split(" ");

        String[] vertex1 = data[0].split("/");
        String[] vertex2 = data[1].split("/");
        String[] vertex3 = data[2].split("/");

        if (vertex1.length != vertex2.length || vertex1.length != vertex3.length)
        {
            return 0;
        }

        int pointer = 0;
        for (int i = 0; i < maxIndex; i++)
        {
            indices[pointer++] = Integer.valueOf(vertex1[i]);
            indices[pointer++] = Integer.valueOf(vertex2[i]);
            indices[pointer++] = Integer.valueOf(vertex3[i]);
        }

        return maxIndex;
    }
}
