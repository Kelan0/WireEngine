package wireengine.core.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

/**
 * @author Kelan
 */

public class FrameBuffer
{
    private int width;
    private int height;
    private int fboID;
    private int colourBuffer;
    private int depthBuffer;

    public FrameBuffer(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void init()
    {
//        this.fboID = glGenFramebuffers();
//        glBindFramebuffer(GL_FRAMEBUFFER, this.fboID);
//
//        this.colourBuffer = glGenTextures();
//        glBindTexture(GL_TEXTURE_2D, this.colourBuffer);
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//
//        this.depthBuffer = glGenRenderbuffers();
//        glBindRenderbuffer(GL_RENDERBUFFER, this.depthBuffer);
//        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, this.width, this.height);
//        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, this.depthBuffer);


        this.fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fboID);

        this.colourBuffer = generateTexture(width, height, GL_RGBA8, GL_RGBA, GL_UNSIGNED_BYTE);
        this.depthBuffer = generateTexture(width, height, GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT, GL_FLOAT);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, this.colourBuffer, 0);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, this.depthBuffer, 0);//optional

//        drawbuffer.push_back(GL_COLOR_ATTACHMENT0 + attachment_index_color_texture);    //add attachements

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        {
            throw new IllegalStateException("Failed to create frame buffer.");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private int generateTexture(int width, int height, int internalFormat, int format, int type)
    {
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, 0);
        return texture;
    }

    public void dispose()
    {
        glDeleteFramebuffers(fboID);
        glDeleteTextures(colourBuffer);
        glDeleteRenderbuffers(depthBuffer);
    }

    public void bind(boolean bind)
    {
        if (bind)
        {
            bindFrameBuffer(fboID, width, height);
        } else
        {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }
    }

    public int getColourBuffer()
    {
        return colourBuffer;
    }

    public int getDepthBuffer()
    {
        return depthBuffer;
    }

    private void bindFrameBuffer(int frameBuffer, int width, int height)
    {
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glViewport(0, 0, width, height);
    }
}
