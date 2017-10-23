package wireengine.core.level;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.WireEngine;
import wireengine.core.physics.PhysicsObject;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionHandler;
import wireengine.core.physics.collision.CompositeCollider;
import wireengine.core.physics.collision.Triangle;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.geometry.*;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.window.InputHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Kelan
 */
public class Level implements PhysicsObject
{
    private CompositeCollider sceneCollider;
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
            this.sceneCollider = CollisionHandler.calculateCollider(sceneMesh);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
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

        if (InputHandler.keyPressed(GLFW.GLFW_KEY_F2))
        {
            renderHitbox = !renderHitbox;
        }

        if (renderHitbox)
        {
            glEnable(GL_LINE_SMOOTH);
            glDisable(GL_DEPTH_TEST);
            DebugRenderer.getInstance().begin(GL_LINES);
            for (Collider collider : this.sceneCollider.getColliders())
            {
                if (collider.colliderType.equals(Collider.ColliderType.TRIANGLE))
                {
                    Triangle tri = (Triangle) collider;

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
            }
            DebugRenderer.getInstance().end(shaderProgram);
            glEnable(GL_DEPTH_TEST);
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

    public CompositeCollider getSceneCollider()
    {
        return sceneCollider;
    }

    @Override
    public Vector3f getPosition()
    {
        return new Vector3f();
    }

    @Override
    public Vector3f getVelocity()
    {
        return new Vector3f();
    }

    @Override
    public Vector3f getAcceleration()
    {
        return new Vector3f();
    }

    @Override
    public float getMass()
    {
        return 1.0F; //Not used, but avoid divide-by-zero if it is for some reason.
    }

    @Override
    public void tick(double delta)
    {

    }

    @Override
    public boolean isStatic()
    {
        return true;
    }

    public Model getSceneMesh()
    {
        return staticScene;
    }
}
