package wireengine.core.physics;

import org.lwjgl.util.vector.Vector3f;
import wireengine.core.TickableThread;
import wireengine.core.WireEngine;
import wireengine.core.event.Event;
import wireengine.core.event.events.TickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class PhysicsEngine extends TickableThread
{
    public static final float GRAVITY = 9.807F;

    private List<IPhysicsObject> tickable = new ArrayList<>();

    public PhysicsEngine()
    {
        super("PHYSICS-THREAD");
    }

    @Override
    public int getMaxTickrate()
    {
        return WireEngine.engine().getGameSettings().getMaxTPS();
    }

    @Override
    public void init()
    {
        WireEngine.getLogger().info("Initializing physics engine");
    }

    @Override
    public void tick(double delta)
    {
        WireEngine.engine().getEventHandler().postEvent(this, new TickEvent.PhysicsTickEvent(Event.State.PRE, delta));

        for (IPhysicsObject object : this.tickable)
        {

//            Vector3f startVelocity = new Vector3f(object.getVelocity());
//            object.getVelocity().x += acceleration.x;
//            object.getVelocity().y += acceleration.y;
//            object.getVelocity().z += acceleration.z;
//
//            //Apply proper acceleration over small time step.
//            object.getPosition().x += object.getVelocity().x * delta + 0.5F * object.getAcceleration().x * delta * delta;
//            object.getPosition().y += object.getVelocity().y * delta + 0.5F * object.getAcceleration().y * delta * delta;
//            object.getPosition().z += object.getVelocity().z * delta + 0.5F * object.getAcceleration().z * delta * delta;
//
//            object.tick(delta);
//            Vector3f endVelocity = new Vector3f(object.getVelocity());
//            object.getAcceleration().set(Vector3f.sub(endVelocity, startVelocity, null));
//
//            if (object instanceof Player)// && ((Player) object).isOnGround())
//            {
//                System.out.println(object.getAcceleration());
//            }

//            object.getAcceleration().y = -GRAVITY;
//            object.getVelocity().x += object.getAcceleration().x * delta;
//            object.getVelocity().y += object.getAcceleration().y * delta;
//            object.getVelocity().z += object.getAcceleration().z * delta;
//
//            object.tick(delta);
//
//            object.getPosition().x += object.getVelocity().x * delta;// + 0.5F * object.getAcceleration().x * delta * delta;
//            object.getPosition().y += object.getVelocity().y * delta;// + 0.5F * object.getAcceleration().y * delta * delta;
//            object.getPosition().z += object.getVelocity().z * delta;// + 0.5F * object.getAcceleration().z * delta * delta;

            object.applyAcceleration(new Vector3f(0.0f, -GRAVITY, 0.0F));
            object.tick(delta);
        }

        WireEngine.engine().getEventHandler().postEvent(this, new TickEvent.PhysicsTickEvent(Event.State.POST, delta));
    }

    @Override
    public void cleanup()
    {
        WireEngine.getLogger().info("Cleaning up physics engine");
    }

    public boolean addPhysicsObject(IPhysicsObject object)
    {
        if (object == null || containsPhysicsObject(object))
        {
            return false;
        }

        return this.tickable.add(object);
    }

    public boolean removePhysicsObject(IPhysicsObject object)
    {
        return this.tickable.remove(object);
    }

    public boolean containsPhysicsObject(IPhysicsObject object)
    {
        return this.tickable.contains(object);
    }
}
