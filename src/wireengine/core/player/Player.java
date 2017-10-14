package wireengine.core.player;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.rendering.Axis;
import wireengine.core.rendering.Camera;
import wireengine.core.rendering.ShaderProgram;
import wireengine.core.window.InputHandler;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Kelan
 */
public class Player
{
    private Camera camera;

    public Player()
    {
        this.camera = new Camera(Axis.getyAxisOnly());
    }

    public void render(ShaderProgram shader)
    {
        camera.render(shader);
    }

    public void handleInput(double delta)
    {
        Vector3f movement = new Vector3f();

        if (InputHandler.keyDown(GLFW_KEY_W))
        {
            movement.z++;
        }

        if (InputHandler.keyDown(GLFW_KEY_A))
        {
            movement.x++;
        }

        if (InputHandler.keyDown(GLFW_KEY_S))
        {
            movement.z--;
        }

        if (InputHandler.keyDown(GLFW_KEY_D))
        {
            movement.x--;
        }

        final float moveSpeed = (float) (delta * 3.5F);
        final float mouseSpeed = (float) (delta * 15.0F);

        camera.rotatePitch(-mouseSpeed * (float) Math.toRadians(InputHandler.cursorPosition().y));
        camera.rotateYaw(-mouseSpeed * (float) Math.toRadians(InputHandler.cursorPosition().x));

        if (movement.lengthSquared() > 0.0F)
        {
            movement.normalise(null);
            movement.x *= moveSpeed;
            movement.y *= moveSpeed;
            movement.z *= moveSpeed;

            camera.translateAxis(movement, camera.getAxis());
        }
    }
}

