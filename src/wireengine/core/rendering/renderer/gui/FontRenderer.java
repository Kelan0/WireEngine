package wireengine.core.rendering.renderer.gui;

import com.sun.org.apache.regexp.internal.RE;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.WireEngine;
import wireengine.core.rendering.geometry.GLMesh;
import wireengine.core.rendering.geometry.MeshData;
import wireengine.core.rendering.renderer.AbstractRenderer;
import wireengine.core.rendering.renderer.gui.RenderableText;
import wireengine.core.rendering.renderer.gui.font.FontData;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;


/**
 * @author Kelan
 */
public class FontRenderer extends AbstractRenderer
{
    private Map<String, FontData> fontMap = new HashMap<>();
    private Map<FontData, List<RenderableText>> renderTextMap = new HashMap<>();
    private GLMesh quad;

    public FontRenderer(int priority, int width, int height)
    {
        super(priority, width, height);
    }

    public void drawText(String text, String font, int x, int y, int size, Vector4f colour)
    {

    }

    public FontData getFontData(String fontName)
    {
        FontData fontData = fontMap.get(fontName);

        if (fontData == null)
        {
            try
            {
                fontData = FontData.loadFont(fontName);
                fontMap.put(fontName, fontData);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return fontData;
    }

    private List<Vector2f> charScreenPos = new ArrayList<>();
    private List<Vector2f> charTexturePos = new ArrayList<>();
    private List<Vector2f> charTextureSize = new ArrayList<>();

    @Override
    public void init()
    {
        MeshData.Vertex v0 = new MeshData.Vertex(new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector2f(0.0F, 0.0F));
        MeshData.Vertex v1 = new MeshData.Vertex(new Vector3f(+1.0F, -1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector2f(1.0F, 0.0F));
        MeshData.Vertex v2 = new MeshData.Vertex(new Vector3f(+1.0F, +1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector2f(1.0F, 1.0F));
        MeshData.Vertex v3 = new MeshData.Vertex(new Vector3f(-1.0F, +1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector2f(0.0F, 1.0F));
        MeshData meshData = new MeshData().addFace(new MeshData.Face4(v0, v1, v2, v3)).withNormals(false).withColours(false).compile();
        this.quad = meshData.getRenderableMesh();
        this.quad.addInstancedAttribute(0, 2, 8, 0);
        this.quad.addInstancedAttribute(1, 2, 8, 2);
        this.quad.addInstancedAttribute(2, 2, 8, 4);

        try
        {
            this.shader.addShader(GL_VERTEX_SHADER, "res/shaders/font/vertex.glsl");
            this.shader.addShader(GL_FRAGMENT_SHADER, "res/shaders/font/fragment.glsl");
            this.shader.addAttribute(GLMesh.ATTRIBUTE_LOCATION_POSITION, "vertexPosition");
            this.shader.addAttribute(GLMesh.ATTRIBUTE_LOCATION_TEXTURE, "vertexTexture");

            this.shader.createProgram();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Random random = WireEngine.engine().getRandom();
        for (int i = 0; i < 5; i++)
        {
            charScreenPos.add(new Vector2f(random.nextFloat(), random.nextFloat()));
            charTexturePos.add(new Vector2f(random.nextFloat(), random.nextFloat()));
            charTextureSize.add(new Vector2f(random.nextFloat(), random.nextFloat()));
        }
    }

    @Override
    public void render(double delta, double time)
    {
//        for (FontData font : renderTextMap.keySet())
//        {
//            List<RenderableText> textList = renderTextMap.get(font);
//
//            for (RenderableText text : textList)
//            {
////                this.quad.draw();
//            }
//        }


//        FloatBuffer data = BufferUtils.createFloatBuffer(40);
//
//        for (int i = 0; i < 5; i++)
//        {
//            data.put(charScreenPos.get(i).x);
//            data.put(charScreenPos.get(i).y);
//            data.put(charTexturePos.get(i).x);
//            data.put(charTexturePos.get(i).y);
//            data.put(charTextureSize.get(i).x);
//            data.put(charTextureSize.get(i).y);
//        }
//
//        data.flip();
//
//        this.quad.setInstanceData(data);
//        this.quad.draw(this.shader);
    }

    @Override
    public void cleanup()
    {

    }

    @Override
    public void createProjection(boolean immediate)
    {

    }
}
