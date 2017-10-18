package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.WireEngine;
import wireengine.core.util.FileUtils;
import wireengine.core.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class MeshHelper
{
    public static final String COMMENT_LINE = "# ";
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
    public static final String MTL_TEXTURE_BLEND_U = "-blendu ";
    public static final String MTL_TEXTURE_BLEND_V = "-blendv ";
    public static final String MTL_TEXTURE_COLOUR_CORRECTION = "-cc ";
    public static final String MTL_TEXTURE_CLAMP_UV = "-clamp ";
    public static final String MTL_TEXTURE_CHANNEL = "-imfchan ";
    public static final String MTL_TEXTURE_BRIGHTNESS_CONTRAST = "-mm ";
    public static final String MTL_TEXTURE_OFFSET = "-o ";
    public static final String MTL_TEXTURE_SCALE = "-s ";
    public static final String MTL_TEXTURE_TURBULENCE = "-t ";
    public static final String MTL_TEXTURE_RESOLUTION = "-texres ";

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

    public static Mesh createTorus(float innerRadius, float outerRadius, float xDivisions, float yDivisions)
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
        WireEngine.getLogger().info("Loading OBJ filePath " + file);
        StringBuilder sb = new StringBuilder();
        if (FileUtils.readFile(file, sb) && sb.length() > 0)
        {
            File f = new File(file);
            String[] fileSource = sb.toString().split("\n");

            List<Vector3f> geometrics = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();
            List<Vector2f> textures = new ArrayList<>();
            List<Mesh.Face3> faces = new ArrayList<>();
            List<Material> materials = new ArrayList<>();
            Material currentMaterial = null;

            String INVALID_VALUE = "\0INVALID_LINE\0";

            for (String line : fileSource)
            {
                if (line == null || line.isEmpty() || line.startsWith(COMMENT_LINE))
                {
                    if (line == null || line.isEmpty())
                    {
                        WireEngine.getLogger().warning("Invalid line in OBJ filePath, skipping");
                    } else if (line.startsWith(COMMENT_LINE))
                    {
                        WireEngine.getLogger().info(line.substring(COMMENT_LINE.length())); // Print comments for debugging.
                    }
                } else if (line.startsWith(OBJ_VERTEX_GEOMETRIC))
                {
                    line = line.substring(OBJ_VERTEX_GEOMETRIC.length());
                    geometrics.add(readVector3f(line, new Vector3f(0.0F, 0.0F, 0.0F)));
                } else if (line.startsWith(OBJ_VERTEX_NORMAL))
                {
                    line = line.substring(OBJ_VERTEX_NORMAL.length());
                    normals.add(readVector3f(line, new Vector3f(0.0F, 0.0F, 0.0F)));
                } else if (line.startsWith(OBJ_VERTEX_TEXTURE))
                {
                    line = line.substring(OBJ_VERTEX_TEXTURE.length());
                    textures.add(readVector2f(line, new Vector2f(0.0F, 0.0F)));
                } else if (line.startsWith(OBJ_FACE_INDEX))
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

                            Mesh.Face3 face = new Mesh.Face3(v1, v2, v3);

                            if (currentMaterial != null)
                            {
                                face.material = currentMaterial;
                            }

                            faces.add(face);
                        }
                    } else
                    {
                        throw new IllegalStateException("OBJ filePath " + file + " is not triangulated. Onnly triangulated OBJs are supported");
                    }
                } else if (line.startsWith(OBJ_MATERIAL_LIB))
                {
                    line = line.substring(OBJ_MATERIAL_LIB.length());
                    String mtlStr = readString(line, INVALID_VALUE); // default value padded with null characters incase someone decides to name an MTL filePath "INVALID_MTL" for some reason.

                    if (!mtlStr.endsWith(INVALID_VALUE) && mtlStr.endsWith(".mtl"))
                    {
                        materials.addAll(parseMtl(f.getParentFile() + File.separator + mtlStr));
                    } else
                    {
                        WireEngine.getLogger().warning("Line \"" + line + "\" specifying an MTL filePath was not valid");
                    }
                } else if (line.startsWith(OBJ_MATERIAL_USE))
                {
                    line = line.substring(OBJ_MATERIAL_USE.length());
                    String name = readString(line, INVALID_VALUE);
                    for (Material material : materials)
                    {
                        if (material == null)
                        {
                            materials.remove(null);
                        } else if (material.name.equals(name))
                        {
                            currentMaterial = material;
                            break;
                        }
                    }
                }
            }

            Mesh mesh = Mesh.create();
            mesh.addFaces(faces.toArray(new Mesh.Face3[faces.size()]));
            WireEngine.getLogger().info("Successfully loaded and compiled OBJ file");

            return mesh.compile();
        } else
        {
            WireEngine.getLogger().warning("Failed to load OBJ filePath " + file);
//        throw new UnexpectedException("An unexpected error occurred while loading the OBJ filePath " + filePath);

            return null;
        }
    }

    public static List<Material> parseMtl(String file) throws IOException
    {
        WireEngine.getLogger().info("Loading MTL filePath " + file);
        List<Material> materials = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        if (FileUtils.readFile(file, sb) && sb.length() > 0)
        {
            File f = new File(file);
            String[] fileSource = sb.toString().split("\n");
            Material currentMaterial = null;

            for (String line : fileSource)
            {
                // TODO: filter comment lines.
                if (line.startsWith(MTL_DEFINITION))
                {
                    line = line.substring(MTL_DEFINITION.length());
                    if (currentMaterial != null)
                    {
                        materials.add(currentMaterial);
                    }

                    currentMaterial = new Material(line.split(" ")[0]);
                }

                if (currentMaterial != null)
                {
                    if (line.startsWith(MTL_AMBIENT_COLOUR))
                    {
                        line = line.substring(MTL_AMBIENT_COLOUR.length());
                        currentMaterial.ambientColour = readVector3f(line, currentMaterial.ambientColour);
                    } else if (line.startsWith(MTL_DEFUSE_OLOUR))
                    {
                        line = line.substring(MTL_DEFUSE_OLOUR.length());
                        currentMaterial.diffuseColour = readVector3f(line, currentMaterial.diffuseColour);
                    } else if (line.startsWith(MTL_SPECULAR_COLOUR))
                    {
                        line = line.substring(MTL_SPECULAR_COLOUR.length());
                        Vector3f specularColour = readVector3f(line, new Vector3f(currentMaterial.specularColour));

                        currentMaterial.specularColour = new Vector4f(specularColour.x, specularColour.y, specularColour.z, 1.0F);
                    } else if (line.startsWith(MTL_SPECULAR_COEFFICIENT))
                    {
                        line = line.substring(MTL_SPECULAR_COEFFICIENT.length());
                        currentMaterial.specularColour.w = readFloat(line, currentMaterial.specularColour.w);
                    } else if (line.startsWith(MTL_AMBIENT_TEXTURE))
                    {
                        line = line.substring(MTL_AMBIENT_TEXTURE.length());
                        Texture texture = readTextureMap(line);
                        texture.setFilePath(f.getParentFile() + File.separator + texture.filePath);
                        currentMaterial.ambientTexture = texture.loadTexture();
                    } else if (line.startsWith(MTL_DEFUSE_TEXTURE))
                    {
                        Texture texture = readTextureMap(line);
                        texture.setFilePath(f.getParentFile() + File.separator + texture.filePath);
                        currentMaterial.diffuseTexture = texture.loadTexture();
                    } else if (line.startsWith(MTL_SPECULAR_TEXTURE))
                    {
                        Texture texture = readTextureMap(line);
                        texture.setFilePath(f.getParentFile() + File.separator + texture.filePath);
                        currentMaterial.specularTexture = texture.loadTexture();
                    }
                }
            }

            if (currentMaterial != null)
            {
                materials.add(currentMaterial);
            }
        } else
        {
            WireEngine.getLogger().warning("Failed to load MTL filePath " + file);
        }

        return materials;
    }

    private static Texture readTextureMap(String line)
    {
        Texture loader = new Texture();

        /*
        Sources that this code is based on. Most of this is translated from C++ code.

        https://github.com/syoyo/tinyobjloader/blob/master/tiny_obj_loader.h
        http://paulbourke.net/dataformats/mtl/
        https://github.com/jaredloomis/YFNH-LWJGL/blob/master/3DYFNH/src/net/future/model/OBJLoader.java
         */

        String[] data = line.split("(?=-)");
        int i = 0;
        while (i < data.length)
        {
            String temp = data[i++];
            if (temp.startsWith(MTL_TEXTURE_BLEND_U))
            {
                loader.blendu = readBoolean(temp.substring(MTL_TEXTURE_BLEND_U.length()), loader.blendu);
            } else if (temp.startsWith(MTL_TEXTURE_BLEND_V))
            {
                loader.blendv = readBoolean(temp.substring(MTL_TEXTURE_BLEND_V.length()), loader.blendv);
            } else if (temp.startsWith(MTL_TEXTURE_COLOUR_CORRECTION))
            {
                loader.colourCorrection = readBoolean(temp.substring(MTL_TEXTURE_COLOUR_CORRECTION.length()), loader.colourCorrection);
            } else if (temp.startsWith(MTL_TEXTURE_CLAMP_UV))
            {
                loader.clampUV = readBoolean(temp.substring(MTL_TEXTURE_CLAMP_UV.length()), loader.clampUV);
            } else if (temp.startsWith(MTL_TEXTURE_CHANNEL))
            {
                loader.channel = temp.substring(MTL_TEXTURE_CHANNEL.length());
            } else if (temp.startsWith(MTL_TEXTURE_BRIGHTNESS_CONTRAST))
            {
                Vector2f mm = readVector2f(temp.substring(MTL_TEXTURE_BRIGHTNESS_CONTRAST.length()), new Vector2f(loader.brightness, loader.contrast));
                loader.brightness = mm.x;
                loader.contrast = mm.y;
            } else if (temp.startsWith(MTL_TEXTURE_OFFSET))
            {
                loader.offset = readVector3f(temp.substring(MTL_TEXTURE_OFFSET.length()), new Vector3f(0.0F, 0.0F, 0.0F));
            } else if (temp.startsWith(MTL_TEXTURE_SCALE))
            {
                loader.scale = readVector3f(temp.substring(MTL_TEXTURE_SCALE.length()), new Vector3f(1.0F, 1.0F, 1.0F));
            } else if (temp.startsWith(MTL_TEXTURE_TURBULENCE))
            {
                loader.turbulence = readVector3f(temp.substring(MTL_TEXTURE_TURBULENCE.length()), new Vector3f(0.0F, 0.0F, 0.0F));
            } else if (temp.startsWith(MTL_TEXTURE_RESOLUTION))
            {
                loader.resolution = readFloat(line.substring(MTL_TEXTURE_RESOLUTION.length()), loader.resolution);
            }
        }

        String[] values = line.split(" ");
        loader.filePath = values[values.length - 1]; //Assuming filePath is always at the end of the line, and it contains no spaces.

        return loader;
    }

    private static int readIndices(String line, int[] indices)
    {
        if (line == null || line.isEmpty() || indices == null || indices.length < 3)
        {
            return 0;
        }

        int maxIndex; //1 guarantees a vertex position. 2 guarantees a position and a normal. 3 guarantees a position, textureLoader and normal
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
            indices[pointer++] = Integer.valueOf(vertex1[i]) - 1;
            indices[pointer++] = Integer.valueOf(vertex2[i]) - 1;
            indices[pointer++] = Integer.valueOf(vertex3[i]) - 1;
        }

        return maxIndex;
    }

    private static boolean readBoolean(String str, boolean defaultValue)
    {
        if (str != null && str.length() > 0)
        {
            str = str.toLowerCase();

            if (StringUtils.stringCompare("true", str, 4) == 0)
            {
                return true;
            }
            if (StringUtils.stringCompare("yes", str, 3) == 0)
            {
                return true;
            }
            if (StringUtils.stringCompare("on", str, 2) == 0)
            {
                return true;
            }

            if (StringUtils.stringCompare("false", str, 5) == 0)
            {
                return false;
            }
            if (StringUtils.stringCompare("no", str, 2) == 0)
            {
                return false;
            }
            if (StringUtils.stringCompare("off", str, 3) == 0)
            {
                return false;
            }
        }

        return defaultValue;
    }

    private static float readFloat(String line, float defaultValue)
    {
        String[] data = line.split(" ");

        return StringUtils.isFloat(data[0]) ? Float.parseFloat(data[0]) : defaultValue;
    }

    private static Vector2f readVector2f(String line, Vector2f defaultValue)
    {
        String[] data = line.split(" ");
        float x = StringUtils.isFloat(data[0]) ? Float.parseFloat(data[0]) : defaultValue.x;
        float y = StringUtils.isFloat(data[1]) ? Float.parseFloat(data[1]) : defaultValue.y;

        return new Vector2f(x, y);
    }

    private static Vector3f readVector3f(String line, Vector3f defaultValue)
    {
        String[] data = line.split(" ");
        float x = StringUtils.isFloat(data[0]) ? Float.parseFloat(data[0]) : defaultValue.x;
        float y = StringUtils.isFloat(data[1]) ? Float.parseFloat(data[1]) : defaultValue.y;
        float z = StringUtils.isFloat(data[2]) ? Float.parseFloat(data[2]) : defaultValue.z;

        return new Vector3f(x, y, z);
    }

    private static String readString(String line, String defaultValue)
    {
        String[] data = line.split(" ");

        return data[0] == null || data[0].isEmpty() ? defaultValue : data[0];
    }
}
