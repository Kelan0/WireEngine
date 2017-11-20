package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Matrix4f;
import wireengine.core.rendering.IRenderable;
import wireengine.core.rendering.ShaderProgram;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public class Model implements IRenderable
{
    private MeshBuilder meshBuilder;
    private GLMesh mesh;
    private Transformation transformation;

    public Model(GLMesh mesh, Transformation transformation)
    {
        this.mesh = mesh;

        if (transformation == null)
        {
            transformation = new Transformation();
        }

        this.transformation = transformation;
    }

    public Model(MeshBuilder meshBuilder, Transformation transformation)
    {
        this((GLMesh) null, transformation);
        this.meshBuilder = meshBuilder;
    }

    public Model(GLMesh mesh)
    {
        this(mesh, new Transformation());
    }

    public Model(MeshBuilder meshBuilder)
    {
        this(meshBuilder, new Transformation());
    }

    public GLMesh getMesh()
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
        if (this.mesh == null && this.meshBuilder != null)
        {
            this.mesh = this.meshBuilder.build();
        }
    }

    @Override
    public void render(double delta, ShaderProgram shaderProgram)
    {
        Matrix4f mat = this.transformation.getMatrix(new Matrix4f());
        shaderProgram.setUniformMatrix4f("modelMatrix", mat);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        this.mesh.draw(shaderProgram);
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
