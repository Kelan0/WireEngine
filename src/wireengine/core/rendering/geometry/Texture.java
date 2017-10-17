package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Vector2f;

/**
 * @author Kelan
 */
public class Texture
{
    private int width;
    private int height;
    private int id;
    private Vector2f textureWrap;

    private Texture()
    {

    }

    public Texture setTextureWrap(float wrapx, float wrapy)
    {
        if (this.textureWrap == null)
        {
            this.textureWrap = new Vector2f();
        }

        this.textureWrap.x = wrapx;
        this.textureWrap.y = wrapy;

        return this;
    }

    public static Texture loadTexture(String file)
    {
        return new Texture();
    }
}
