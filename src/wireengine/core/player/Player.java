package wireengine.core.player;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.WireEngine;
import wireengine.core.physics.PhysicsObject;
import wireengine.core.physics.collision.Collider;
import wireengine.core.physics.collision.CollisionHandler;
import wireengine.core.physics.collision.Cylinder;
import wireengine.core.physics.collision.Triangle;
import wireengine.core.rendering.Axis;
import wireengine.core.rendering.Camera;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.util.MathUtils;
import wireengine.core.window.InputHandler;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static wireengine.core.physics.PhysicsEngine.GRAVITY;
import static wireengine.core.util.Constants.HALF_PI;
import static wireengine.core.util.Constants.RADIANS;

/**
 * @author Kelan
 */
public class Player implements PhysicsObject
{
    private final Camera camera;
    private final float playerSize = 0.5F;
    private final float standHeight = 1.3F;
    private final float crouchHeight = 0.7F;
    private final float walkSpeed = 3.8F;
    private final float crouchSpeed = 1.2F;
    private float currentHeight = standHeight;
    private float currentSpeed = walkSpeed; //meters per second.
    private float mouseSpeed = 0.6F; //arbitrary value based on DPI. TODO, calculate a proper value for this base don mouse DPI.
    private boolean onGround = false;

    private Cylinder collider; //Change this to some spheres. Cylinders are awful.
    private Vector3f moveDirection;
    private Vector3f position;
    private Vector3f velocity;
    private Vector3f acceleration;
    private float mass = 70.0F;

    public Player()
    {
        this.camera = new Camera(Axis.getyAxisOnly());
        this.moveDirection = new Vector3f();
        this.position = new Vector3f(0.0F, 0.0F, 0.0F);
        this.velocity = new Vector3f();
        this.acceleration = new Vector3f();

        this.collider = new Cylinder(this.getHeadPosition(), new Vector3f(0.0F, -1.0F, 0.0F), this.playerSize, this.currentHeight);
    }

    public void render(ShaderProgram shaderProgram)
    {
        camera.setPosition(this.getHeadPosition());
        camera.render(shaderProgram);

        collider.renderDebug(shaderProgram, new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));

        DebugRenderer.getInstance().begin(GL_LINES);
        DebugRenderer.getInstance().addColour(new Vector4f(0.0F, 0.0F, 1.0F, 1.0F));
        for (Collider c : WireEngine.engine().getGame().getLevel().getSceneCollider().getColliders())
        {
            Triangle triangle = (Triangle) c;

            CollisionHandler collision = this.collider.getCollision(triangle, 0.001F);

            if (collision.didHit())
            {
                System.out.println("Colliding with wall. " + collision.string);
                triangle.renderDebug(shaderProgram, new Vector4f(1.0F, 0.0F, 0.0F, 1.0F), GL_LINES);

                Vector3f[] hitdata = (Vector3f[]) collision.getHitData();

                for (int i = 0, j = hitdata.length - 1; i < hitdata.length; j = i++)
                {
                    System.out.println(hitdata[i] + " -> " + hitdata[j]);
                    DebugRenderer.getInstance().addVertex(hitdata[i]);
                    DebugRenderer.getInstance().addVertex(hitdata[j]);
                }
            }
        }
        DebugRenderer.getInstance().end(shaderProgram);
    }

    private Vector3f v = new Vector3f();

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

        float numSeconds = 0.3F; //Number of seconds it takes to trasition between fully crouched/uncrouched.
        if (InputHandler.keyDown(GLFW_KEY_LEFT_CONTROL))
        {
            this.currentSpeed = crouchSpeed;
            this.currentHeight -= delta / numSeconds;
        } else
        {
            this.currentSpeed = walkSpeed;
            this.currentHeight += delta / numSeconds;
        }

        if (InputHandler.keyDown(GLFW_KEY_LEFT_SHIFT) && isOnGround())
        {
            float jumpHeight = 3.0F;
            this.velocity.y = -jumpHeight * jumpHeight;
        }
        if (InputHandler.keyDown(GLFW_KEY_SPACE) && isOnGround())
        {
            float jumpHeight = 3.0F;
            this.velocity.y = jumpHeight * jumpHeight;
        }

        this.currentHeight = Math.max(this.currentHeight, this.crouchHeight);
        this.currentHeight = Math.min(this.currentHeight, this.standHeight);

        if (toMove.lengthSquared() > 0.01F)
        {
            this.getAxis().transform((Vector3f) toMove.normalise(null).scale(this.currentSpeed), this.moveDirection);
        }

        if (InputHandler.isCursorGrabbed())
        {
            float pitch = (float) (RADIANS) * -mouseSpeed * InputHandler.cursorPosition().y;
            float yaw = (float) (RADIANS) * -mouseSpeed * InputHandler.cursorPosition().x;

            camera.rotatePitch(pitch);
            camera.rotateYaw(yaw);
        }

        if (InputHandler.mouseButtonDown(GLFW_MOUSE_BUTTON_1))
        {
            if (v == null)
            {
                this.v = Vector3f.sub(this.collider.getPosition(), this.getHeadPosition(), null);
            }
            this.collider.setPosition(Vector3f.add(this.getHeadPosition(), v, null));
        }

        if (InputHandler.mouseButtonReleased(GLFW_MOUSE_BUTTON_1))
        {
            this.v = null;
        }

        Vector3f euler = MathUtils.quaternionToEuler(camera.getOrientation(), null);
        camera.rotatePitch((float) (euler.x > +HALF_PI ? +HALF_PI - euler.x : (euler.x < -HALF_PI ? -HALF_PI - euler.x : 0.0F)));
    }

    @Override
    public void tick(double delta)
    {
        if (onGround)
        {
            this.velocity.x += this.moveDirection.x * this.currentSpeed * delta;
            this.velocity.y += this.moveDirection.y * this.currentSpeed * delta;
            this.velocity.z += this.moveDirection.z * this.currentSpeed * delta;

            float g = Math.max(Math.abs(GRAVITY), 1.0F); //zero gravity not allowed. Divide-by-zero error.
            float drag = (float) Math.pow(0.01 / (this.mass * g), delta);
            this.velocity.x *= drag;
            this.velocity.y *= drag;
            this.velocity.z *= drag;
        } else
        {
            //air resistance.
        }

//        Vector3f pos = new Vector3f(this.position.x, this.position.y + (this.currentHeight + 0.075F) * 0.5F, this.position.z);
//        Vector3f.add(this.camera.getAxis().getBackward(), pos, pos);
//        this.collider.setPosition(pos);
//        this.collider.setHeight(this.currentHeight + 0.075F);

        this.moveDirection = new Vector3f();

//        this.colliding.clear();
        this.onGround = true;
//        for (Collider c : WireEngine.engine().getGame().getLevel().getSceneCollider().getColliders())
//        {
//            Triangle triangle = (Triangle) c;
//            CollisionHandler collision = this.collider.getCollision(triangle, 0.001F);
//
//            if (collision.didHit())
//            {
//                System.out.println("Colliding with wall.");
//                this.colliding.add(triangle);
//            }
//        }
    }

    @Override
    public synchronized Vector3f getPosition()
    {
        return this.position;
    }

    public synchronized Vector3f getHeadPosition()
    {
        return Vector3f.add(this.getPosition(), new Vector3f(0.0F, this.currentHeight, 0.0F), null);
    }

    public synchronized Vector3f getPosition(float partialTicks)
    {
        float x = this.getPosition().x + this.getVelocity().x * partialTicks;
        float y = this.getPosition().y + this.getVelocity().y * partialTicks;
        float z = this.getPosition().z + this.getVelocity().z * partialTicks;

        return new Vector3f(x, y, z);
    }

    public synchronized Vector3f getHeadPosition(float partialTicks)
    {
        return Vector3f.add(this.getPosition(partialTicks), new Vector3f(0.0F, this.currentHeight, 0.0F), null);
    }

    @Override
    public synchronized Vector3f getVelocity()
    {
        return this.velocity;
    }

    @Override
    public synchronized Vector3f getAcceleration()
    {
        return this.acceleration;
    }

    @Override
    public float getMass()
    {
        return mass;
    }

    @Override
    public boolean isStatic()
    {
        return false;
    }

    public synchronized boolean isOnGround()
    {
        return this.onGround;
    }

    public Collider getCollider()
    {
        return this.collider;
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
}

