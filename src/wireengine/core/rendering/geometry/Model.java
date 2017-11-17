package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import wireengine.core.rendering.ShaderProgram;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BACK;

/**
 * @author Kelan
 */
public class Model
{
    private Mesh mesh;
    private Transformation transformation;

    public Model(Mesh mesh, Transformation transformation)
    {
        this.mesh = mesh;

        if (transformation != null)
        {
            this.transformation = transformation;
        } else
        {
            this.transformation = new Transformation();
        }
    }

    public Model(Mesh mesh)
    {
        this(mesh, new Transformation());
    }

    public Mesh getMesh()
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

    public void render(ShaderProgram shader)
    {
        Matrix4f mat = this.transformation.getMatrix(new Matrix4f());
        shader.setUniformMatrix4f("modelMatrix", mat);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        this.mesh.draw(shader);
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
