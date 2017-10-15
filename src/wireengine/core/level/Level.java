package wireengine.core.level;

import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.geometry.Mesh;
import wireengine.core.rendering.geometry.Model;
import wireengine.core.rendering.geometry.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class Level
{
    public Model staticScene;
    public List<Model> dynamicScene;

    public Level()
    {

    }

    public void init()
    {
        this.dynamicScene = new ArrayList<>();
        this.staticScene = new Model(Mesh.create().compile(), new Transformation());
    }

    public void cleanup()
    {

    }

    public void physics(double delta)
    {

    }

    public void render(ShaderProgram shaderProgram, double delta, double time)
    {
        this.staticScene.render(shaderProgram);

        for (Model model : this.dynamicScene)
        {
            model.render(shaderProgram);
        }
    }

    public void addStaticMesh(Model model)
    {
        addStaticMesh(model.getMesh().transform(model.getTransformation().getMatrix(null)));
    }

    public void addStaticMesh(Mesh mesh)
    {
        this.staticScene.getMesh().addMesh(mesh);
    }
}
