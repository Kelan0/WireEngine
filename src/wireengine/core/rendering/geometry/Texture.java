package wireengine.core.rendering.geometry;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.WireEngine;
import wireengine.core.rendering.ShaderProgram;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * @author Kelan
 */
public final class Texture
{
    protected boolean blendu = true;
    protected boolean blendv = true;
    protected boolean colourCorrection = false;
    protected boolean clampUV = false;
    protected float brightness = 1.0F;
    protected float contrast = 0.0F;
    protected float resolution = 1.0F;
    protected Vector3f offset = new Vector3f();
    protected Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);
    protected Vector3f turbulence = new Vector3f();
    protected String channel = "";
    protected String filePath = "";

    private boolean isLoaded = false;
    private int textureUnit = GL_TEXTURE0;
    private int textureTarget = GL_TEXTURE_2D;
    private int textureFormat = GL_RGBA;
    private int textureID;
    private int width;
    private int height;

    Texture()
    {

    }

    Texture loadTexture()
    {
        if (isLoaded)
        {
            throw new IllegalStateException("Texture is already loaded.");
        }

        File file = new File(this.filePath);

        WireEngine.getLogger().info("Loading texture " + this.filePath);

        if (file.exists())
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(file);
                PNGDecoder pngDecoder = new PNGDecoder(inputStream);
                this.width = pngDecoder.getWidth();
                this.height = pngDecoder.getHeight();
                int bytes = Integer.BYTES;

                ByteBuffer data = ByteBuffer.allocateDirect(bytes * this.width * this.height);
                pngDecoder.decode(data, this.width * bytes, PNGDecoder.Format.RGBA);
                data.flip();
                inputStream.close();

                this.textureID = glGenTextures();
                glActiveTexture(this.textureUnit);
                glBindTexture(this.textureTarget, this.textureID);
                glPixelStorei(GL_UNPACK_ALIGNMENT, bytes);

                glTexParameteri(this.textureTarget, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(this.textureTarget, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(this.textureTarget, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(this.textureTarget, GL_TEXTURE_WRAP_T, GL_REPEAT);
                glTexImage2D(this.textureTarget, 0, GL_RGBA, this.width, this.height, 0, this.textureFormat, GL_UNSIGNED_BYTE, data);

            } catch (IOException e)
            {
                e.printStackTrace();
            }

            this.isLoaded = true;
        } else
        {
            WireEngine.getLogger().warning("Texture does not exist");
        }
        return this;
    }

    public void bind(ShaderProgram shader)
    {
        glActiveTexture(this.textureUnit);
        glBindTexture(this.textureTarget, this.textureID);
        shader.setUniformVector3f("textureScale", this.scale);
        shader.setUniformBoolean("useTexture", true);
    }

    public void destroy()
    {

    }

    public boolean doBlendu()
    {
        return blendu;
    }

    public boolean doBlendv()
    {
        return blendv;
    }

    public boolean doColourCorrection()
    {
        return colourCorrection;
    }

    public boolean doClampUV()
    {
        return clampUV;
    }

    public float getBrightness()
    {
        return brightness;
    }

    public float getContrast()
    {
        return contrast;
    }

    public float getResolution()
    {
        return resolution;
    }

    public Vector3f getOffset()
    {
        return offset;
    }

    public Vector3f getScale()
    {
        return scale;
    }

    public Vector3f getTurbulence()
    {
        return turbulence;
    }

    public String getChannel()
    {
        return channel;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public boolean isLoaded()
    {
        return isLoaded;
    }

    public Texture setBlendu(boolean blendu)
    {
        this.blendu = blendu;
        return this;
    }

    public Texture setBlendv(boolean blendv)
    {
        this.blendv = blendv;
        return this;
    }

    public Texture setColourCorrection(boolean colourCorrection)
    {
        this.colourCorrection = colourCorrection;
        return this;
    }

    public Texture setClampUV(boolean clampUV)
    {
        this.clampUV = clampUV;
        return this;
    }

    public Texture setBrightness(float brightness)
    {
        this.brightness = brightness;
        return this;
    }

    public Texture setContrast(float contrast)
    {
        this.contrast = contrast;
        return this;
    }

    public Texture setResolution(float resolution)
    {
        this.resolution = resolution;
        return this;
    }

    public Texture setOffset(Vector3f offset)
    {
        this.offset = offset;
        return this;
    }

    public Texture setScale(Vector3f scale)
    {
        this.scale = scale;
        return this;
    }

    public Texture setTurbulence(Vector3f turbulence)
    {
        this.turbulence = turbulence;
        return this;
    }

    public Texture setChannel(String channel)
    {
        this.channel = channel;
        return this;
    }

    public Texture setFilePath(String filePath)
    {
        this.filePath = filePath;
        return this;
    }

    @Override
    public String toString()
    {
        return "Texture{" + "blendu=" + blendu + ", blendv=" + blendv + ", colourCorrection=" + colourCorrection + ", clampUV=" + clampUV + ", brightness=" + brightness + ", contrast=" + contrast + ", resolution=" + resolution + ", offset=" + offset + ", scale=" + scale + ", turbulence=" + turbulence + ", channel='" + channel + '\'' + ", filePath='" + filePath + '\'' + '}';
    }
}
