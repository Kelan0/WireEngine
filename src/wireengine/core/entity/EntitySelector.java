package wireengine.core.entity;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wireengine.core.level.Level;
import wireengine.core.physics.collision.colliders.Ray;
import wireengine.core.rendering.renderer.ShaderProgram;
import wireengine.core.rendering.renderer.DebugRenderer;
import wireengine.core.util.MathUtils;
import wireengine.core.window.InputHandler;

import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glPointSize;

/**
 * @author Kelan
 */
public class EntitySelector
{
    private Entity selectedEntity;
    private Entity highlightedEntity;
    private Vector3f hitPos = new Vector3f();

    private Vector3f lastPosition;
    private Vector3f startOffset;
    private Quaternion startRotationHead;
    private Quaternion startRotationEntity;

    private Player player;
    private Level level;

    public EntitySelector(Player player, Level level)
    {
        this.player = player;
        this.level = level;
    }

    public void render(double delta, ShaderProgram shaderProgram)
    {
        if (this.highlightedEntity != null)
        {
            shaderProgram.setUniformVector4f("colourMultiplier", new Vector4f(1.0F, 0.5F, 0.5F, 1.0F));

            this.highlightedEntity.render(delta, shaderProgram);

            shaderProgram.setUniformVector4f("colourMultiplier", new Vector4f(1.0F, 1.0F, 1.0F, 1.0F));

            glPointSize(10.0F);
            DebugRenderer.getInstance().begin(GL_POINTS);
            DebugRenderer.getInstance().addColour(new Vector4f(1.0F, 0.0F, 0.0F, 1.0F));
            DebugRenderer.getInstance().addVertex(this.highlightedEntity.getPhysicsObject().getCollider().getFurthestVertex(this.player.getLookDirection()));
            DebugRenderer.getInstance().end(shaderProgram);
        }
    }

    public void update(double delta)
    {
        Vector3f headPosition = this.player.getHeadPosition();
        Quaternion headRotation = this.player.getCamera().getOrientation();

        if (InputHandler.mouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1))
        {
            if (this.selectedEntity != null)
            {
                this.level.checkLevelBoundaries(this.selectedEntity, delta);
                this.selectedEntity = null;
            }
        }

        if (InputHandler.mouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_1))
        {
            if (this.selectedEntity == null)
            {
                if (this.highlightedEntity != null)
                {
                    this.selectedEntity = this.highlightedEntity;
                    this.startOffset = Vector3f.sub(this.selectedEntity.transformation.getTranslation(), headPosition, null);
                    this.startRotationHead = new Quaternion(headRotation);
                    this.startRotationEntity = new Quaternion(this.selectedEntity.transformation.getRotation());
                }
            } else
            {
                Quaternion relativeHeadRotation = Quaternion.mulInverse(startRotationHead, headRotation, null);
                Vector3f position = Vector3f.add(headPosition, MathUtils.rotateVector3f(relativeHeadRotation, startOffset, null), null);
                Quaternion rotation = Quaternion.mul(this.startRotationEntity, relativeHeadRotation, null);

                this.lastPosition = new Vector3f(this.selectedEntity.transformation.getTranslation());
                this.selectedEntity.transformation.setRotation(rotation);
                this.selectedEntity.transformation.setTranslation(position);


                if (InputHandler.keyDown(GLFW.GLFW_KEY_R))
                {
                    this.startOffset = Vector3f.sub(this.selectedEntity.transformation.getTranslation(), headPosition, null);
                    this.startRotationHead = new Quaternion(headRotation);
                    this.startRotationEntity = new Quaternion();
                }

                if (this.level.checkLevelBoundaries(this.selectedEntity, delta))
                {
                    this.selectedEntity.physicsObject.getLinearVelocity().scale(0.0F);
                    this.selectedEntity.physicsObject.getLinearAcceleration().scale(0.0F);
                }

                Vector3f velocity = (Vector3f) Vector3f.sub(this.selectedEntity.transformation.getTranslation(), this.lastPosition, null).scale((float) (1.0F / delta));
                this.selectedEntity.physicsObject.getLinearVelocity().set(velocity);
            }
        } else
        {
            this.highlightedEntity = this.level.getEntity(new Ray(this.player.getHeadPosition(), this.player.getLookDirection()), this.hitPos);
        }
    }
}
