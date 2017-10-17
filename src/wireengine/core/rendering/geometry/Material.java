package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class Material
{
    protected String name;
    protected Vector3f ambient;
    protected Vector3f diffuse;
    protected Vector3f specular;
    protected Texture texture;

    public Material(String name)
    {
        this.name = name;
    }
}
