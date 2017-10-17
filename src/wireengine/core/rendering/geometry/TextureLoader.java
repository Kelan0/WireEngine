package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author Kelan
 */
public class TextureLoader
{
    private Texture texture;

    public TextureLoader(String file)
    {
        this.texture = new Texture();
        this.texture.filePath = file;
    }

    public TextureLoader setBlendu(boolean blendu)
    {
        if (this.texture != null)
        {
            this.texture.blendu = blendu;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setBlendv(boolean blendv)
    {
        if (this.texture != null)
        {
            this.texture.blendv = blendv;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setColourCorrection(boolean colourCorrection)
    {
        if (this.texture != null)
        {
            this.texture.colourCorrection = colourCorrection;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setClampUV(boolean clampUV)
    {
        if (this.texture != null)
        {
            this.texture.clampUV = clampUV;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setBrightness(float brightness)
    {
        if (this.texture != null)
        {
            this.texture.brightness = brightness;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setContrast(float contrast)
    {
        if (this.texture != null)
        {
            this.texture.contrast = contrast;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setResolution(float resolution)
    {
        if (this.texture != null)
        {
            this.texture.resolution = resolution;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setOffset(Vector3f offset)
    {
        if (this.texture != null)
        {
            this.texture.offset = offset;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setScale(Vector3f scale)
    {
        if (this.texture != null)
        {
            this.texture.scale = scale;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setTurbulence(Vector3f turbulence)
    {
        if (this.texture != null)
        {
            this.texture.turbulence = turbulence;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setChannel(String channel)
    {
        if (this.texture != null)
        {
            this.texture.channel = channel;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public TextureLoader setFilePath(String filePath)
    {
        if (this.texture != null)
        {
            this.texture.filePath = filePath;
            return this;
        }
        throw new IllegalStateException("Null texture");
    }

    public Texture loadTexture()
    {
        if (this.texture != null)
        {
            Texture ret = this.texture.loadTexture();
            this.texture = null;
            return ret;
        }

        throw new IllegalStateException("Null texture");
    }
}
