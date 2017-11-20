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
    public static final float GRAVITY = 0.0F;//9.807F;

    private List<ITickable> tickList = new ArrayList<>();

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

        for (ITickable object : this.tickList)
        {
            object.initTickable();
        }
    }

    @Override
    public void tick(double delta)
    {
        WireEngine.engine().getEventHandler().postEvent(this, new TickEvent.PhysicsTickEvent(Event.State.PRE, delta));

        for (ITickable object : this.tickList)
        {
            if (object.getPhysicsObject() != null)
            {
                object.getPhysicsObject().applyAcceleration(new Vector3f(0.0f, -GRAVITY, 0.0F));
            }

            object.tick(delta);
        }

        WireEngine.engine().getEventHandler().postEvent(this, new TickEvent.PhysicsTickEvent(Event.State.POST, delta));
    }

    @Override
    public void cleanup()
    {
        WireEngine.getLogger().info("Cleaning up physics engine");
    }

    public boolean addTickable(ITickable object)
    {
        if (object == null || containsTickable(object))
        {
            return false;
        }

        return this.tickList.add(object);
    }

    public boolean removeTickable(ITickable object)
    {
        return this.tickList.remove(object);
    }

    public boolean containsTickable(ITickable object)
    {
        return this.tickList.contains(object);
    }
}
