package wireengine.core.event.events;

import wireengine.core.event.Event;

/**
 * @author Kelan
 */
public abstract class TickEvent<T extends TickEvent<T>> extends Event<T>
{
    private double delta;

    private TickEvent(State state, double delta)
    {
        super(state);
        this.delta = delta;
    }

    public double getDelta()
    {
        return delta;
    }

    public static class PhysicsTickEvent extends TickEvent<PhysicsTickEvent>
    {
        //store information about the current physics situation, i.e. number of collisions etc.
        public PhysicsTickEvent(State state, double delta)
        {
            super(state, delta);
        }
    }

    public static class RenderTickEvent extends TickEvent<RenderTickEvent>
    {
        //store information about the current render state, i.e. current bound buffers??
        public RenderTickEvent(State state, double delta)
        {
            super(state, delta);
        }
    }
}
