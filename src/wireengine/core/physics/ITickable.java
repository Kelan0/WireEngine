package wireengine.core.physics;

/**
 * @author Kelan
 */
public interface ITickable
{
    void initTickable();

    void tick(double delta);

    PhysicsObject getPhysicsObject();
}
