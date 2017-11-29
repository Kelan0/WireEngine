package wireengine.core.entity;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.WireEngine;
import wireengine.core.physics.PhysicsObject;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.Colliders;
import wireengine.core.physics.collision.algorithm.Polytope;
import wireengine.core.physics.collision.algorithm.Simplex;
import wireengine.core.physics.collision.algorithm.SupportPoint;
import wireengine.core.rendering.Axis;
import wireengine.core.rendering.Camera;
import wireengine.core.rendering.renderer.ShaderProgram;
import wireengine.core.util.MathUtils;
import wireengine.core.window.InputHandler;
import wireengine.testgame.TestGame;

import static org.lwjgl.glfw.GLFW.*;
import static wireengine.core.util.Constants.HALF_PI;
import static wireengine.core.util.Constants.RADIANS;

/**
 * @author Kelan
 */
public class Player extends Entity
{
    private final Camera camera;
    private final float playerSize = 0.18F;
    private final float standHeight = 1.3F;
    private final float crouchHeight = 0.7F;
    private final float walkSpeed = 3.6F;
    private final float crouchSpeed = 1.2F;
    private float currentHeight = standHeight;
    private float currentSpeed = walkSpeed; //meters per second.
    private float mouseSpeed = 0.2F; //arbitrary value based on DPI. TODO, calculate a proper value for this base don mouse DPI.
    private boolean canFly = true;
    private EntitySelector entitySelector;
    private Polytope polytope = new Polytope();

    public Player()
    {
        super(WireEngine.engine().getGameSettings().getPlayerName());
        this.camera = new Camera(Axis.getyAxisOnly());
    }

    @Override
    public void initRenderable()
    {
        super.initRenderable();
        this.entitySelector = new EntitySelector(this, TestGame.getInstance().getLevel());
    }

    @Override
    public void initTickable()
    {
        //        new Ellipsoid(new Vector3f(0.0F, this.currentHeight * 0.5F + 0.05F, 0.0F), new Vector3f(playerSize, currentHeight * 0.5F + 0.1f, playerSize))
        Collider collider = Colliders.getEllipsoid(1, new Vector3f(playerSize, currentHeight * 0.5F + 0.1F, playerSize));
        this.physicsObject = new PhysicsObject(collider, 70.0F, null);
        this.physicsObject.setDampVelocity(true);
//        debug = new PolyTriangle(new Vector3f(10.0F, 0.0F, 1.0f), new Vector3f(10.0F, 3.0F, -4.0F), new Vector3f(12.0F, -1.0F, -4.5F));
//        this.test1 = new PhysicsObject(new PolyTriangle[]{new PolyTriangle(new Vector3f(-1.0F, 0.0F, 1.5F), new Vector3f(-0.8F, 1.8F, 0.6F), new Vector3f(0.2F, 0.7F, -1.4F))}, 70.0F);
//        this.test2 = new PhysicsObject(new PolyTriangle[]{new PolyTriangle(new Vector3f(0.0F, 0.5F, 1.2F), new Vector3f(0.5F, 0.8F, -1.7F), new Vector3f(-1.2F, 0.7F, 0.5F))}, 70.0F);

        Simplex simplex = new Simplex();
        simplex.push(new SupportPoint(new Vector3f(0.0F, 0.0F, 0.0F)));
        simplex.push(new SupportPoint(new Vector3f(1.0F, 0.0F, 0.0F)));
        simplex.push(new SupportPoint(new Vector3f(0.0F, 1.0F, 0.0F)));
        simplex.push(new SupportPoint(new Vector3f(0.0F, 0.0F, 1.0F)));
        this.polytope = new Polytope(simplex);

        System.out.println(this.polytope);
        this.validate();
    }

    public void render(double delta, ShaderProgram shaderProgram)
    {
        camera.setPosition(this.getHeadPosition());
        camera.render(shaderProgram);

        entitySelector.update(delta);
        entitySelector.render(delta, shaderProgram);

//        Vector3f offset = new Vector3f(0.0F, -4.0F, 0.0F);
//
//        if (WireEngine.engine().getRandom().nextFloat() < 0.06F)
//        {
//            float r = 3.0F;
//            Vector3f pos = new Vector3f();
//            pos.x = WireEngine.engine().getRandom().nextFloat() - 0.5F;
//            pos.y = WireEngine.engine().getRandom().nextFloat() - 0.5F;
//            pos.z = WireEngine.engine().getRandom().nextFloat() - 0.5F;
//
//            pos.normalise().scale(r);
//            this.polytope.addPoint(new SupportPoint(pos));
//            this.newPoint = new Vector3f(pos);
//        }
//
//        if (newPoint != null)
//        {
//            DebugRenderer.getInstance().begin(GL_POINTS);
//            DebugRenderer.getInstance().addColour(new Vector4f(1.0F, 0.0F, 0.0F, 1.0F));
//            DebugRenderer.getInstance().addVertex(Vector3f.add(offset, newPoint, null));
//            DebugRenderer.getInstance().end(shaderProgram);
//        }
//
//        DebugRenderer.getInstance().begin(GL_LINES);
//
//        for (Polytope.PolyTriangle tri : this.polytope.triangles)
//        {
//            DebugRenderer.getInstance().addColour(new Vector4f(1.0F, 0.0F, 0.0F, 1.0F));
//            DebugRenderer.getInstance().addVertex(Vector3f.add(offset, tri.vertices[0].get(), null));
//            DebugRenderer.getInstance().addColour(new Vector4f(0.0F, 1.0F, 0.0F, 1.0F));
//            DebugRenderer.getInstance().addVertex(Vector3f.add(offset, tri.vertices[1].get(), null));
//
//            DebugRenderer.getInstance().addColour(new Vector4f(1.0F, 0.0F, 0.0F, 1.0F));
//            DebugRenderer.getInstance().addVertex(Vector3f.add(offset, tri.vertices[1].get(), null));
//            DebugRenderer.getInstance().addColour(new Vector4f(0.0F, 1.0F, 0.0F, 1.0F));
//            DebugRenderer.getInstance().addVertex(Vector3f.add(offset, tri.vertices[2].get(), null));
//
//            DebugRenderer.getInstance().addColour(new Vector4f(1.0F, 0.0F, 0.0F, 1.0F));
//            DebugRenderer.getInstance().addVertex(Vector3f.add(offset, tri.vertices[2].get(), null));
//            DebugRenderer.getInstance().addColour(new Vector4f(0.0F, 1.0F, 0.0F, 1.0F));
//            DebugRenderer.getInstance().addVertex(Vector3f.add(offset, tri.vertices[0].get(), null));
//
//            Vector3f c = Vector3f.add(offset, MathUtils.averageVector3f(tri.vertices[0].get(), tri.vertices[1].get(), tri.vertices[2].get()), null);
//            Vector3f n = (Vector3f) new Vector3f(tri.normal).scale(0.2F);
//
//            DebugRenderer.getInstance().addColour(new Vector4f(0.0F, 0.0F, 1.0F, 1.0F));
//            DebugRenderer.getInstance().addVertex(c);
//            DebugRenderer.getInstance().addColour(new Vector4f(0.0F, 1.0F, 0.0F, 1.0F));
//            DebugRenderer.getInstance().addVertex(Vector3f.add(c, n, null));
//        }
//
//        DebugRenderer.getInstance().end(shaderProgram);
    }

    public void handleInput(double delta)
    {
        Vector3f toMove = new Vector3f();

        if (InputHandler.keyPressed(GLFW_KEY_P))
        {
            this.camera.setOrthoDirection(new Vector3f());
        }

        if (InputHandler.keyPressed(GLFW_KEY_UP))
        {
            this.camera.setOrthoDirection(new Vector3f(0.0F, 1.0F, 0.0F));
        }

        if (InputHandler.keyPressed(GLFW_KEY_DOWN))
        {
            this.camera.setOrthoDirection(new Vector3f(0.0F, -1.0F, 0.0F));
        }

        if (InputHandler.keyPressed(GLFW_KEY_LEFT))
        {
            if (InputHandler.keyPressed(GLFW_KEY_LEFT_SHIFT))
            {
                this.camera.setOrthoDirection(new Vector3f(0.0F, 0.0F, -1.0F));
            } else
            {
                this.camera.setOrthoDirection(new Vector3f(-1.0F, 0.0F, 0.0F));
            }
        }

        if (InputHandler.keyPressed(GLFW_KEY_RIGHT))
        {
            if (InputHandler.keyPressed(GLFW_KEY_LEFT_SHIFT))
            {
                this.camera.setOrthoDirection(new Vector3f(0.0F, 0.0F, 1.0F));
            } else
            {
                this.camera.setOrthoDirection(new Vector3f(1.0F, 0.0F, 0.0F));
            }
        }

        if (InputHandler.keyDown(GLFW_KEY_W))
        {
            toMove.z--;
        }

        if (InputHandler.keyDown(GLFW_KEY_A))
        {
            toMove.x--;
        }

        if (InputHandler.keyDown(GLFW_KEY_S))
        {
            toMove.z++;
        }

        if (InputHandler.keyDown(GLFW_KEY_D))
        {
            toMove.x++;
        }

        float numSeconds = 0.2F; //Number of seconds it takes to trasition between fully crouched/uncrouched.
        if (InputHandler.keyDown(GLFW_KEY_LEFT_CONTROL))
        {
            this.currentSpeed = crouchSpeed;
            this.currentHeight -= delta / numSeconds;
        } else
        {
            this.currentSpeed = walkSpeed;
            this.currentHeight += delta / numSeconds;
        }

        if (this.physicsObject.isOnGround())
        {
            if (InputHandler.keyDown(GLFW_KEY_SPACE))
            {
                float jumpHeight = 2.0F;
                this.physicsObject.getLinearVelocity().y = jumpHeight;
            }
        } else if (canFly)
        {
            if (InputHandler.keyDown(GLFW_KEY_SPACE))
            {
                toMove.y++;
            }

            if (InputHandler.keyDown(GLFW_KEY_LEFT_SHIFT))
            {
                toMove.y--;
            }
        }

        this.currentHeight = Math.max(this.currentHeight, this.crouchHeight);
        this.currentHeight = Math.min(this.currentHeight, this.standHeight);

        //The axis nullifies the y-axis, so the y direction of toMove has to be added separately.
        Vector3f.add(new Vector3f(0.0F, toMove.y, 0.0F), this.getAxis().transform(toMove, null), toMove);
        if (toMove.lengthSquared() > 0.0F)
        {
            toMove.normalise().scale(currentSpeed);
            this.physicsObject.applyLinearAcceleration(toMove);
        }

        if (InputHandler.isCursorGrabbed())
        {
            float pitch = (float) (RADIANS) * -mouseSpeed * InputHandler.cursorPosition().y;
            float yaw = (float) (RADIANS) * -mouseSpeed * InputHandler.cursorPosition().x;

            camera.rotatePitch(pitch);
            camera.rotateYaw(yaw);
        }
        Vector3f euler = MathUtils.quaternionToEuler(camera.getOrientation(), null);
        camera.rotatePitch((float) (euler.x > +HALF_PI ? +HALF_PI - euler.x : (euler.x < -HALF_PI ? -HALF_PI - euler.x : 0.0F)));
    }

    public synchronized Vector3f getFeetPosition()
    {
        Vector3f centre = this.physicsObject.getTransformation().getTranslation();

        return Vector3f.sub(centre, new Vector3f(0.0F, currentHeight * 0.5F, 0.0F), null);
    }

    public synchronized Vector3f getHeadPosition()
    {
        Vector3f centre = this.physicsObject.getTransformation().getTranslation();

        return Vector3f.add(centre, new Vector3f(0.0F, currentHeight * 0.5F, 0.0F), null);
    }

    public synchronized Vector3f getFeetPosition(float partialTicks)
    {
        float x = this.getFeetPosition().x + this.getVelocity().x * partialTicks;
        float y = this.getFeetPosition().y + this.getVelocity().y * partialTicks;
        float z = this.getFeetPosition().z + this.getVelocity().z * partialTicks;

        return new Vector3f(x, y, z);
    }

    public synchronized Vector3f getHeadPosition(float partialTicks)
    {
        float x = this.getHeadPosition().x + this.getVelocity().x * partialTicks;
        float y = this.getHeadPosition().y + this.getVelocity().y * partialTicks;
        float z = this.getHeadPosition().z + this.getVelocity().z * partialTicks;

        return new Vector3f(x, y, z);
    }

    public synchronized Vector3f getVelocity()
    {
        return this.physicsObject.getLinearVelocity();
    }

    public synchronized Vector3f getAcceleration()
    {
        return this.physicsObject.getLinearAcceleration();
    }

    public float getMass()
    {
        return this.physicsObject.getMass();
    }

    public synchronized boolean isOnGround()
    {
        return this.physicsObject.isOnGround();
    }

    public Axis getAxis()
    {
        return getAxis(true);
    }

    public Axis getAxis(boolean normalise)
    {
        Axis axis = camera.getAxis().scale(new Vector3f(1.0F, 0.0F, 1.0F), null);

        if (normalise)
        {
            axis.normalize(axis);
        }

        return axis;
    }

    public Vector3f getLookDirection()
    {
        return this.camera.getAxis().getZ().negate(null);
    }

    public Camera getCamera()
    {
        return camera;
    }

//    @Override
//    public void tick(double delta)
//    {
//        Level level = WireEngine.engine().getGame().getLevel();
//        this.colliders.setPosition(new Vector3f(this.position.x, this.position.y + (this.currentHeight + 0.1F) * 0.5F, this.position.z));
//        this.colliders.getRadius().y = (this.currentHeight + 0.1F) * 0.5F;
//
//        float friction = 8.6F;
//
//        if (moveDirection.lengthSquared() > 0.0)
//        {
//            this.linearVelocity.x += this.moveDirection.x * currentSpeed * delta;
//            this.linearVelocity.y += this.moveDirection.y * currentSpeed * delta;
//            this.linearVelocity.z += this.moveDirection.z * currentSpeed * delta;
//
//            float dot = (Vector3f.dot(this.linearVelocity.normalise(null), this.moveDirection.normalise(null)) + 1.0F) * 0.5F; // 1 if we are moving in the same direction, 0 if we are moving exactly opposite to the linearVelocity.
//            friction = MathUtils.interpolate(0.5F, friction, 1.0F - dot); //This avoids sliding when changing direction without stopping.
//            this.moveDirection = new Vector3f();
//        }
//
//        this.linearVelocity = level.collideWith(this.colliders, this.linearVelocity);
//
//        if (this.linearVelocity.y == 0.0F)
//        {
//            onGround = true;
//
//            linearVelocity.x /= (1.0F + friction * delta);
//            linearVelocity.y /= (1.0F + friction * delta);
//            linearVelocity.z /= (1.0F + friction * delta);
//        } else
//        {
//            onGround = false;
//        }
//    }
}

