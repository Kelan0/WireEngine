package wireengine.core.level;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.physics.PhysicsObject;
import wireengine.core.physics.collision.ColliderMesh;
import wireengine.core.physics.collision.CollisionResult;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.geometry.Mesh;
import wireengine.core.rendering.geometry.MeshHelper;
import wireengine.core.rendering.geometry.Model;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.window.InputHandler;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public class Level
{
    private ColliderMesh collider;
    private Model staticScene;
    private List<Model> dynamicScene;
    private boolean renderHitbox;
    public Model testModel;

    public Level()
    {

    }

    public void init()
    {
        this.dynamicScene = new ArrayList<>();
//
        this.testModel = new Model(MeshHelper.createCube(1.0F, 1.5F, 1.0F).subdivideFaces(3));
        this.addDynamicMesh(testModel);

//        Mesh mesh = MeshHelper.createPlane(15.0F, 15.0F, 15, 15, Axis.getyAxisOnly());
//        Mesh mesh = MeshHelper.createUVSphere(1.0F, 20, 20);
//        this.staticScene = new Model(mesh, new Transformation());
//
//        this.colliders = Colliders.getMesh(mesh);

//        try
//        {
//            Mesh mesh = MeshHelper.parseObj("res/level/testlevel/testlevel.obj");
////            Mesh sceneMesh = MeshHelper.createPlane(10.0F, 10.0F, 10, 10, Axis.getWorldAxis());
//
//            this.staticScene = new Model(mesh, new Transformation());
//
//            List<Triangle> triangles = new ArrayList<>();
//            for (Mesh.Face3 face : mesh.getFaceList())
//            {
//                Vector3f p1 = face.getV1().getPosition();
//                Vector3f p2 = face.getV2().getPosition();
//                Vector3f p3 = face.getV3().getPosition();
//
//                Triangle triangle = new Triangle(p1, p2, p3);
//                triangles.add(triangle);
//            }
//
//            this.collider = new ColliderMesh(mesh, new Transformation());
////            WireEngine.engine().getPhysicsEngine().addPhysicsObject(this.physicsObject);
//
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }

    public void cleanup()
    {

    }

    public List<CollisionResult> getCollision(PhysicsObject object, double delta)
    {
        return new ArrayList<>(); //CollisionHandler.getCollision(object, this.colliders, delta);
    }

    public void render(ShaderProgram shaderProgram, double delta, double time)
    {
        this.renderFloorGrid(shaderProgram);

        if (this.staticScene != null)
        {
            this.staticScene.render(shaderProgram);
        }

        if (this.dynamicScene != null)
        {
            for (Model model : this.dynamicScene)
            {
                if (model == null)
                {
                    continue;
                }

                model.render(shaderProgram);
            }
        }

        if (InputHandler.keyPressed(GLFW.GLFW_KEY_F2))
        {
            renderHitbox = !renderHitbox;
        }

        if (renderHitbox)
        {
            this.renderHitboxes(shaderProgram);
        }

    }

    private void renderHitboxes(ShaderProgram shaderProgram)
    {
        if (collider != null && collider.getNumTriangles() > 0)
        {
            glEnable(GL_LINE_SMOOTH);
            glDisable(GL_DEPTH_TEST);
            DebugRenderer.getInstance().begin(GL_LINES);
            for (Triangle tri : this.collider.getTriangles())
            {
                Vector3f p1 = tri.getCentre();
                Vector3f p2 = Vector3f.add(new Vector3f(tri.getNormal()), p1, null);

                DebugRenderer.getInstance().addColour(new Vector4f(1.0F, 0.0F, 0.0F, 1.0F));
                DebugRenderer.getInstance().addVertex(p1);
                DebugRenderer.getInstance().addVertex(p2);

                DebugRenderer.getInstance().addColour(new Vector4f(0.0F, 1.0F, 0.0F, 1.0F));
                DebugRenderer.getInstance().addVertex(tri.getP1());
                DebugRenderer.getInstance().addVertex(tri.getP2());
                DebugRenderer.getInstance().addVertex(tri.getP2());
                DebugRenderer.getInstance().addVertex(tri.getP3());
                DebugRenderer.getInstance().addVertex(tri.getP3());
                DebugRenderer.getInstance().addVertex(tri.getP1());
            }
            DebugRenderer.getInstance().end(shaderProgram);
            glEnable(GL_DEPTH_TEST);
        }
    }

    private void renderFloorGrid(ShaderProgram shaderProgram)
    {
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        int radius = 10;
        int sub = 3; //The number of times to subdivide.

        DebugRenderer.getInstance().begin(GL_LINES);

        for (int a = 1; a <= sub; a++)
        {
            int power = 1 << (a - 1);
            float incr = 1.0F / power; // 2^(-a)
            int div = (int) (radius / incr);

            DebugRenderer.getInstance().addColour(new Vector4f(incr, incr, incr, 1.0F));
            for (int i = -div; i <= div; i++)
            {
                DebugRenderer.getInstance().addVertex(new Vector3f(i * incr, 0.0F, -radius));
                DebugRenderer.getInstance().addVertex(new Vector3f(i * incr, 0.0F, +radius));
                DebugRenderer.getInstance().addVertex(new Vector3f(-radius, 0.0F, i * incr));
                DebugRenderer.getInstance().addVertex(new Vector3f(+radius, 0.0F, i * incr));
            }
        }
        DebugRenderer.getInstance().end(shaderProgram);
    }

    public void addStaticMesh(Model model)
    {
        if (model != null)
        {
            addStaticMesh(model.getMesh().transform(model.getTransformation().getMatrix(null)));
        }
    }

    public void addStaticMesh(Mesh mesh)
    {
        if (mesh != null)
        {
            this.staticScene.getMesh().addMesh(mesh);
        }
    }

    public void addDynamicMesh(Model model)
    {
        if (model != null)
        {
            this.dynamicScene.add(model);
        }
    }

    public Model getSceneMesh()
    {
        return staticScene;
    }
}
