package wireengine.core.physics;

import wireengine.core.TickableThread;
import wireengine.core.WireEngine;
import wireengine.core.event.Event;
import wireengine.core.event.events.TickEvent;

/**
 * @author Kelan
 */
public class PhysicsEngine extends TickableThread
{
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
        WireEngine.engine().getEventHandler().postEvent(this, new TickEvent.PhysicsTickEvent(Event.State.POST, delta));
    }

    @Override
    public void cleanup()
    {
        WireEngine.getLogger().info("Cleaning up physics engine");
    }
}
