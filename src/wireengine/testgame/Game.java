package wireengine.testgame;

import wireengine.core.event.EventListener;
import wireengine.core.event.events.TickEvent;
import wireengine.core.level.Level;
import wireengine.core.player.Player;

/**
 * @author Kelan
 */
public abstract class Game
{
    protected Player player;

    public Game(Player player)
    {
        this.player = player;
    }

    public Game()
    {
        this(new Player());
    }

    public abstract void preInit();

    public abstract void postInit();

    public abstract void onRender(TickEvent.RenderTickEvent event);

    public abstract void onPhysics(TickEvent.PhysicsTickEvent event);

    public abstract void handleInput(double delta);

    public abstract void cleanup();

    public abstract Level getLevel();

    public Player getPlayer()
    {
        return player;
    }

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
