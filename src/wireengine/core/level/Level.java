package wireengine.core.level;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.entity.Entity;
import wireengine.core.physics.ITickable;
import wireengine.core.physics.PhysicsObject;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.IRenderable;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.geometry.GLMesh;
import wireengine.core.rendering.geometry.Model;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.window.InputHandler;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public class Level implements IRenderable, ITickable
{
    private Collider collider;
    private Model staticScene;
    private List<Model> dynamicScene;
    private List<Entity> entityList = new ArrayList<>();

    private boolean renderHitbox;

    public Level()
    {

    }

    @Override
    public void initRenderable()
    {
        this.dynamicScene = new ArrayList<>();
//
//        Model colliderModel = new Model(MeshHelper.createCuboid(new Vector3f(1.0F, 1.5F, 1.0F)).subdivideFaces(2), new Transformation().translate(new Vector3f(4.0F, 0.5F, 0.0F)));
//        this.colliderMesh = new ColliderMesh(colliderModel);
//        this.addDynamicMesh(colliderModel);

//        GLMesh mesh = MeshHelper.createPlane(15.0F, 15.0F, 15, 15, Axis.getyAxisOnly());
//        GLMesh mesh = MeshHelper.createUVSphere(1.0F, 20, 20);
//        this.staticScene = new Model(mesh, new Transformation());
//
//        this.colliders = Colliders.getMesh(mesh);

//        try
//        {
//            GLMesh mesh = MeshHelper.parseObj("res/level/testlevel/testlevel.obj");
////            GLMesh sceneMesh = MeshHelper.createPlane(10.0F, 10.0F, 10, 10, Axis.getWorldAxis());
//
//            this.staticScene = new Model(mesh, new Transformation());
//
//            List<Triangle> triangles = new ArrayList<>();
//            for (GLMesh.Face3 face : mesh.getFaces())
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
////            WireEngine.engine().getPhysicsEngine().addTickable(this.physicsObject);
//
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        if (this.entityList != null && this.entityList.size() > 0)
        {
            for (Entity entity : this.entityList)
            {
                entity.initRenderable();
            }
        }
    }

    @Override
    public void initTickable()
    {
        if (this.entityList != null && this.entityList.size() > 0)
        {
            for (Entity entity : this.entityList)
            {
                entity.initTickable();
            }
        }
    }

    @Override
    public void render(double delta, ShaderProgram shaderProgram)
    {
        this.renderFloorGrid(shaderProgram);

        if (this.staticScene != null)
        {
            this.staticScene.render(delta, shaderProgram);
        }

        if (this.dynamicScene != null)
        {
            for (Model model : this.dynamicScene)
            {
                if (model == null)
                {
                    continue;
                }

                model.render(delta, shaderProgram);
            }
        }

        if (this.entityList != null && !this.entityList.isEmpty())
        {
            for (Entity entity : this.entityList)
            {
                entity.render(delta, shaderProgram);
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

    @Override
    public void tick(double delta)
    {
        if (this.entityList != null && this.entityList.size() > 0)
        {
            for (Entity entity : this.entityList)
            {
                entity.tick(delta);
            }
        }
    }

    @Override
    public PhysicsObject getPhysicsObject()
    {
        return null;
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

    public void addStaticMesh(GLMesh mesh)
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

    public void addEntity(Entity entity)
    {
        if (entity != null)
        {
            this.entityList.add(entity);
            entity.exists = true;
        }
    }

    public void removeEntity(Entity entity)
    {
        if (entity != null)
        {
            this.entityList.remove(entity);
            entity.exists = false;
        }
    }

    public Model getSceneMesh()
    {
        return staticScene;
    }
}
