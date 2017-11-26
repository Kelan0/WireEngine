package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Matrix4f;
import wireengine.core.WireEngine;
import wireengine.core.rendering.IRenderable;
import wireengine.core.rendering.renderer.ShaderProgram;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public class Model implements IRenderable
{
    private MeshData mesh;
    private Transformation transformation;

    public Model(MeshData mesh, Transformation transformation)
    {
        this.mesh = mesh;

        if (transformation == null)
        {
            transformation = new Transformation();
        }

        this.transformation = transformation;
    }

    public Model(MeshData mesh)
    {
        this(mesh, new Transformation());
    }

    public MeshData getMesh()
    {
        return mesh;
    }

    public Transformation getTransformation()
    {
        return transformation;
    }

    public Model setTransformation(Transformation transformation)
    {
        if (transformation != null)
        {
            this.transformation = transformation;
        }

        return this;
    }

    @Override
    public void initRenderable()
    {
        if (this.mesh != null)
        {
            if (WireEngine.isRenderThread())
            {
                if (this.mesh.renderableMesh == null)
                {
                    this.mesh.compile();
                }
            }
        }
    }

    @Override
    public void render(double delta, ShaderProgram shaderProgram)
    {
        if (this.mesh != null && this.mesh.renderableMesh != null)
        {
            Matrix4f mat = this.transformation.getMatrix(new Matrix4f());
            shaderProgram.setUniformMatrix4f("modelMatrix", mat);
            shaderProgram.setUniformBoolean("useLight", true);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
            this.mesh.renderableMesh.draw(shaderProgram);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Model model = (Model) o;

        if (mesh != null ? !mesh.equals(model.mesh) : model.mesh != null) return false;
        return transformation != null ? transformation.equals(model.transformation) : model.transformation == null;
    }

    @Override
    public int hashCode()
    {
        int result = mesh != null ? mesh.hashCode() : 0;
        result = 31 * result + (transformation != null ? transformation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Model{" + "mesh=" + mesh + ", transformation=" + transformation + '}';
    }
}
