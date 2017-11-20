package wireengine.testgame;

import wireengine.core.event.EventListener;
import wireengine.core.event.events.TickEvent;

/**
 * @author Kelan
 */
public abstract class Game
{
    public abstract void preInit();

    public abstract void postInit();

    public abstract void onRender(TickEvent.RenderTickEvent event);

    public abstract void onPhysics(TickEvent.PhysicsTickEvent event);

    public abstract void handleInput(double delta);

    public abstract void cleanup();

    @EventListener
    public final void renderEvent(TickEvent.RenderTickEvent event)
    {
        this.onRender(event);
    }

    @EventListener
    public final void physicsEvent(TickEvent.PhysicsTickEvent event)
    {
        this.onPhysics(event);
    }
}
