package wireengine.core.level;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.WireEngine;
import wireengine.core.entity.Entity;
import wireengine.core.entity.EntityManager;
import wireengine.core.entity.Player;
import wireengine.core.physics.ITickable;
import wireengine.core.physics.PhysicsObject;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.algorithm.EPA;
import wireengine.core.physics.collision.algorithm.GJK;
import wireengine.core.physics.collision.colliders.Ray;
import wireengine.core.physics.collision.colliders.Triangle;
import wireengine.core.rendering.IRenderable;
import wireengine.core.rendering.geometry.MeshData;
import wireengine.core.rendering.geometry.Model;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.rendering.renderer.ShaderProgram;
import wireengine.core.util.MathUtils;
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

    private final List<Player> players = new ArrayList<>();
    private final List<Entity> entityList = new ArrayList<>();
    private final List<Entity> collidingEntities = new ArrayList<>();

    private EntityManager entityManager = new EntityManager();

    private Vector3f levelSize = new Vector3f(25.0F, 15.0F, 25.0F);
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
//            List<PolyTriangle> triangles = new ArrayList<>();
//            for (GLMesh.Face3 face : mesh.getFaces())
//            {
//                Vector3f p1 = face.getV1().getPosition();
//                Vector3f p2 = face.getV2().getPosition();
//                Vector3f p3 = face.getV3().getPosition();
//
//                PolyTriangle triangle = new PolyTriangle(p1, p2, p3);
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

        for (Player player : this.players)
        {
            player.initRenderable();
        }

        for (Entity entity : this.entityList)
        {
            entity.initRenderable();
        }
    }

    @Override
    public void initTickable()
    {
        for (Player player : this.players)
        {
            player.initTickable();
        }

        for (Entity entity : this.entityList)
        {
            entity.initTickable();
        }
    }

    @Override
    public void render(double delta, ShaderProgram shaderProgram)
    {
        for (Player player : this.players)
        {
            player.render(delta, shaderProgram);
        }

        this.entityManager.update();
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

        synchronized (this.collidingEntities)
        {
            for (Entity entity : this.collidingEntities)
            {
                entity.physicsObject.renderDebug(shaderProgram, new Vector4f(1.0F, 1.0F, 0.0F, 1.0F), GL_LINES);
            }
        }
    }

    @Override
    public void tick(double delta)
    {
        for (Player player : this.players)
        {
            player.tick(delta);
        }

        this.entityManager.update();

        List<Entity> entities = this.entityList;

        synchronized (this.collidingEntities)
        {
            for (Entity entity : entities)
            {
                entity.physicsObject.applyAcceleration(new Vector3f(0.0F, -9.807F, 0.0F));
                entity.tick(delta);
                checkLevelBoundaries(entity, delta);
            }

            this.collidingEntities.clear();
            for (int i = 0; i < entities.size(); i++)
            {
                for (int j = i + 1; j < entities.size(); j++)
                {
                    Entity entity1 = entities.get(i);
                    Entity entity2 = entities.get(j);

                    if (entity1.physicsObject.getAxisAlignedBB().intersects(entity2.physicsObject.getAxisAlignedBB()))
                    {
                        collidingEntities.add(entity1);
                        collidingEntities.add(entity2);

                        GJK gjk = GJK.checkCollision(entity1.physicsObject.getCollider(), entity2.physicsObject.getCollider());

                        if (gjk.didCollide)
                        {
                            EPA epa = EPA.getContact(gjk);

                            if (epa != null && epa.collisionPoint != null && epa.collisionNormal != null)
                            {
//                                float overlap = epa.collisionDepth;
//                                entity1.transformation.translate((Vector3f) new Vector3f(epa.collisionNormal).scale(+overlap));
//                                entity2.transformation.translate((Vector3f) new Vector3f(epa.collisionNormal).scale(-overlap));
                                correctPenetration(epa.collisionNormal, entity1, entity2, epa.collisionDepth);
                                applyLinearImpulse(epa.collisionNormal, 0.9F, entity1, entity2, delta);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean checkLevelBoundaries(Entity entity, double delta)
    {
        Vector3f[] directions = new Vector3f[]{new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0, 0, 1)};

        boolean corrected = false;
        for (int i = 0; i < 3; i++)
        {
            Vector3f dirPos = (Vector3f) new Vector3f(directions[i]).scale(+1.0F);
            Vector3f dirNeg = (Vector3f) new Vector3f(directions[i]).scale(-1.0F);
            Vector3f maxPoint = entity.physicsObject.getCollider().getFurthestVertex(dirPos);
            Vector3f minPoint = entity.physicsObject.getCollider().getFurthestVertex(dirNeg);

            float levelSize = MathUtils.getVectorElement(this.levelSize, i);
            float max = MathUtils.getVectorElement(maxPoint, i);
            float min = MathUtils.getVectorElement(minPoint, i);

            float e = 0.5F;

            if (max > +levelSize)
            {
                float overlap = levelSize - max;
                entity.transformation.translate((Vector3f) new Vector3f(dirPos).scale(overlap));
                applyLinearImpulse(dirNeg, e, entity, null, delta);
                corrected = true;
            }

            if (min < -levelSize)
            {
                float overlap = min + levelSize;
                entity.transformation.translate((Vector3f) new Vector3f(dirNeg).scale(overlap));
                applyLinearImpulse(dirPos, e, entity, null, delta);
                corrected = true;
            }
        }

        return corrected;
    }

    public void correctPenetration(Vector3f normal, Entity a, Entity b, float penetrationDepth)
    {
        float amount = 0.2F;
        float epsilon = 0.01F;

        float mass = a.physicsObject.getMass() + b.physicsObject.getMass();
        float im0 = 1.0F / a.physicsObject.getMass();
        float im1 = 1.0F / b.physicsObject.getMass();
        float r0 = a.physicsObject.getMass() / mass;
        float r1 = b.physicsObject.getMass() / mass;

        float correction = (Math.max(penetrationDepth - epsilon, 0.0F) / (im0 + im1)) * amount;

        a.transformation.translate((Vector3f) new Vector3f(normal).scale(+correction * r0));
        b.transformation.translate((Vector3f) new Vector3f(normal).scale(-correction * r1));
    }

    public void applyLinearImpulse(Vector3f normal, float e, Entity a, Entity b, double delta)
    {
        if (e >= 1.0F)
        {
            e = 1.0F;
        } else if (e <= 0.0F)
        {
            e = 0.0F;
        } else
        {
            e = (float) Math.sqrt(e);
        }

        if (a != null || b != null)
        {
            Vector3f v0 = a != null ? new Vector3f(a.physicsObject.getLinearVelocity()) : new Vector3f();
            Vector3f v1 = b != null ? new Vector3f(b.physicsObject.getLinearVelocity()) : new Vector3f();

            float m0 = a != null ? a.physicsObject.getMass() : Float.POSITIVE_INFINITY;
            float m1 = b != null ? b.physicsObject.getMass() : Float.POSITIVE_INFINITY; // infinite mass will not move. Java interprates div by inf to be 0

            float im0 = 1.0F / m0;
            float im1 = 1.0F / m1;

            Vector3f rv = Vector3f.sub(v0, v1, null);
            float dot = Vector3f.dot(rv, normal);

            if (dot <= 0.0F)
            {
                float j = (-(1.0F + e) * dot) / (im0 + im1);

                if (a != null)
                {
                    a.physicsObject.applyForce((Vector3f) new Vector3f(normal).scale((float) (+j / delta)));
                }
                if (b != null)
                {
                    b.physicsObject.applyForce((Vector3f) new Vector3f(normal).scale((float) (-j / delta)));
                }
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
        int sub = 3; //The number of times to subdivide.

        DebugRenderer.getInstance().begin(GL_LINES);

        for (int a = 1; a <= sub; a++)
        {
            int power = 1 << (a - 1);
            float incr = 1.0F / power; // 2^(-a)
            DebugRenderer.getInstance().setLighting(false);
            DebugRenderer.getInstance().addColour(new Vector4f(incr, incr, incr, 1.0F));

            int xdiv = (int) (levelSize.x / incr);
            for (int i = -xdiv; i <= xdiv; i++)
            {
                DebugRenderer.getInstance().addVertex(new Vector3f(-levelSize.z, -levelSize.y, i * incr));
                DebugRenderer.getInstance().addVertex(new Vector3f(+levelSize.z, -levelSize.y, i * incr));
            }

            int ydiv = (int) (levelSize.z / incr);
            for (int i = -ydiv; i <= ydiv; i++)
            {
                DebugRenderer.getInstance().addVertex(new Vector3f(i * incr, -levelSize.y, -levelSize.x));
                DebugRenderer.getInstance().addVertex(new Vector3f(i * incr, -levelSize.y, +levelSize.x));
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

    public void addStaticMesh(MeshData mesh)
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

    public void addPlayer(Player player)
    {
        this.players.add(player);
    }

    public void addEntity(Entity entity)
    {
        if (entity != null && !(entity instanceof Player))
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

    /**
     * Gets the first entity with the specified name identity in the list of entities.
     *
     * @param name The name of the entity to find.
     * @return The first entity found with a matching name, null if no entity was found.
     */
    public Entity getEntity(String name)
    {
        if (name != null)
        {
            for (Entity entity : entityList)
            {
                if (entity != null && entity.name != null && name.equals(entity.getName()))
                {
                    return entity;
                }
            }
        }

        return null;
    }

    /**
     * Finds the closest entity to the ray.
     *
     * @param ray The ray to find the closest entity to.
     * @return The entity that was found.
     */
    public Entity getEntity(Ray ray, Vector3f hitPos)
    {
        if (hitPos == null)
        {
            hitPos = new Vector3f();
        }

        Entity entity = null;

        float closestPoint = Float.MAX_VALUE;

        for (Entity e : this.entityList)
        {
            if (!e.getName().equals(WireEngine.engine().getGameSettings().getPlayerName()))
            {
                Collider collider = e.getPhysicsObject().getCollider();

                if (collider != null)
                {
                    Vector3f point = new Vector3f();
                    if (collider.rayIntersects(ray, point, true))
                    {
                        float distanceSq = MathUtils.distanceSquared(ray.origin, point);

                        if (distanceSq < closestPoint)
                        {
                            closestPoint = distanceSq;
                            entity = e;
                            hitPos.set(point);
                        }
                    }
                }
            }
        }

        return entity;
    }

    public List<Entity> getEntities()
    {
        return this.entityList;
    }

    public Model getSceneMesh()
    {
        return staticScene;
    }
}
