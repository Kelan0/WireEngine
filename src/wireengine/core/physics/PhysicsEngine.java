package wireengine.core.physics;

import org.lwjgl.util.vector.Matrix3f;
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
            object.tick(1.0 / this.getMaxTickrate());
            // Physics simulation should not have a variable tickrate, so we assume that the tickrate is stable and constant here. This
            // has the side effect of noticeable choppiness and slowdowns if the real tickrate does dip too much, but it avoids things like
            // runaway reactions, where an object gains energy after every collision.
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
