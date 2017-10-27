package wireengine.core.level;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.physics.PhysicsObject;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionHandler;
import wireengine.core.physics.collision.CollisionState;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.geometry.Mesh;
import wireengine.core.rendering.geometry.MeshHelper;
import wireengine.core.rendering.geometry.Model;
import wireengine.core.rendering.geometry.Transformation;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.window.InputHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public class Level
{
    private List<Triangle> triangles = new ArrayList<>();
    private Model staticScene;
    private List<Model> dynamicScene;
    private boolean renderHitbox;

    public Level()
    {

    }

    public void init()
    {
        this.dynamicScene = new ArrayList<>();
        try
        {
            Mesh sceneMesh = MeshHelper.parseObj("res/level/testlevel/testlevel.obj");

            this.staticScene = new Model(sceneMesh, new Transformation());

            for (Mesh.Face3 face : sceneMesh.getFaceList())
            {
                Vector3f p1 = face.getV1().getPosition();
                Vector3f p2 = face.getV2().getPosition();
                Vector3f p3 = face.getV3().getPosition();

                Triangle triangle = new Triangle(p1, p2, p3);
                this.triangles.add(triangle);
            }

//            WireEngine.engine().getPhysicsEngine().addPhysicsObject(this.physicsObject);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void cleanup()
    {

    }

    public void render(ShaderProgram shaderProgram, double delta, double time)
    {
        this.staticScene.render(shaderProgram);

        for (Model model : this.dynamicScene)
        {
            model.render(shaderProgram);
        }

        if (InputHandler.keyPressed(GLFW.GLFW_KEY_F2))
        {
            renderHitbox = !renderHitbox;
        }

        if (renderHitbox)
        {
            glEnable(GL_LINE_SMOOTH);
            glDisable(GL_DEPTH_TEST);
            DebugRenderer.getInstance().begin(GL_LINES);
            for (Triangle tri : this.triangles)
            {
                Vector3f p1 = tri.getPosition();
                Vector3f p2 = Vector3f.add(tri.getNormalAt(null), p1, null);

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

    public <C extends Collider<C>> void collideWith(PhysicsObject<C> object)
    {
        for (Triangle triangle : triangles)
        {
            CollisionHandler<Triangle, C> collisionHandler = CollisionHandler.getHandler(triangle, object.getCollider());
            collisionHandler.setPosition(triangle.getPosition(), object.getPosition());
            collisionHandler.setVelocity(new Vector3f(), object.getVelocity());
            collisionHandler.updateCollision();

            if (collisionHandler.getCollision().didHit)
            {
                CollisionState<Triangle, C> correctedState = collisionHandler.getCorrectedState();

                object.getVelocity().set(correctedState.bComponent.velocity);
            }
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

    public Model getSceneMesh()
    {
        return staticScene;
    }
}
